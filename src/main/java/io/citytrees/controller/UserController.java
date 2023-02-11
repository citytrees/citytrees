package io.citytrees.controller;

import io.citytrees.service.UserEmailConfirmationService;
import io.citytrees.service.UserPasswordResetService;
import io.citytrees.service.UserService;
import io.citytrees.v1.controller.UserControllerApiDelegate;
import io.citytrees.v1.model.UserEmailConfirmRequest;
import io.citytrees.v1.model.UserGetAllResponse;
import io.citytrees.v1.model.UserGetResponse;
import io.citytrees.v1.model.UserPasswordResetRequest;
import io.citytrees.v1.model.UserRegisterRequest;
import io.citytrees.v1.model.UserRegisterResponse;
import io.citytrees.v1.model.UserRequestPasswordResetRequest;
import io.citytrees.v1.model.UserStatus;
import io.citytrees.v1.model.UserUpdatePasswordRequest;
import io.citytrees.v1.model.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserController extends BaseController implements UserControllerApiDelegate {

    private final UserService userService;
    private final UserEmailConfirmationService userEmailConfirmationService;
    private final UserPasswordResetService userPasswordResetService;

    @Override
    @PreAuthorize("permitAll()")
    public ResponseEntity<UserRegisterResponse> registerNewUser(UserRegisterRequest registerUserRequest) {
        var response = new UserRegisterResponse()
            .userId(userService.register(registerUserRequest));
        return ResponseEntity.ok(response);
    }

    @Override
    @PreAuthorize("permitAll()")
    public ResponseEntity<UserGetResponse> getUserById(UUID id) {
        var optionalUser = userService.getById(id);
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
    @PreAuthorize("hasAuthority(@Roles.ADMIN) || (isAuthenticated() && hasPermission(#id, @Domains.USER, @Permissions.EDIT))")
    public ResponseEntity<Void> updateUserById(UUID id, UserUpdateRequest userUpdateRequest) {
        userService.update(id, userUpdateRequest);
        return ResponseEntity.ok().build();
    }

    @Override
    @PreAuthorize("isAuthenticated() && hasPermission(@securityService.currentUserId, @Domains.USER, @Permissions.EDIT)")
    public ResponseEntity<Void> updateUserPassword(UserUpdatePasswordRequest userUpdatePasswordRequest) {
        userService.updatePassword(userUpdatePasswordRequest);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> confirmUserEmail(UserEmailConfirmRequest userEmailConfirmRequest) {
        userEmailConfirmationService.confirmEmail(userEmailConfirmRequest.getUserId(), userEmailConfirmRequest.getConfirmationId());
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> requestPasswordReset(UserRequestPasswordResetRequest userRequestPasswordResetRequest) {
        userPasswordResetService.requestReset(userRequestPasswordResetRequest.getEmail());
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> resetPassword(UserPasswordResetRequest userPasswordResetRequest) {
        userPasswordResetService.reset(userPasswordResetRequest.getEmail(), userPasswordResetRequest.getToken(), userPasswordResetRequest.getNewPassword());
        return ResponseEntity.ok().build();
    }

    // todo #32 fix cursor
    @Override
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<UserGetAllResponse>> getAllUsers(Integer limit, OffsetDateTime cursorPosition) {
        var response = userService.listAll(limit, cursorPosition != null ? cursorPosition.toLocalDateTime() : null).stream()
            .map(user -> new UserGetAllResponse()
                .id(user.getId())
                .email(user.getEmail())
                .roles(user.getRoles().stream().toList())
                .status(user.getStatus())
                .creationDate(user.getCreationDateTime().atOffset(ZoneOffset.UTC))
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
            )
            .toList();

        return ResponseEntity.ok(response);
    }

    @Override
    @PreAuthorize("hasAuthority(@Roles.ADMIN)")
    public ResponseEntity<Void> restore(UUID id) {
        userService.updateStatus(id, UserStatus.NEW);
        return ResponseEntity.ok().build();
    }

    @Override
    @PreAuthorize("hasAuthority(@Roles.ADMIN)")
    public ResponseEntity<Void> ban(UUID id) {
        userService.updateStatus(id, UserStatus.BANNED);
        return ResponseEntity.ok().build();
    }
}
