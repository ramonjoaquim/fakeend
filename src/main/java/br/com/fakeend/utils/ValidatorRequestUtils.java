package br.com.fakeend.utils;

import br.com.fakeend.commons.Constants;
import br.com.fakeend.commons.RequestHandler;
import br.com.fakeend.model.Endpoint;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

public class ValidatorRequestUtils {

    private ValidatorRequestUtils() {
    }

    public static void validateIdOnPost(RequestHandler requestHandler) {
        if (!Constants.ID_PATH_DEFAULT.equals(requestHandler.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Identifier not be used in URL in POST method, use PUT/PATCH method instead.");
        }
    }

    public static void validateId(RequestHandler requestHandler) {
        if (Constants.ID_PATH_DEFAULT.equals(requestHandler.getId()) && !requestHandler.isPurgeAll()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Identifier path must be informed in URL.");
        }
    }

    public static void validate(Endpoint endpoint, RequestHandler requestHandler, Map body, boolean bodyRequired) {
        if (endpoint == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Endpoint " + requestHandler.getPath() + " not exists");
        }

        if (bodyRequired && body == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body Request must be informed");
        }
    }
}
