package com.primecart.controller;

import com.primecart.dto.request.CreateProductRequest;
import com.primecart.dto.request.UpdateProductRequest;
import com.primecart.dto.response.ProductResponse;
import com.primecart.metrics.ProductMetrics;
import com.primecart.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;
    @Mock
    private ProductMetrics productMetrics;

    @InjectMocks
    private ProductController productController;

    @Test
    void shouldCreateProductAndIncrementMetric() {
        CreateProductRequest request = createRequest();
        ProductResponse product = response();
        when(productService.createProduct(request)).thenReturn(product);

        ResponseEntity<ProductResponse> result = productController.createProduct(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isSameAs(product);
        verify(productMetrics).incrementProductCreated();
    }

    @Test
    void shouldUpdateProductAndIncrementMetric() {
        UpdateProductRequest request = updateRequest();
        ProductResponse product = response();
        when(productService.updateProduct(1L, request)).thenReturn(product);

        ResponseEntity<ProductResponse> result = productController.updateProduct(1L, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isSameAs(product);
        verify(productMetrics).incrementProductUpdated();
    }

    @Test
    void shouldGetProductAndIncrementViewMetric() {
        ProductResponse product = response();
        when(productService.getProductById(1L)).thenReturn(product);

        ResponseEntity<ProductResponse> result = productController.getProductById(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isSameAs(product);
        verify(productMetrics).incrementProductView();
    }

    @Test
    void shouldGetActiveProductsAndIncrementMetric() {
        List<ProductResponse> products = List.of(response());
        when(productService.getActiveProducts()).thenReturn(products);

        ResponseEntity<List<ProductResponse>> result = productController.getActiveProducts();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isSameAs(products);
        verify(productMetrics).incrementActiveProductsView();
    }

    @Test
    void shouldDeleteProductAndIncrementMetric() {
        ResponseEntity<Void> result = productController.deleteProduct(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(result.getBody()).isNull();
        verify(productService).deleteProduct(1L);
        verify(productMetrics).incrementProductDeleted();
    }

    @Test
    void shouldDelegateProductSearch() {
        Page<ProductResponse> products = new PageImpl<>(List.of(response()));
        when(productService.getProducts(10L, 20L, true, "phone", 0, 20, "id", "asc"))
                .thenReturn(products);

        ResponseEntity<Page<ProductResponse>> result = productController.getProducts(
                10L,
                20L,
                true,
                "phone",
                0,
                20,
                "id",
                "asc");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isSameAs(products);
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
