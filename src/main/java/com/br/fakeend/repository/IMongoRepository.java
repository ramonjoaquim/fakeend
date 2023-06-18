package com.br.fakeend.repository;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface IMongoRepository {
  ResponseEntity<Object> update(Map<String, Object> body, String collection);

  ResponseEntity<Object> create(
      Map<String, Object> endpoint, Map<String, Object> body, String path, String collection);

  ResponseEntity<Object> getById(int id, String collection);

  ResponseEntity<Object> getAll(String collection);

  Integer getLastId(String collection);

  Map<String, Object> getEndpoint(String collection, String path);

  ResponseEntity<Object> delete(int id, String collection, Boolean purgeAll);

  ResponseEntity<Object> patch(int id, Map<String, Object> body, String collection);
}
