package io.citytrees.service.exception;

public class UserPasswordResetException extends BaseUserInputError {
    public UserPasswordResetException(String message) {
        super(message);
    }
}
