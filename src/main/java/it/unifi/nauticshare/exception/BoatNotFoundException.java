package it.unifi.nauticshare.exception;

public class BoatNotFoundException extends RuntimeException {

    public BoatNotFoundException(String message) {
        super(message);
    }

    public BoatNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}