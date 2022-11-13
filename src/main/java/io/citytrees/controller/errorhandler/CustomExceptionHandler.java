package io.citytrees.controller.errorhandler;

import io.citytrees.v1.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(Exception exception) {
        ErrorResponse errorResponse = new ErrorResponse();
        if (exception instanceof ResponseStatusException e) {
            errorResponse
                .code(String.valueOf(e.getStatus().value()))
                .message(e.getReason());
            return new ResponseEntity<>(errorResponse, e.getStatus());
        } else {
            errorResponse
                .code(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
