package com.primecart.service;

import com.primecart.config.CacheNames;
import com.primecart.dto.response.BrandResponse;
import com.primecart.entity.Brand;
import com.primecart.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.BRANDS,
               key = "'all'",
               sync = true)
    public List<BrandResponse> getBrands() {

        return brandRepository
                .findAll(Sort.by(Sort.Direction.ASC, "name"))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private BrandResponse toResponse(Brand brand) {

        return new BrandResponse(brand.getId(), brand.getName(), brand.getDescription(), brand.getLogoUrl(), brand.getActive(),
                                 brand.getCreatedAt(), brand.getUpdatedAt());
    }
}