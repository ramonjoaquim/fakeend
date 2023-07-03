package br.com.fakeend.controller;

import br.com.fakeend.model.Endpoint;
import br.com.fakeend.model.EndpointContent;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
        value = "fakeend/api/helper/reset-data",
        method = RequestMethod.DELETE
)
public class HelperController {

    private final MongoTemplate mongoTemplate;

    public HelperController(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @DeleteMapping
    public void resetData() {
        mongoTemplate.dropCollection(Endpoint.class);
        mongoTemplate.dropCollection(EndpointContent.class);
    }
}
