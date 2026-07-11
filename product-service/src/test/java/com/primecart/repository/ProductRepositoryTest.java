package com.primecart.repository;

import com.primecart.entity.Brand;
import com.primecart.entity.Category;
import com.primecart.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Test
    void findByActive_and_findByCategory_and_findByNameContaining() {
        Category cat = Category.builder().name("Cat").description("d").build();
        Category savedCat = categoryRepository.save(cat);

        Brand brand = Brand.builder().name("Brand").description("d").build();
        Brand savedBrand = brandRepository.save(brand);

        Product p1 = Product.builder()
                .name("Apple")
                .description("fresh")
                .price(new BigDecimal("2.00"))
                .sku("S1")
                .stock(5)
                .active(true)
                .category(savedCat)
                .brand(savedBrand)
                .build();

        Product p2 = Product.builder()
                .name("Banana")
                .description("ripe")
                .price(new BigDecimal("1.00"))
                .sku("S2")
                .stock(3)
                .active(false)
                .category(savedCat)
                .brand(savedBrand)
                .build();

        productRepository.save(p1);
        productRepository.save(p2);

        var activeList = productRepository.findByActive(true);
        // ensure at least one of the active products is the one we just saved
        assertThat(activeList).isNotEmpty();
        assertThat(activeList.stream().anyMatch(p -> "S1".equals(p.getSku()))).isTrue();

        Page<Product> byCategory = productRepository.findByCategoryId(savedCat.getId(), PageRequest.of(0, 10));
        assertThat(byCategory.getTotalElements()).isGreaterThanOrEqualTo(2);

        Page<Product> byName = productRepository.findByNameContainingIgnoreCase("app", PageRequest.of(0, 10));
        assertThat(byName.getTotalElements()).isGreaterThanOrEqualTo(1);
    }
}

