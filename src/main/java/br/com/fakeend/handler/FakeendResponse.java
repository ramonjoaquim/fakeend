package br.com.fakeend.handler;

import br.com.fakeend.model.Endpoint;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.TimeUnit;

@EqualsAndHashCode
public class FakeendResponse<T> extends ResponseEntity<T> {

    private Endpoint endpoint;

    private FakeendResponse(T body, HttpStatus status, Endpoint endpoint) {
        super(body, status);
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
        private Object body;

        private FakeendResponseBuilder(HttpStatus status) {
            this.status = status;
        }

        public FakeendResponseBuilder endpoint(Endpoint endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public FakeendResponseBuilder body(Object body) {
            this.body = body;
            return this;
        }

        public <T> FakeendResponse<T> build() {
            applyTimeout(endpoint);

            return (FakeendResponse<T>) new FakeendResponse<>(body, status, endpoint);
        }
    }

    public FakeendResponse(T body, HttpStatusCode status) {
        super(body, status);
    }

    public static <T> FakeendResponse<T> ok(T body, Endpoint endpoint) {
        applyTimeout(endpoint);

        return new FakeendResponse<>(body, HttpStatus.OK);
    }

    private static void applyTimeout(Endpoint endpoint) {
        try {
            if (endpoint.timeout() != null) {
                TimeUnit.SECONDS.sleep(endpoint.timeout());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
