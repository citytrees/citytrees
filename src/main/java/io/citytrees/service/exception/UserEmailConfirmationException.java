package io.citytrees.service.exception;

public class UserEmailConfirmationException extends BaseUserInputError {
    public UserEmailConfirmationException(String message) {
        super(message);
    }
}
