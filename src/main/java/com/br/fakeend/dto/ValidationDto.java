package com.br.fakeend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ValidationDto {
    private boolean containsError;
    private ResponseEntity<String> message;
}
