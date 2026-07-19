package com.primecart.service;

import com.primecart.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    List<CategoryResponse> getCategories();
}