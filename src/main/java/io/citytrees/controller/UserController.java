package io.citytrees.controller;

import io.citytrees.service.UserService;
import io.citytrees.v1.controller.UserControllerApiDelegate;
import io.citytrees.v1.model.UserGetById200Response;
import io.citytrees.v1.model.UserRegisterNew200Response;
import io.citytrees.v1.model.UserRegisterRequest;
import io.citytrees.v1.model.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserController extends BaseController implements UserControllerApiDelegate {

    private final UserService service;

    @Override
    public ResponseEntity<UserRegisterNew200Response> userRegisterNew(UserRegisterRequest registerUserRequest) {
        var response = new UserRegisterNew200Response()
            .userId(service.create(registerUserRequest));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserGetById200Response> userGetById(UUID id) {
        var optionalUser = service.getById(id);
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        var user = optionalUser.get();
        var response = new UserGetById200Response()
            .id(user.getId())
            .email(user.getEmail())
            .roles(user.getRoles().stream().toList())
            .firstName(user.getFirstName())
            .lastName(user.getLastName());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> userUpdateById(UUID id, UserUpdateRequest userUpdateRequest) {
        service.update(id, userUpdateRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
