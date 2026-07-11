package com.primecart.repository;

import com.primecart.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Used by /active endpoint
    List<Product> findByActive(Boolean active);

    // Used by filtered + paginated endpoint
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Product> findByBrandId(Long brandId, Pageable pageable);

    Page<Product> findByActive(Boolean active, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
}