package com.br.fakeend.repository;

import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.UUID;

public interface IMongoRepository {
  ResponseEntity<?> update(UUID idEndpoint, Map<String, Object> body, String collection);

  ResponseEntity<?> create(
      Map<String, Object> endpoint, Map<String, Object> body, String path, String collection);

  ResponseEntity<?> getById(int id, String collection);

  ResponseEntity<?> getAll(String collection);

  Integer getLastId(String collection);

  UUID getIdEndpoint(String collection, String path);

  Map<String, Object> getEndpoint(String collection, String path);
}
