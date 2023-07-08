package br.com.fakeend.service;

import br.com.fakeend.Utils.ValidatorRequestUtils;
import br.com.fakeend.commons.Constants;
import br.com.fakeend.commons.RequestHandler;
import br.com.fakeend.handler.FakeendResponse;
import br.com.fakeend.model.Content;
import br.com.fakeend.model.Endpoint;
import br.com.fakeend.model.EndpointContent;
import br.com.fakeend.repository.EndpointContentRepository;
import br.com.fakeend.repository.EndpointRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PostService {

    private final EndpointRepository endpointRepository;
    private final EndpointContentRepository ecRepository;

    public PostService(EndpointRepository endpointRepository, EndpointContentRepository ecRepository) {
        this.endpointRepository = endpointRepository;
        this.ecRepository = ecRepository;
    }

    public ResponseEntity<Object> process(RequestHandler requestHandler, Map<String, Object> body) {
        ValidatorRequestUtils.validateIdOnPost(requestHandler);
        Endpoint endpoint = endpointRepository.findByName(requestHandler.getPath());
        ValidatorRequestUtils.validate(endpoint, requestHandler, null, false);

        Content content = transformData(endpoint, body);
        EndpointContent insert = new EndpointContent(endpoint.name(), content);

        ecRepository.insert(insert);

        return FakeendResponse
                .status(HttpStatus.CREATED)
                .endpoint(endpoint)
                .body(content.body())
                .build();
    }

    private Content transformData(Endpoint endpoint, Map<String, Object> body) {
        Integer lastId = getLastId(endpoint.name());
        if (!body.containsKey(Constants.ID)) {
            body.put(Constants.ID, lastId + 1);
        } else {
            body.replace(Constants.ID, lastId + 1);
        }

        return new Content(lastId + 1, body);
    }

    public Integer getLastId(String nameCollection) {
        List<EndpointContent> contents = ecRepository.findByName(nameCollection);
        List<Integer> idsOfContent = new ArrayList<>();
        contents.stream()
                .map(EndpointContent::content)
                .map(Content::id)
                .filter(Objects::nonNull)
                .forEach(idsOfContent::add);

        boolean isFirstContentForCollection = idsOfContent.isEmpty();
        if (isFirstContentForCollection) {
            return 0;
        } else {
            return Collections.max(idsOfContent);
        }
    }
}
