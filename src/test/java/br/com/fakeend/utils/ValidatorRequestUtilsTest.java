package br.com.fakeend.utils;

import br.com.fakeend.commons.Constants;
import br.com.fakeend.commons.RequestHandler;
import br.com.fakeend.model.Endpoint;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ValidatorRequestUtilsTest {

    private static final HttpServletRequest HTTP_SERVLET_REQUEST = mock(HttpServletRequest.class);
    private static final String URL = "localhost:8080/fakeend/api/endpoint";
    private static final StringBuffer REQUEST_URL = new StringBuffer().append(URL);

    @BeforeAll
    static void before() {
        when(HTTP_SERVLET_REQUEST.getAttribute(any())).thenReturn(URL);
        when(HTTP_SERVLET_REQUEST.getRequestURL()).thenReturn(REQUEST_URL);
    }

    @Test
    void testValidateIdOnPost() {
        RequestHandler requestHandler = new RequestHandler(HTTP_SERVLET_REQUEST);
        requestHandler.setId(123);

        try {
            ValidatorRequestUtils.validateIdOnPost(requestHandler);
        } catch (ResponseStatusException ex) {
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
            Assertions.assertEquals("Identifier not be used in URL in POST method, use PUT/PATCH method instead.", ex.getReason());
        }
    }

    @Test
    void testValidateIdOnPostWithDefaultId() {
        RequestHandler requestHandler = new RequestHandler(HTTP_SERVLET_REQUEST);
        requestHandler.setId(Constants.ID_PATH_DEFAULT);

        // Ensure that no exception is thrown
        ValidatorRequestUtils.validateIdOnPost(requestHandler);
    }

    @Test
    void testValidateId() {
        RequestHandler requestHandler = new RequestHandler(HTTP_SERVLET_REQUEST);
        requestHandler.setId(Constants.ID_PATH_DEFAULT);
        requestHandler.setPurgeAll(false);

        try {
            ValidatorRequestUtils.validateId(requestHandler);
        } catch (ResponseStatusException ex) {
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
            Assertions.assertEquals("Identifier path must be informed in URL.", ex.getReason());
        }
    }

    @Test
    void testValidateIdWithIdAndPurgeAll() {
        RequestHandler requestHandler = new RequestHandler(HTTP_SERVLET_REQUEST);
        requestHandler.setId(123);
        requestHandler.setPurgeAll(true);

        // Ensure that no exception is thrown
        ValidatorRequestUtils.validateId(requestHandler);
    }

    @Test
    void testValidateEndpoint() {
        RequestHandler requestHandler = new RequestHandler(HTTP_SERVLET_REQUEST);
        requestHandler.setPath("/api/endpoint");

        Endpoint endpoint = null;
        Map<String, Object> body = new HashMap<>();
        boolean bodyRequired = true;

        try {
            ValidatorRequestUtils.validate(endpoint, requestHandler, body, bodyRequired);
        } catch (ResponseStatusException ex) {
            Assertions.assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
            Assertions.assertEquals("Endpoint /api/endpoint not exists", ex.getReason());
        }
    }

    @Test
    void testValidateEndpointWithBodyRequired() {
        RequestHandler requestHandler = new RequestHandler(HTTP_SERVLET_REQUEST);
        requestHandler.setPath("/api/endpoint");

        Endpoint endpoint = new Endpoint("a", "b", null);
        Map<String, Object> body = null;
        boolean bodyRequired = true;

        try {
            ValidatorRequestUtils.validate(endpoint, requestHandler, body, bodyRequired);
        } catch (ResponseStatusException ex) {
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
            Assertions.assertEquals("Body Request must be informed", ex.getReason());
        }
    }

    @Test
    void testValidateEndpointWithBodyNotRequired() {
        RequestHandler requestHandler = new RequestHandler(HTTP_SERVLET_REQUEST);
        requestHandler.setPath("/api/endpoint");

        Endpoint endpoint = new Endpoint("a", "b", null);
        Map<String, Object> body = null;
        boolean bodyRequired = false;

        // Ensure that no exception is thrown
        ValidatorRequestUtils.validate(endpoint, requestHandler, body, bodyRequired);
    }
}
