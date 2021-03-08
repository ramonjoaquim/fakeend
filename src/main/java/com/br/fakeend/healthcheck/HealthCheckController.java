package com.br.fakeend.healthcheck;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "fakeend/api")
public class HealthCheckController {

    @GetMapping(path = "health-check")
    public ResponseEntity<String> healtCheck() {
        return ResponseEntity.ok("Application is up!");
    }
}
