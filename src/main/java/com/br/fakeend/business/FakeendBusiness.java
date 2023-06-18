package com.br.fakeend.business;

import com.br.fakeend.config.MongoRepository;
import com.br.fakeend.dto.ResultPrettyDto;
import com.br.fakeend.repository.IMongoRepository;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Projections;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.br.fakeend.commons.Constants.*;
import static java.lang.Integer.parseInt;

@Component
public class FakeendBusiness implements IMongoRepository {

  private final MongoRepository repository;

  @Autowired
  public FakeendBusiness(MongoRepository repository) {
    this.repository = repository;
  }

  @Override
  public ResponseEntity<Object> update(Map<String, Object> body, String collection) {
    Document condition = new Document(ID, body.get(ID));
    Document document = new Document();
    document.put(ID, body.get(ID));
    document.put(BODY, body);

    UpdateResult target =
        repository.getMongoDatabase().getCollection(collection).replaceOne(condition, document);

    return target.getModifiedCount() == 0
        ? ResponseEntity.status(HttpStatus.NO_CONTENT)
            .body("Record with id " + body.get(ID) + " not found.")
        : ResponseEntity.ok().build();
  }

  @Override
  public  ResponseEntity<Object> patch(int id, Map<String, Object> body, String collection) {
    Document filter = new Document();
    filter.put(ID, id);
    var retorno =
            repository.getMongoDatabase().getCollection(collection).find(filter).first();

    Document bodyExtract = (Document) retorno.get(BODY);
    List<Document> documentList = new ArrayList<>(bodyExtract.keySet().size());

    for (String key : bodyExtract.keySet()) {
      if (body.containsKey(key) && !body.containsKey(ID)) {
          bodyExtract.replace(key, body.get(key));
      }

      documentList.add(new Document(key, bodyExtract.get(key)));
    }

    Map<String, Object> newBody = new LinkedHashMap<>();
    documentList.forEach(item -> item.keySet().forEach(key -> newBody.put(key, item.get(key))));

    Document document = new Document();
    document.put(ID, id);
    document.put(BODY, newBody);

    UpdateResult target =
            repository.getMongoDatabase().getCollection(collection).replaceOne(filter, document);

    return target.getModifiedCount() == 0
            ? ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(String.format("Record with id %s or propertie for PATCH not found.", id))
            : ResponseEntity.ok().body(newBody);
  }

  @Override
  public ResponseEntity<?> create(
      Map<String, Object> endpoint, Map<String, Object> body, String path, String collection) {
    Document filter = new Document();
    filter.put(PATH, path);
    var retornoByPath =
        repository
            .getMongoDatabase()
            .getCollection(collection)
            .find(filter);
    if (retornoByPath.first() != null) {
      MongoCollection<Document> mongoCollection =
          repository.getMongoDatabase().getCollection(endpoint.get("name").toString());

      Integer lastId = getLastId(endpoint.get(NAME).toString());
      if (!body.containsKey(ID)) body.put(ID, lastId + 1);
      else body.replace(ID, lastId + 1);

      Document insert = new Document();
      insert.put(ID, body.get(ID));
      insert.put(BODY, body);
      mongoCollection.insertOne(insert);
    }

    return ResponseEntity.status(HttpStatus.CREATED).body(body);
  }

  @Override
  public ResponseEntity<Object> getById(int id, String collection) {
    Document filter = new Document();
    filter.put(ID, id);
    var retorno =
        repository.getMongoDatabase().getCollection(collection).find(filter).first();

    return Objects.isNull(retorno)
        ? ResponseEntity.status(HttpStatus.NO_CONTENT).build()
        : ResponseEntity.status(HttpStatus.OK).body(retorno.get(BODY));
  }

  @Override
  public ResponseEntity<ResultPrettyDto> getAll(String collection) {
    var retorno =
        repository
            .getMongoDatabase()
            .getCollection(collection)
            .find()
            .projection(Projections.excludeId());
    List justBody = new LinkedList();
    for (Document document : retorno) {
      justBody.add(document.get(BODY));
    }

    ResultPrettyDto build = ResultPrettyDto.builder()
            .results(justBody.size())
            .content(justBody)
            .build();

    return ResponseEntity.status(HttpStatus.OK).body(build);
  }

  @Override
  public Integer getLastId(String collection) {
    Document document = new Document();
    List<Integer> ids = new LinkedList<>();
    var retorno = repository.getMongoDatabase().getCollection(collection).find(document);

    if (!retorno.iterator().hasNext()) {
      return 0;
    } else {
      for (Document d : retorno) {
        ids.add(parseInt(d.get(ID).toString()));
      }
    }

    return Collections.max(ids);
  }

  @Override
  public Map getEndpoint(String collection, String path) {
    Document filter = new Document();
    filter.put(PATH, path);
    var retornoByPath =
        repository
            .getMongoDatabase()
            .getCollection(collection)
            .find(filter);

    Map<String, String> mapper = new HashMap<>();
    if (retornoByPath.first() != null) {
      mapper.put(NAME, retornoByPath.first().get(NAME).toString());
      mapper.put(PATH, retornoByPath.first().get(PATH).toString());
    }

    return mapper;
  }

  @Override
  public ResponseEntity<Object> delete(int id, String collection, Boolean purgeAll) {
    if (purgeAll) {
      DeleteResult retorno =
              repository.getMongoDatabase().getCollection(collection).deleteMany(new Document());
      return retorno.getDeletedCount() > 0
              ? ResponseEntity.status(HttpStatus.NO_CONTENT).build()
              : ResponseEntity.status(HttpStatus.OK).body(String.format("Collection %s was already empty", collection));
    } else {
      Document filter = new Document();
      filter.put(ID, id);
      DeleteResult retorno =
              repository.getMongoDatabase().getCollection(collection).deleteOne(filter);

      return retorno.getDeletedCount() > 0
              ? ResponseEntity.status(HttpStatus.NO_CONTENT).build()
              : ResponseEntity.status(HttpStatus.OK).body(String.format("Record with id %d not found.", id));
    }
  }
}
