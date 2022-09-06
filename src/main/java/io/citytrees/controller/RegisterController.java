package io.citytrees.controller;

import io.citytrees.service.UserService;
import io.citytrees.v1.controller.RegisterControllerApiDelegate;
import io.citytrees.v1.model.RegisterNewUser200Response;
import io.citytrees.v1.model.RegisterUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegisterController extends BaseController implements RegisterControllerApiDelegate {

    private final UserService service;

    @Override
    public ResponseEntity<RegisterNewUser200Response> registerNewUser(RegisterUserRequest registerUserRequest) {
        var response = new RegisterNewUser200Response()
            .userId(service.create(registerUserRequest));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
