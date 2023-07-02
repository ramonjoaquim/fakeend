package br.com.fakeend.repository;

import br.com.fakeend.model.EndpointContent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface EndpointContentRepository extends MongoRepository<EndpointContent, String> {

    @Query("{endpointName: '?0'}")
    List<EndpointContent> findByName(String name);

    @Query("{'endpointName': '?0', 'content.id': ?1}")
    EndpointContent findByEndpointNameAndContentId(String name, Integer id);
}
