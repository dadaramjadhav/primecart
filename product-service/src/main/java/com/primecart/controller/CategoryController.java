package com.primecart.controller;

import com.primecart.dto.response.CategoryResponse;
import com.primecart.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategories() {

        log.info("GET /api/categories - Get categories request received");

        List<CategoryResponse> categories = categoryService.getCategories();

        log.info("Categories retrieved successfully: count={}", categories.size());

        return ResponseEntity.ok(categories);
    }
}