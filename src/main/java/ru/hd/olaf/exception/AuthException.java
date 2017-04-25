package ru.hd.olaf.exception;

/**
 * Created by d.v.hozyashev on 25.04.2017.
 */
public class AuthException extends Exception {

    public AuthException() {
    }

    public AuthException(String message) {
        super(message);
    }
}
