package br.com.fakeend.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document("endpoint")
public record Endpoint(
        String id,
        String name,
        String path
) {

    public Endpoint(String name, String path) {
        this(null, name, path);
    }
}
