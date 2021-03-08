package com.br.fakeend.controller;

import com.br.fakeend.config.MongoRepository;
import com.br.fakeend.model.Endpoint;
import com.mongodb.client.MongoCollection;
import lombok.AllArgsConstructor;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.br.fakeend.commons.Constants.NAME;
import static com.br.fakeend.commons.Constants.PATH;

@RestController
@RequestMapping(value = "fakeend/api/endpoint", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class EndpointController {
  private static final String COLLECTION = "ENDPOINTS";
  private static final Logger LOGGER = Logger.getLogger(EndpointController.class.getName());

  @Autowired private final MongoRepository mongoRepository;

  @PostMapping(path = "create", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> createEndpoint(@RequestBody Endpoint endpoint) {

    if (Objects.isNull(endpoint)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

    if (endpointExists(endpoint.getName(), endpoint.getPath())) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .body(String.format("Endpoint %s already exists", endpoint.getName()));
    }

    // Criação da collection para armazenar os dados fake referenciado pelo name do endpoint
    createCollectionIfNotExists(endpoint.getName());
    Document documento = buildEndpoint(endpoint);
    mongoRepository.getMongoDatabase().getCollection(COLLECTION).insertOne(documento);

    return ResponseEntity.status(HttpStatus.CREATED).body(documento);
  }

  @DeleteMapping(path = "{nameEndpoint}")
  public ResponseEntity<?> deleteEndpoint(@PathVariable("nameEndpoint") String nameEndpoint) {

    if (nameEndpoint.isEmpty())
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nome do enpoint obrigatório.");

    MongoCollection<Document> collection =
        mongoRepository.getMongoDatabase().getCollection(nameEndpoint);
    collection.drop();

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  private void createCollectionIfNotExists(String nameCollection) {
    try {
      mongoRepository.getMongoDatabase().createCollection(nameCollection);
    } catch (Exception ex) {
      LOGGER.log(Level.FINE, ex.toString(), ex);
    }
  }

  private Boolean endpointExists(String name, String path) {
    Document document = new Document();
    document.put(NAME, name);
    document.put(PATH, path);

    return mongoRepository.getMongoDatabase().getCollection(COLLECTION).find(document).first()
        != null;
  }

  private Document buildEndpoint(Endpoint endpoint) {
    Document document = new Document();
    document.put(NAME, endpoint.getName());
    document.put(PATH, endpoint.getPath());

    return document;
  }
}
