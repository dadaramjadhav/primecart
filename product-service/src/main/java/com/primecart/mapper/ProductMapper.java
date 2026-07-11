package com.primecart.mapper;

import com.primecart.dto.request.CreateProductRequest;
import com.primecart.dto.request.UpdateProductRequest;
import com.primecart.dto.response.ProductResponse;
import com.primecart.entity.Brand;
import com.primecart.entity.Category;
import com.primecart.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(CreateProductRequest request,
                            Category category,
                            Brand brand) {

        Product product = new Product();

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setImageUrl(request.imageUrl());
        product.setCategory(category);
        product.setBrand(brand);
        product.setActive(request.active());

        return product;
    }

    public void updateEntity(Product product,
                             UpdateProductRequest request,
                             Category category,
                             Brand brand) {

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setImageUrl(request.imageUrl());
        product.setCategory(category);
        product.setBrand(brand);
        product.setActive(request.active());
    }

    public ProductResponse toResponse(Product product) {

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getImageUrl(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getBrand().getId(),
                product.getBrand().getName(),
                product.getActive(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}