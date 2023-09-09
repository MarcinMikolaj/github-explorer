package com.explorer.infrastructure.exceptions;

public class ResponseException extends RuntimeException {

    public ResponseException() {
        super("Incorrect Response");
    }

    public ResponseException(String message) {
        super(message);
    }
}
