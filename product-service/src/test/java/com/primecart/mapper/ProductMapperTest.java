package com.primecart.mapper;

import com.primecart.dto.request.CreateProductRequest;
import com.primecart.dto.request.UpdateProductRequest;
import com.primecart.dto.response.ProductResponse;
import com.primecart.entity.Brand;
import com.primecart.entity.Category;
import com.primecart.entity.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ProductMapperTest {

    private final ProductMapper productMapper = new ProductMapper();

    @Test
    void shouldMapCreateRequestToEntity() {
        Category category = Category.builder().id(10L).name("Phones").build();
        Brand brand = Brand.builder().id(20L).name("Prime").build();
        CreateProductRequest request = new CreateProductRequest(
                "Phone",
                "Flagship phone",
                new BigDecimal("999.99"),
                "https://example.com/phone.jpg",
                "PHONE-001",
                15,
                category.getId(),
                brand.getId(),
                true);

        Product product = productMapper.toEntity(request, category, brand);

        assertThat(product.getName()).isEqualTo(request.name());
        assertThat(product.getDescription()).isEqualTo(request.description());
        assertThat(product.getPrice()).isEqualByComparingTo(request.price());
        assertThat(product.getImageUrl()).isEqualTo(request.imageUrl());
        assertThat(product.getSku()).isEqualTo(request.sku());
        assertThat(product.getStock()).isEqualTo(request.stock());
        assertThat(product.getCategory()).isSameAs(category);
        assertThat(product.getBrand()).isSameAs(brand);
        assertThat(product.getActive()).isTrue();
    }

    @Test
    void shouldUpdateOnlyMutableProductFields() {
        Product product = Product.builder()
                                 .id(1L)
                                 .sku("PHONE-001")
                                 .stock(15)
                                 .build();
        Category category = Category.builder().id(11L).name("Tablets").build();
        Brand brand = Brand.builder().id(21L).name("Updated Brand").build();
        UpdateProductRequest request = new UpdateProductRequest(
                "Updated Phone",
                "Updated description",
                new BigDecimal("1099.99"),
                "https://example.com/updated.jpg",
                category.getId(),
                brand.getId(),
                false);

        productMapper.updateEntity(product, request, category, brand);

        assertThat(product.getName()).isEqualTo(request.name());
        assertThat(product.getDescription()).isEqualTo(request.description());
        assertThat(product.getPrice()).isEqualByComparingTo(request.price());
        assertThat(product.getImageUrl()).isEqualTo(request.imageUrl());
        assertThat(product.getCategory()).isSameAs(category);
        assertThat(product.getBrand()).isSameAs(brand);
        assertThat(product.getActive()).isFalse();
        assertThat(product.getSku()).isEqualTo("PHONE-001");
        assertThat(product.getStock()).isEqualTo(15);
    }

    @Test
    void shouldMapEntityToResponse() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 7, 25, 10, 0);
        LocalDateTime updatedAt = createdAt.plusHours(1);
        Product product = Product.builder()
                                 .id(1L)
                                 .sku("PHONE-001")
                                 .stock(15)
                                 .name("Phone")
                                 .description("Flagship phone")
                                 .price(new BigDecimal("999.99"))
                                 .imageUrl("https://example.com/phone.jpg")
                                 .category(Category.builder().id(10L).name("Phones").build())
                                 .brand(Brand.builder().id(20L).name("Prime").build())
                                 .active(true)
                                 .createdAt(createdAt)
                                 .updatedAt(updatedAt)
                                 .build();

        ProductResponse response = productMapper.toResponse(product);

        assertThat(response.id()).isEqualTo(product.getId());
        assertThat(response.sku()).isEqualTo(product.getSku());
        assertThat(response.stock()).isEqualTo(product.getStock());
        assertThat(response.name()).isEqualTo(product.getName());
        assertThat(response.description()).isEqualTo(product.getDescription());
        assertThat(response.price()).isEqualByComparingTo(product.getPrice());
        assertThat(response.imageUrl()).isEqualTo(product.getImageUrl());
        assertThat(response.categoryId()).isEqualTo(10L);
        assertThat(response.categoryName()).isEqualTo("Phones");
        assertThat(response.brandId()).isEqualTo(20L);
        assertThat(response.brandName()).isEqualTo("Prime");
        assertThat(response.active()).isTrue();
        assertThat(response.createdAt()).isEqualTo(createdAt);
        assertThat(response.updatedAt()).isEqualTo(updatedAt);
    }
}
