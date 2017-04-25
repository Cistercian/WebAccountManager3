package ru.hd.olaf.exception;

/**
 * Created by d.v.hozyashev on 25.04.2017.
 */
public class CrudException extends Exception {

    public CrudException() {
    }

    public CrudException(String message) {
        super(message);
    }
}
