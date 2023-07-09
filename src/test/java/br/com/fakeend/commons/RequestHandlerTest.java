package br.com.fakeend.commons;


import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class RequestHandlerTest {

    private static final HttpServletRequest HTTP_SERVLET_REQUEST = mock(HttpServletRequest.class);
    private static final String URL = "localhost:8080/fakeend/api/endpoint";
    private static final StringBuffer REQUEST_URL = new StringBuffer().append(URL);

    @BeforeAll
    static void before() {
        when(HTTP_SERVLET_REQUEST.getRequestURL()).thenReturn(REQUEST_URL);
    }

    @Test
    void testGetPath() {
        when(HTTP_SERVLET_REQUEST.getAttribute(any())).thenReturn(URL);

        RequestHandler requestHandler = new RequestHandler(HTTP_SERVLET_REQUEST);

        Assertions.assertEquals("api/endpoint", requestHandler.getPath());
        Assertions.assertFalse(requestHandler.isPurgeAll());
    }

    @Test
    void testGetId() {
        when(HTTP_SERVLET_REQUEST.getAttribute(any())).thenReturn(URL+"/1");

        RequestHandler requestHandler = new RequestHandler(HTTP_SERVLET_REQUEST);

        Assertions.assertEquals(1, requestHandler.getId());
        Assertions.assertFalse(requestHandler.isPurgeAll());
    }

    @Test
    void testIsPurgeAll() {
        when(HTTP_SERVLET_REQUEST.getAttribute(any())).thenReturn(URL+"/purge-all");

        RequestHandler requestHandler = new RequestHandler(HTTP_SERVLET_REQUEST);

        Assertions.assertTrue(requestHandler.isPurgeAll());
        Assertions.assertEquals(Constants.ID_PATH_DEFAULT, requestHandler.getId());
    }

    @Test
    void testGetRequestURL() {
        when(HTTP_SERVLET_REQUEST.getAttribute(any())).thenReturn(URL);

        RequestHandler requestHandler = new RequestHandler(HTTP_SERVLET_REQUEST);

        Assertions.assertEquals(URL, requestHandler.getRequestURL());
    }
}