package com.primecart.service;

import com.primecart.dto.request.CreateProductRequest;
import com.primecart.dto.request.UpdateProductRequest;
import com.primecart.dto.response.ProductResponse;
import com.primecart.entity.Brand;
import com.primecart.entity.Category;
import com.primecart.entity.Product;
import com.primecart.exception.DuplicateResourceException;
import com.primecart.exception.ResourceNotFoundException;
import com.primecart.mapper.ProductMapper;
import com.primecart.messaging.events.ProductCreatedEvent;
import com.primecart.metrics.ProductMetrics;
import com.primecart.repository.BrandRepository;
import com.primecart.repository.CategoryRepository;
import com.primecart.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BrandRepository brandRepository;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private ProductMetrics productMetrics;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void shouldCreateProductAndPublishEvent() {
        CreateProductRequest request = createRequest();
        Category category = category();
        Brand brand = brand();
        Product product = product();
        ProductResponse response = response();

        when(productRepository.existsBySku(request.sku())).thenReturn(false);
        when(categoryRepository.findById(request.categoryId())).thenReturn(Optional.of(category));
        when(brandRepository.findById(request.brandId())).thenReturn(Optional.of(brand));
        when(productMapper.toEntity(request, category, brand)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(response);

        ProductResponse result = productService.createProduct(request);

        assertThat(result).isSameAs(response);
        verify(productRepository).save(product);

        ArgumentCaptor<ProductCreatedEvent> eventCaptor = ArgumentCaptor.forClass(ProductCreatedEvent.class);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().productId()).isEqualTo(product.getId());
        assertThat(eventCaptor.getValue().sku()).isEqualTo(product.getSku());
        assertThat(eventCaptor.getValue().eventType()).isEqualTo("PRODUCT_CREATED");
    }

    @Test
    void shouldRejectDuplicateSku() {
        CreateProductRequest request = createRequest();
        when(productRepository.existsBySku(request.sku())).thenReturn(true);

        assertThatThrownBy(() -> productService.createProduct(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining(request.sku());

        verify(productRepository, never()).save(any());
        verify(applicationEventPublisher, never()).publishEvent(any());
    }

    @Test
    void shouldReturnProductById() {
        Product product = product();
        ProductResponse response = response();
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(response);

        assertThat(productService.getProductById(product.getId())).isSameAs(response);
    }

    @Test
    void shouldIncrementNotFoundMetricWhenProductDoesNotExist() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(productMetrics).incrementProductNotFound();
    }

    @Test
    void shouldUpdateProduct() {
        Product product = product();
        Category category = category();
        Brand brand = brand();
        UpdateProductRequest request = updateRequest();
        ProductResponse response = response();
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(categoryRepository.findById(request.categoryId())).thenReturn(Optional.of(category));
        when(brandRepository.findById(request.brandId())).thenReturn(Optional.of(brand));
        when(productMapper.toResponse(product)).thenReturn(response);

        ProductResponse result = productService.updateProduct(product.getId(), request);

        assertThat(result).isSameAs(response);
        verify(productMapper).updateEntity(product, request, category, brand);
    }

    @Test
    void shouldDeleteExistingProduct() {
        Product product = product();
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        productService.deleteProduct(product.getId());

        verify(productRepository).delete(product);
    }

    @Test
    void shouldReturnActiveProducts() {
        Product product = product();
        ProductResponse response = response();
        when(productRepository.findByActive(true)).thenReturn(List.of(product));
        when(productMapper.toResponse(product)).thenReturn(response);

        assertThat(productService.getActiveProducts()).containsExactly(response);
    }

    @Test
    void shouldFilterProductsByCategoryWithRequestedSort() {
        Product product = product();
        ProductResponse response = response();
        Page<Product> products = new PageImpl<>(List.of(product));
        when(productRepository.findByCategoryId(any(), any(Pageable.class))).thenReturn(products);
        when(productMapper.toResponse(product)).thenReturn(response);

        Page<ProductResponse> result = productService.getProducts(
                10L,
                null,
                null,
                null,
                0,
                20,
                "price",
                "desc");

        assertThat(result.getContent()).containsExactly(response);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(productRepository).findByCategoryId(org.mockito.ArgumentMatchers.eq(10L), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getSort().getOrderFor("price").isDescending()).isTrue();
    }

    @Test
    void shouldRejectUnsupportedSortField() {
        assertThatThrownBy(() -> productService.getProducts(
                null,
                null,
                null,
                null,
                0,
                20,
                "unsupported",
                "asc"))
                .isInstanceOfSatisfying(ResponseStatusException.class, exception -> {
                    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(exception.getReason()).contains("unsupported");
                });

        verify(productRepository, never()).findAll(any(Pageable.class));
    }

    private CreateProductRequest createRequest() {
        return new CreateProductRequest(
                "Phone",
                "Flagship phone",
                new BigDecimal("999.99"),
                "https://example.com/phone.jpg",
                "PHONE-001",
                15,
                10L,
                20L,
                true);
    }

    private UpdateProductRequest updateRequest() {
        return new UpdateProductRequest(
                "Updated Phone",
                "Updated description",
                new BigDecimal("1099.99"),
                "https://example.com/updated.jpg",
                10L,
                20L,
                true);
    }

    private Category category() {
        return Category.builder().id(10L).name("Phones").build();
    }

    private Brand brand() {
        return Brand.builder().id(20L).name("Prime").build();
    }

    private Product product() {
        return Product.builder()
                      .id(1L)
                      .sku("PHONE-001")
                      .stock(15)
                      .name("Phone")
                      .description("Flagship phone")
                      .price(new BigDecimal("999.99"))
                      .category(category())
                      .brand(brand())
                      .active(true)
                      .build();
    }

    private ProductResponse response() {
        return new ProductResponse(
                1L,
                "PHONE-001",
                15,
                "Phone",
                "Flagship phone",
                new BigDecimal("999.99"),
                "https://example.com/phone.jpg",
                10L,
                "Phones",
                20L,
                "Prime",
                true,
                null,
                null);
    }
}
