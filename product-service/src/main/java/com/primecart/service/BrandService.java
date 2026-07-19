package com.primecart.service;

import com.primecart.dto.response.BrandResponse;

import java.util.List;

public interface BrandService {

    List<BrandResponse> getBrands();
}