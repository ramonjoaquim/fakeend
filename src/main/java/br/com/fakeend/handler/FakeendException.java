package br.com.fakeend.handler;

public class FakeendException extends Exception {

    public FakeendException() {
        super();
    }

    public FakeendException(String message) {
        super(message);
    }

    public FakeendException(String message, Throwable cause) {
        super(message, cause);
    }
}
