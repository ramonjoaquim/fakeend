package br.com.fakeend.service;

import br.com.fakeend.utils.ValidatorRequestUtils;
import br.com.fakeend.commons.Constants;
import br.com.fakeend.commons.RequestHandler;
import br.com.fakeend.handler.FakeendResponse;
import br.com.fakeend.model.Endpoint;
import br.com.fakeend.repository.EndpointExtensionRepository;
import br.com.fakeend.repository.EndpointRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class DeleteService {

    private final EndpointRepository endpointRepository;
    private final EndpointExtensionRepository eceRepository;

    public DeleteService(EndpointRepository endpointRepository, EndpointExtensionRepository eceRepository) {
        this.endpointRepository = endpointRepository;
        this.eceRepository = eceRepository;
    }

    public ResponseEntity<Object> process(RequestHandler requestHandler) {
        if (Constants.ID_PATH_DEFAULT.equals(requestHandler.getId()) && !requestHandler.isPurgeAll()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Identifier path must be informed in URL.");
        }

        Endpoint endpoint = endpointRepository.findByName(requestHandler.getPath());
        ValidatorRequestUtils.validate(endpoint, requestHandler, null, false);

        if (requestHandler.isPurgeAll()) {
            eceRepository.deleteByEndpointName(endpoint.name());
        } else {
            Integer contentId = requestHandler.getId();
            long deletedCount = eceRepository.deleteByContentId(contentId);
            if (deletedCount == 0) {
                return FakeendResponse
                        .status(HttpStatus.NOT_FOUND)
                        .endpoint(endpoint)
                        .build();
            }
        }

        return FakeendResponse
                .status(HttpStatus.NO_CONTENT)
                .endpoint(endpoint)
                .build();
    }
}
