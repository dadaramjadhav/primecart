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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductMapper productMapper;

    @CacheEvict(value = "allProducts", allEntries = true)
    @Override
    public ProductResponse createProduct(CreateProductRequest request) {

        log.info("Creating product: {}", request.name());

        Category category = categoryRepository.findById(request.categoryId())
                                              .orElseThrow(() -> {
                                                  log.warn("Category not found with id: {}", request.categoryId());
                                                  return new ResourceNotFoundException(
                                                          "Category not found with id: " + request.categoryId());
                                              });

        Brand brand = brandRepository.findById(request.brandId())
                                     .orElseThrow(() -> {
                                         log.warn("Brand not found with id: {}", request.brandId());
                                         return new ResourceNotFoundException(
                                                 "Brand not found with id: " + request.brandId());
                                     });

        Product product = productMapper.toEntity(request, category, brand);

        Product savedProduct = productRepository.save(product);

        log.info("Product created with id: {}", savedProduct.getId());

        return productMapper.toResponse(savedProduct);
    }

    @Cacheable(value = "products", key = "#id")
    @Override
    public ProductResponse getProductById(Long id) {

        log.info("Fetching product with id: {}", id);

        Product product = productRepository.findById(id)
                                           .orElseThrow(() -> {
                                               log.warn("Product not found with id: {}", id);
                                               return new ResourceNotFoundException("Product not found with id: " + id);
                                           });

        log.info("Product fetched successfully with id: {}", id);

        return productMapper.toResponse(product);
    }

    @Override
    public List<ProductResponse> getActiveProducts() {
        return List.of();
    }

    @Caching(evict = {
            @CacheEvict(value = "products", key = "#id"),
            @CacheEvict(value = "allProducts", allEntries = true)
    })
    @Override
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {

        log.info("Updating product with id: {}", id);

        Product product = productRepository.findById(id)
                                           .orElseThrow(() -> {
                                               log.warn("Product not found with id: {}", id);
                                               return new ResourceNotFoundException("Product not found with id: " + id);
                                           });

        Category category = categoryRepository.findById(request.categoryId())
                                              .orElseThrow(() -> {
                                                  log.warn("Category not found with id: {}", request.categoryId());
                                                  return new ResourceNotFoundException(
                                                          "Category not found with id: " + request.categoryId());
                                              });

        Brand brand = brandRepository.findById(request.brandId())
                                     .orElseThrow(() -> {
                                         log.warn("Brand not found with id: {}", request.brandId());
                                         return new ResourceNotFoundException(
                                                 "Brand not found with id: " + request.brandId());
                                     });

        productMapper.updateEntity(product, request, category, brand);

        Product updatedProduct = productRepository.save(product);

        log.info("Product updated successfully with id: {}", updatedProduct.getId());

        return productMapper.toResponse(updatedProduct);
    }

    @Caching(evict = {
            @CacheEvict(value = "products", key = "#id"),
            @CacheEvict(value = "allProducts", allEntries = true)
    })
    @Override
    public void deleteProduct(Long id) {

        log.info("Deleting product with id: {}", id);

        Product product = productRepository.findById(id)
                                           .orElseThrow(() -> {
                                               log.warn("Product not found with id: {}", id);
                                               return new ResourceNotFoundException("Product not found with id: " + id);
                                           });

        productRepository.delete(product);

        log.info("Product deleted successfully with id: {}", id);
    }

    @Cacheable(
            value = "allProducts"
//            key = "{#category,#brand,#active,#keyword,#page,#size,#sortBy,#direction}"
    )
    @Override
    public Page<ProductResponse> getProducts(
            Long category,
            Long brand,
            Boolean active,
            String keyword,
            int page,
            int size,
            String sortBy,
            String direction) {

        log.info("Fetching products with filters - category={}, brand={}, active={}, keyword={}",
                category, brand, active, keyword);

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> productPage;

        if (category != null) {
            productPage = productRepository.findByCategoryId(category, pageable);
        } else if (brand != null) {
            productPage = productRepository.findByBrandId(brand, pageable);
        } else if (active != null) {
            productPage = productRepository.findByActive(active, pageable);
        } else if (keyword != null && !keyword.isBlank()) {
            productPage = productRepository.findByNameContainingIgnoreCase(keyword, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }

        log.info("Found {} products", productPage.getTotalElements());

        return productPage.map(productMapper::toResponse);
    }
}