package com.br.fakeend.controller;

import com.br.fakeend.config.MongoRepository;
import com.br.fakeend.model.Collection;
import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoCollection;
import lombok.AllArgsConstructor;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "fakeend/api/collection", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class CollectionsController {

  @Autowired private final MongoRepository mongoRepository;

  @GetMapping()
  public ResponseEntity<?> getCollection() {
    return ResponseEntity.ok().body(mongoRepository.getMongoDatabase().listCollectionNames());
  }

  @PostMapping(path = "create")
  public ResponseEntity<String> createCollection(@RequestBody Collection collection) {

    try {
      mongoRepository.getMongoDatabase().createCollection(collection.getName());

      return ResponseEntity.status(HttpStatus.CREATED).build();
    } catch (MongoCommandException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(e.getResponse().getString("errmsg").getValue());
    }
  }

  @DeleteMapping(path = "{nameCollection}")
  public ResponseEntity<?> deleteCollection(@PathVariable("nameCollection") String nameCollection) {
    try {
      MongoCollection<Document> collection =
          mongoRepository.getMongoDatabase().getCollection(nameCollection);
      collection.drop();

      return ResponseEntity.ok().build();
    } catch (MongoCommandException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(e.getResponse().getString("errmsg").getValue());
    }
  }
}
