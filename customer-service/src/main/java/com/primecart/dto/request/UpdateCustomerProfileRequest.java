package com.primecart.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateCustomerProfileRequest(String firstName, String lastName, @Size(max = 30) @Pattern(regexp = "^[0-9+() -]*$",
                                                                                                       message = "Phone contains invalid characters") String phone) {
}