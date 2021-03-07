package com.br.fakeend.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@Component
public class MongoRepository {

    private MongoClient client;
    private MongoClientURI uri;

    @Value("${mongodb.uri}")
    private String mongodbConnection;

    @Value("${mongodb.database}")
    private String dataBase;

    public MongoDatabase getMongoDatabase() {
        setUri(new MongoClientURI(mongodbConnection));
        setClient(new MongoClient(uri));

        return getClient().getDatabase(dataBase);
    }
}
