package br.com.fakeend.service;

import br.com.fakeend.utils.ValidatorRequestUtils;
import br.com.fakeend.commons.Constants;
import br.com.fakeend.commons.RequestHandler;
import br.com.fakeend.handler.FakeendResponse;
import br.com.fakeend.model.Content;
import br.com.fakeend.model.Endpoint;
import br.com.fakeend.model.EndpointContent;
import br.com.fakeend.repository.EndpointContentRepository;
import br.com.fakeend.repository.EndpointExtensionRepository;
import br.com.fakeend.repository.EndpointRepository;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetService {

    private final EndpointRepository endpointRepository;
    private final EndpointContentRepository ecRepository;
    private final EndpointExtensionRepository endpointExtensionRepository;

    public GetService(EndpointRepository endpointRepository,
                      EndpointContentRepository ecRepository,
                      EndpointExtensionRepository endpointExtensionRepository) {
        this.endpointRepository = endpointRepository;
        this.ecRepository = ecRepository;
        this.endpointExtensionRepository = endpointExtensionRepository;
    }

    public ResponseEntity<Object> process(RequestHandler requestHandler, int page, int size) {
        Endpoint endpoint = endpointRepository.findByName(requestHandler.getPath());
        ValidatorRequestUtils.validate(endpoint, requestHandler, null, false);

        boolean isGetAll = Constants.ID_PATH_DEFAULT.equals(requestHandler.getId());

        if (isGetAll) {
            PagedModel<Content> contents = endpointExtensionRepository.findAll(requestHandler, endpoint.name(), page, size);

            return FakeendResponse.ok(contents, endpoint);
        } else {
            Integer contentId = requestHandler.getId();
            EndpointContent content = ecRepository.findByEndpointNameAndContentId(endpoint.name(), contentId);

            return FakeendResponse.ok(content.content(), endpoint);
        }
    }
}
