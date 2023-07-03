package br.com.fakeend.handler;

import br.com.fakeend.model.Endpoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.TimeUnit;

public class FakeendResponse<T> extends ResponseEntity<T> {

    private Endpoint endpoint;

    private FakeendResponse(HttpStatus status, Endpoint endpoint) {
        super(status);
        this.endpoint = endpoint;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public static FakeendResponseBuilder status(HttpStatus status) {
        return new FakeendResponseBuilder(status);
    }

    public static class FakeendResponseBuilder {
        private final HttpStatus status;
        private Endpoint endpoint;

        private FakeendResponseBuilder(HttpStatus status) {
            this.status = status;
        }

        public FakeendResponseBuilder endpoint(Endpoint endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public <T> FakeendResponse<T> build() {
            delay(endpoint);
            return new FakeendResponse<>(status, endpoint);
        }
    }

    public FakeendResponse(T body, HttpStatusCode status) {
        super(body, status);
    }

    public static <T> FakeendResponse<T> ok(T body, Endpoint endpoint) {
        delay(endpoint);
        return new FakeendResponse<>(body, HttpStatus.OK);
    }

    private static void delay(Endpoint endpoint) {
        try {
            if (endpoint.delay() != null) {
                TimeUnit.SECONDS.sleep(endpoint.delay());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
