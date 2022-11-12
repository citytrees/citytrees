package io.citytrees.controller;

import io.citytrees.service.UserService;
import io.citytrees.v1.controller.UserControllerApiDelegate;
import io.citytrees.v1.model.UserGetResponse;
import io.citytrees.v1.model.UserRegisterRequest;
import io.citytrees.v1.model.UserRegisterResponse;
import io.citytrees.v1.model.UserUpdatePasswordRequest;
import io.citytrees.v1.model.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserController extends BaseController implements UserControllerApiDelegate {

    private final UserService service;

    @Override
    @PreAuthorize("permitAll()")
    public ResponseEntity<UserRegisterResponse> registerNewUser(UserRegisterRequest registerUserRequest) {
        var response = new UserRegisterResponse()
            .userId(service.create(registerUserRequest));
        return ResponseEntity.ok(response);
    }

    @Override
    @PreAuthorize("permitAll()")
    public ResponseEntity<UserGetResponse> getUserById(UUID id) {
        var optionalUser = service.getById(id);
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        var user = optionalUser.get();
        var response = new UserGetResponse()
            .id(user.getId())
            .email(user.getEmail())
            .roles(user.getRoles().stream().toList())
            .firstName(user.getFirstName())
            .lastName(user.getLastName());
        return ResponseEntity.ok(response);
    }

    @Override
    @PreAuthorize("hasAnyRole(@Roles.ADMIN) || (isAuthenticated() && hasPermission(#id, @Domains.USER, @Permissions.EDIT))")
    public ResponseEntity<Void> updateUserById(UUID id, UserUpdateRequest userUpdateRequest) {
        service.update(id, userUpdateRequest);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> updateUserPassword(UserUpdatePasswordRequest userUpdatePasswordRequest) {
        service.updatePassword(userUpdatePasswordRequest);
        return ResponseEntity.ok().build();
    }
}
