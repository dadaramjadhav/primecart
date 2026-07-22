package com.primecart.service;

import com.primecart.dto.request.UpdateCustomerProfileRequest;
import com.primecart.dto.response.CustomerProfileResponse;
import org.springframework.security.oauth2.jwt.Jwt;

public interface CustomerProfileService {

    CustomerProfileResponse getProfile(Jwt jwt);

    CustomerProfileResponse updateProfile(Jwt jwt, UpdateCustomerProfileRequest request);
}