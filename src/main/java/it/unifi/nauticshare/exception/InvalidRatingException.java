package it.unifi.nauticshare.exception;

public class InvalidRatingException extends RuntimeException {

    public InvalidRatingException(String message) {
        super(message);
    }

    public InvalidRatingException(String message, Throwable cause) {
        super(message, cause);
    }
}