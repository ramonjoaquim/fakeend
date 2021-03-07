package com.br.fakeend.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CollectionDefaultInitialize implements InitializingBean {

  @Autowired private final MongoRepository mongoRepository;

  public CollectionDefaultInitialize(MongoRepository mongoRepository) {
    this.mongoRepository = mongoRepository;
  }

  @Override
  public void afterPropertiesSet() {
    try {
      mongoRepository.getMongoDatabase().createCollection("ENDPOINTS");
    } catch (Exception ignored) {
    }
  }
}
