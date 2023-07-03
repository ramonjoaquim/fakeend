package br.com.fakeend.service;

import br.com.fakeend.Utils.ValidatorRequestUtils;
import br.com.fakeend.commons.Constants;
import br.com.fakeend.commons.RequestHandler;
import br.com.fakeend.handler.FakeendResponse;
import br.com.fakeend.model.Endpoint;
import br.com.fakeend.model.EndpointContent;
import br.com.fakeend.repository.EndpointContentRepository;
import br.com.fakeend.repository.EndpointRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetService {

    private final EndpointRepository endpointRepository;
    private final EndpointContentRepository ecRepository;

    public GetService(EndpointRepository endpointRepository, EndpointContentRepository ecRepository) {
        this.endpointRepository = endpointRepository;
        this.ecRepository = ecRepository;
    }

    public ResponseEntity<Object> process(RequestHandler requestHandler) {
        Endpoint endpoint = endpointRepository.findByName(requestHandler.getPath());
        ValidatorRequestUtils.validate(endpoint, requestHandler, null, false);

        boolean isGetAll = Constants.ID_PATH_DEFAULT.equals(requestHandler.getId());

        if (isGetAll) {
            List<EndpointContent> contents = ecRepository.findByName(endpoint.name());
            return FakeendResponse.ok(contents, endpoint);
        } else {
            Integer contentId = requestHandler.getId();
            EndpointContent content = ecRepository.findByEndpointNameAndContentId(endpoint.name(), contentId);

            return FakeendResponse.ok(content, endpoint);
        }
    }
}
