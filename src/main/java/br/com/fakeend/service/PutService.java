package br.com.fakeend.service;

import br.com.fakeend.utils.ValidatorRequestUtils;
import br.com.fakeend.commons.Constants;
import br.com.fakeend.commons.RequestHandler;
import br.com.fakeend.handler.FakeendResponse;
import br.com.fakeend.model.Endpoint;
import br.com.fakeend.repository.EndpointExtensionRepository;
import br.com.fakeend.repository.EndpointRepository;
import com.mongodb.client.result.UpdateResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PutService {

    private final EndpointRepository endpointRepository;
    private final EndpointExtensionRepository ecRepository;

    public PutService(EndpointRepository endpointRepository, EndpointExtensionRepository ecRepository) {
        this.endpointRepository = endpointRepository;
        this.ecRepository = ecRepository;
    }

    public ResponseEntity<Object> process(RequestHandler requestHandler, Map<String, Object> body) {
        ValidatorRequestUtils.validateId(requestHandler);
        Endpoint endpoint = endpointRepository.findByName(requestHandler.getPath());
        ValidatorRequestUtils.validate(endpoint, requestHandler, body, true);

        body.put(Constants.ID, requestHandler.getPath());

        UpdateResult target = ecRepository.updateContent(requestHandler.getId(), body);

        if (target.getModifiedCount() == 0) {
            return FakeendResponse
                    .status(HttpStatus.NO_CONTENT)
                    .endpoint(endpoint)
                    .body("Record with id " + body.get(Constants.ID) + " not found.")
                    .build();
        }

        return FakeendResponse.ok(null, endpoint);
    }
}
