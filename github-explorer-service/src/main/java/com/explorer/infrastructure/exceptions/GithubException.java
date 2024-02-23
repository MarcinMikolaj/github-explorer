package com.explorer.infrastructure.exceptions;

public class GithubException extends RuntimeException {
    public GithubException() {super();}
    public GithubException(String message) {
        super(message);
    }
}
