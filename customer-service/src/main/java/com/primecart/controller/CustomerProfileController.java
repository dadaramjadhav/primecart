package com.primecart.controller;

import com.primecart.dto.request.UpdateCustomerProfileRequest;
import com.primecart.dto.response.CustomerProfileResponse;
import com.primecart.service.CustomerProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerProfileController {

    private final CustomerProfileService customerProfileService;

    @GetMapping("/me")
    public CustomerProfileResponse getMyProfile(
            @AuthenticationPrincipal
            Jwt jwt) {

        return customerProfileService.getProfile(jwt);
    }

    @PutMapping("/me")
    public CustomerProfileResponse updateMyProfile(
            @AuthenticationPrincipal
            Jwt jwt,
            @Valid
            @RequestBody
            UpdateCustomerProfileRequest request) {

        return customerProfileService.updateProfile(jwt, request);
    }
}