package br.com.fakeend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.hateoas.server.core.Relation;

import java.util.Map;

@Relation(collectionRelation = "data")
public record Content(
        @JsonIgnore
        Integer id,
        Map<String, Object> body
) {}
