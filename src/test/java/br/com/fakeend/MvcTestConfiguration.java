package br.com.fakeend;

import br.com.fakeend.repository.EndpointContentRepository;
import br.com.fakeend.repository.EndpointExtensionRepository;
import br.com.fakeend.repository.EndpointRepository;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;

public class MvcTestConfiguration {

    @MockBean
    public EndpointRepository repository;

    @MockBean
    public EndpointContentRepository endpointContentRepository;

    @MockBean
    public EndpointExtensionRepository endpointExtensionRepository;

    @MockBean
    public MongoTemplate mongoTemplate;
}
