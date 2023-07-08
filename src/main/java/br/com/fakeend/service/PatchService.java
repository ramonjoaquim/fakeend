package br.com.fakeend.service;

import br.com.fakeend.Utils.ValidatorRequestUtils;
import br.com.fakeend.commons.Constants;
import br.com.fakeend.commons.RequestHandler;
import br.com.fakeend.handler.FakeendResponse;
import br.com.fakeend.model.Endpoint;
import br.com.fakeend.repository.EndpointExtensionRepository;
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
    private final EndpointExtensionRepository ecRepository;

    public PatchService(EndpointRepository endpointRepository,
                        EndpointContentRepository endpointContentRepository,
                        EndpointExtensionRepository ecRepository) {
        this.endpointRepository = endpointRepository;
        this.endpointContentRepository = endpointContentRepository;
        this.ecRepository = ecRepository;
    }

    public ResponseEntity<Object> process(RequestHandler requestHandler, Map<String, Object> body) {
        ValidatorRequestUtils.validateId(requestHandler);
        Endpoint endpoint = endpointRepository.findByName(requestHandler.getPath());
        ValidatorRequestUtils.validate(endpoint, requestHandler, body, true);

        body.put(Constants.ID, requestHandler.getId());

        UpdateResult target = ecRepository.patchEndpointContent(requestHandler.getId(), body);

        if (target == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "%s with id %s not found".formatted(endpoint.name(), requestHandler.getId()));
        }

        Map<String, Object> objectMap = endpointContentRepository.findByEndpointNameAndContentId(endpoint.name(),
                requestHandler.getId()).content().body();

        return FakeendResponse.ok(objectMap, endpoint);
    }
}
