package it.unifi.nauticshare.exception;

public class MemberNotFoundException extends RuntimeException {

    // Usato quando conosci solo il messaggio
    public MemberNotFoundException(String message) {
        super(message);
    }

    // Usato quando vuoi wrappare un'eccezione originale
    public MemberNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}