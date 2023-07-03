package br.com.fakeend.service;

import br.com.fakeend.Utils.ValidatorRequestUtils;
import br.com.fakeend.commons.Constants;
import br.com.fakeend.commons.RequestHandler;
import br.com.fakeend.model.Endpoint;
import br.com.fakeend.repository.EndpointContentExtensionRepository;
import br.com.fakeend.repository.EndpointRepository;
import com.mongodb.client.result.UpdateResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PutService {

    private final EndpointRepository endpointRepository;
    private final EndpointContentExtensionRepository ecRepository;

    public PutService(EndpointRepository endpointRepository, EndpointContentExtensionRepository ecRepository) {
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
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body("Record with id " + body.get(Constants.ID) + " not found.");
        }

        return ResponseEntity.ok().build();
    }
}
