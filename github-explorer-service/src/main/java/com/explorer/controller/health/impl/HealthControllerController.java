package com.explorer.controller.health.impl;

import com.explorer.controller.health.HealthControllerApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthControllerController implements HealthControllerApi {
    @Override
    public ResponseEntity<String> getHealth() {
        return new ResponseEntity<>("UP", HttpStatus.OK);
    }
}
