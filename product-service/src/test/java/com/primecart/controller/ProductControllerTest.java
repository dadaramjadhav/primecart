package com.primecart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.primecart.dto.request.CreateProductRequest;
import com.primecart.dto.response.ProductResponse;
import com.primecart.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @Test
    void createProduct_returnsCreated() throws Exception {
        CreateProductRequest request = new CreateProductRequest(
                "Test Product",
                "A product",
                new BigDecimal("9.99"),
                "http://img",
                1L,
                2L,
                true
        );

        ProductResponse response = new ProductResponse(
                10L,
                "Test Product",
                "A product",
                new BigDecimal("9.99"),
                "http://img",
                1L,
                "Category",
                2L,
                "Brand",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(productService.createProduct(any(CreateProductRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void getActiveProducts_returnsList() throws Exception {
        ProductResponse p = new ProductResponse(
                1L, "A", "desc", new BigDecimal("1.00"), null,
                1L, "cat", 1L, "brand", true, LocalDateTime.now(), LocalDateTime.now()
        );

        when(productService.getActiveProducts()).thenReturn(List.of(p));

        mockMvc.perform(get("/api/products/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}

