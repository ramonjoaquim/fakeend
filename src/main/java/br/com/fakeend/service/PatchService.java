package br.com.fakeend.service;

import br.com.fakeend.Utils.ValidatorRequestUtils;
import br.com.fakeend.commons.Constants;
import br.com.fakeend.commons.RequestHandler;
import br.com.fakeend.handler.FakeendResponse;
import br.com.fakeend.model.Endpoint;
import br.com.fakeend.repository.EndpointContentExtensionRepository;
import br.com.fakeend.repository.EndpointContentRepository;
import br.com.fakeend.repository.EndpointRepository;
import com.mongodb.client.result.UpdateResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Component
public class PatchService {

    private final EndpointRepository endpointRepository;

    private final EndpointContentRepository endpointContentRepository;
    private final EndpointContentExtensionRepository ecRepository;

    public PatchService(EndpointRepository endpointRepository,
                        EndpointContentRepository endpointContentRepository,
                        EndpointContentExtensionRepository ecRepository) {
        this.endpointRepository = endpointRepository;
        this.endpointContentRepository = endpointContentRepository;
        this.ecRepository = ecRepository;
    }

    public ResponseEntity<Object> process(RequestHandler requestHandler, Map<String, Object> body) {
        ValidatorRequestUtils.validateId(requestHandler);
        Endpoint endpoint = endpointRepository.findByName(requestHandler.getPath());
        ValidatorRequestUtils.validate(endpoint, requestHandler, body, true);

        body.put(Constants.ID, requestHandler.getId());

        UpdateResult target = ecRepository.patch(requestHandler.getId(), body);

        if (target == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "%s with id %s not found".formatted(endpoint.name(), requestHandler.getId()));
        }

        return FakeendResponse.ok()
                .body(endpointContentRepository.findByEndpointNameAndContentId(endpoint.name(), requestHandler.getId()).content().body());
    }
}
