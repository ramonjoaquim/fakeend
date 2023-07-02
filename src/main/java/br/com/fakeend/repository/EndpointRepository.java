package br.com.fakeend.repository;

import br.com.fakeend.model.Endpoint;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface EndpointRepository extends MongoRepository<Endpoint, String> {

    @Query("{name: '?0'}")
    Endpoint findByName(String name);

    @Query("{$or: [{name: '?0'}, {path: '?1'}]}")
    List<Endpoint> findByNameOrPath(String name, String path);
}
