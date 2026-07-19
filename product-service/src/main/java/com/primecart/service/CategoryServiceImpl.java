package com.primecart.service;

import com.primecart.config.CacheNames;
import com.primecart.dto.response.CategoryResponse;
import com.primecart.entity.Category;
import com.primecart.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @PreAuthorize("hasRole('CATEGORY_READ')")
    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.CATEGORIES,
               key = "'all'",
               sync = true)
    public List<CategoryResponse> getCategories() {

        return categoryRepository
                .findAll(Sort.by(Sort.Direction.ASC, "name"))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private CategoryResponse toResponse(Category category) {

        return new CategoryResponse(category.getId(), category.getName(), category.getDescription(), category.getActive(),
                                    category.getCreatedAt(), category.getUpdatedAt());
    }
}