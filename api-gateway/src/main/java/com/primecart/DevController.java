package com.primecart;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/dev")
public class DevController {

    private final WebClient webClient;

    public DevController(WebClient webClient) {
        this.webClient = webClient;
    }

    @Operation(
            summary = "Get Keycloak Access Token (Development Only)",
            description = "Returns an access token from Keycloak."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token generated"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping(
            value = "/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<String>> token(

            @Parameter(description = "Grant type", example = "password")
            @RequestParam(defaultValue = "password")
            String grant_type,

            @Parameter(description = "Client ID", example = "api-gateway")
            @RequestParam(defaultValue = "api-gateway")
            String client_id,

//            @Parameter(description = "Client Secret")
//            @RequestParam(defaultValue = "")
//            String client_secret,

//            @Parameter(description = "Username", example = "admin")
//            @RequestParam(defaultValue = "admin")
            String username,

            @Parameter(description = "Password", example = "123")
            @RequestParam(defaultValue = "123")
            String password
    ) {

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", grant_type);
        form.add("client_id", client_id);
//        form.add("client_secret", client_secret);
        form.add("username", username);
        form.add("password", password);

        return webClient.post()
                        .uri("http://localhost:8090/realms/primecart/protocol/openid-connect/token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .bodyValue(form)
                        .retrieve()
                        .toEntity(String.class);
    }
}