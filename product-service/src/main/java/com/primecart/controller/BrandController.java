package com.primecart.controller;

import com.primecart.dto.response.BrandResponse;
import com.primecart.service.BrandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    public ResponseEntity<List<BrandResponse>> getBrands() {

        log.info("GET /api/brands - Get brands request received");

        List<BrandResponse> brands = brandService.getBrands();

        log.info("Brands retrieved successfully: count={}", brands.size());

        return ResponseEntity.ok(brands);
    }
}