package com.primecart.service;

import com.primecart.dto.request.UpdateCustomerProfileRequest;
import com.primecart.dto.response.CustomerProfileResponse;
import com.primecart.entity.CustomerProfile;
import com.primecart.repository.CustomerProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerProfileServiceImpl implements CustomerProfileService {

    private final CustomerProfileRepository repository;

    @Override
    @Transactional
    public CustomerProfileResponse getOrCreateProfile(Jwt jwt) {

        CustomerProfile profile = repository
                .findByKeycloakUserId(jwt.getSubject())
                .orElseGet(() -> createProfile(jwt));

        synchronizeIdentityClaims(profile, jwt);

        return toResponse(profile);
    }

    @Override
    @Transactional
    public CustomerProfileResponse updateProfile(Jwt jwt, UpdateCustomerProfileRequest request) {

        CustomerProfile profile = repository
                .findByKeycloakUserId(jwt.getSubject())
                .orElseGet(() -> createProfile(jwt));

        synchronizeIdentityClaims(profile, jwt);
        profile.setPhone(request.phone());
        profile.setEmail(request.email());
        profile.setFirstName(request.firstName());
        profile.setLastName(request.lastName());
        log.info("Updating customer profile. profileId={}", profile.getId());
        return toResponse(profile);
    }

    private CustomerProfile createProfile(Jwt jwt) {

        log.info("Creating customer profile for keycloakUserId={}", jwt.getSubject());
        CustomerProfile profile = new CustomerProfile();

        profile.setKeycloakUserId(jwt.getSubject());
        synchronizeIdentityClaims(profile, jwt);

        CustomerProfile savedProfile = repository.save(profile);
        log.info("Customer profile created. profileId={}", savedProfile.getId());

        return savedProfile;
    }

    private void synchronizeIdentityClaims(CustomerProfile profile, Jwt jwt) {

        profile.setUsername(jwt.getClaimAsString("preferred_username"));

        profile.setEmail(jwt.getClaimAsString("email"));

        profile.setFirstName(jwt.getClaimAsString("given_name"));

        profile.setLastName(jwt.getClaimAsString("family_name"));
    }

    private CustomerProfileResponse toResponse(CustomerProfile profile) {

        return new CustomerProfileResponse(profile.getId(), profile.getKeycloakUserId(), profile.getUsername(), profile.getEmail(),
                                           profile.getFirstName(), profile.getLastName(), profile.getPhone(), profile.getCreatedAt(),
                                           profile.getUpdatedAt());
    }
}