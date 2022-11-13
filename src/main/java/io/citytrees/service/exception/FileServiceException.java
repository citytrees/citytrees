package io.citytrees.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FileServiceException extends RuntimeException {
    public FileServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
