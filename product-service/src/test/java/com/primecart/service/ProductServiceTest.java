package com.primecart.service;

import com.primecart.dto.request.CreateProductRequest;
import com.primecart.dto.request.UpdateProductRequest;
import com.primecart.dto.response.ProductResponse;
import com.primecart.entity.Brand;
import com.primecart.entity.Category;
import com.primecart.entity.Product;
import com.primecart.exception.ResourceNotFoundException;
import com.primecart.mapper.ProductMapper;
import com.primecart.repository.BrandRepository;
import com.primecart.repository.CategoryRepository;
import com.primecart.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BrandRepository brandRepository;

    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @Captor
    private ArgumentCaptor<Product> productCaptor;

    @BeforeEach
    void setUp() {
        productMapper = new ProductMapper();
        // Manually inject mapper into service
        productService = new ProductServiceImpl(productRepository, categoryRepository, brandRepository, productMapper);
    }

    @Test
    void createProduct_savesAndReturnsResponse() {
        CreateProductRequest request = new CreateProductRequest(
                "Name", "desc", new BigDecimal("5.00"), null, 1L, 2L, true
        );

        Category cat = Category.builder().id(1L).name("Cat").build();
        Brand brand = Brand.builder().id(2L).name("Brand").build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(cat));
        when(brandRepository.findById(2L)).thenReturn(Optional.of(brand));

        Product saved = Product.builder()
                .id(100L)
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .category(cat)
                .brand(brand)
                .active(request.active())
                .sku("SKU")
                .stock(10)
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(saved);

        ProductResponse resp = productService.createProduct(request);

        assertThat(resp.id()).isEqualTo(100L);
        verify(productRepository).save(productCaptor.capture());
        Product captured = productCaptor.getValue();
        assertThat(captured.getName()).isEqualTo("Name");
    }

    @Test
    void getProductById_whenNotFound_throws() {
        when(productRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(5L));
    }

    @Test
    void getProducts_withCategory_delegatesToRepository() {
        Category cat = Category.builder().id(2L).name("Cat").build();
        Brand b = Brand.builder().id(3L).name("B").build();
        Product p = Product.builder().id(1L).name("p").price(new BigDecimal("1.00")).sku("s").stock(1).active(true)
                .category(cat).brand(b).build();
        Page<Product> page = new PageImpl<>(List.of(p));

        when(productRepository.findByCategoryId(eq(2L), any(Pageable.class))).thenReturn(page);

        Page<ProductResponse> result = productService.getProducts(2L, null, null, null,0,10,"id","asc");

        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(productRepository).findByCategoryId(eq(2L), any(Pageable.class));
    }
}

