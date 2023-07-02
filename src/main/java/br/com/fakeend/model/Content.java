package br.com.fakeend.model;

import java.util.Map;

public record Content(
        Integer id,
        Map<String, Object> body
) {}
