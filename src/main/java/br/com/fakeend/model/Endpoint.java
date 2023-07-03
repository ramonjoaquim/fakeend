package br.com.fakeend.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document("endpoint")
public record Endpoint(
        String id,
        String name,
        String path,
        Integer delay
) {

    public Endpoint(String name, String path, Integer delay) {
        this(null, name, path, delay);
    }
}
