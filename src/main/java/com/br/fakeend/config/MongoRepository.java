package com.br.fakeend.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@Component
public class MongoRepository {

    private MongoClient client;
    private MongoClientURI uri;

    public MongoDatabase getMongoDatabase() {
        setUri(new MongoClientURI(""));
        setClient(new MongoClient(uri));

        return client.getDatabase("fakeend");
    }
}
