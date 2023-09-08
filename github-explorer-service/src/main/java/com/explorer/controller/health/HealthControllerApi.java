package com.explorer.controller.health;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/v1/api/health")
public interface HealthControllerApi {

    @GetMapping
    ResponseEntity<String> getHealth();

}
