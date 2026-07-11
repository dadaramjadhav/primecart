package com.primecart;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
public class DebugController {

    @GetMapping("/debug")
    public Map<String, Object> debug(
            @AuthenticationPrincipal Jwt jwt) {

        Map<String, Object> map = new HashMap<>();

        map.put("subject", jwt.getSubject());
        map.put("claims", jwt.getClaims());

        return map;
    }
}