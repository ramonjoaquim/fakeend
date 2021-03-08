package com.br.fakeend.repository;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface IMongoRepository {
  ResponseEntity<?> update(Map<String, Object> body, String collection);

  ResponseEntity<?> create(
      Map<String, Object> endpoint, Map<String, Object> body, String path, String collection);

  ResponseEntity<?> getById(int id, String collection);

  ResponseEntity<?> getAll(String collection);

  Integer getLastId(String collection);

  Map<String, Object> getEndpoint(String collection, String path);

  ResponseEntity<?> delete(int id, String collection, Boolean purgeAll);
}
