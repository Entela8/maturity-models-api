package com.maturity.models.api.exception;

public class InvalidGitlabAccessTokenException extends RuntimeException {
    public InvalidGitlabAccessTokenException(String message) {
        super(message);
    }
}
