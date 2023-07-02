package br.com.fakeend.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document("endpoint-content")
public record EndpointContent(
        String id,
        String endpointName,
        Content content
) {

    public EndpointContent(String endpointName, Content content) {
        this(null, endpointName, content);
    }
}
