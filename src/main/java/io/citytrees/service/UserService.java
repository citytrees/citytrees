package io.citytrees.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.citytrees.configuration.security.JWTUserDetails;
import io.citytrees.model.User;
import io.citytrees.repository.UserRepository;
import io.citytrees.service.exception.UserInputError;
import io.citytrees.util.HashUtil;
import io.citytrees.v1.model.UserRegisterRequest;
import io.citytrees.v1.model.UserRole;
import io.citytrees.v1.model.UserStatus;
import io.citytrees.v1.model.UserUpdatePasswordRequest;
import io.citytrees.v1.model.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final HashUtil hashUtil;
    private final ObjectMapper objectMapper;
    private final SecurityService securityService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getByEmail(username)
            .map(user -> JWTUserDetails.builder()
                .id(user.getId())
                .email(user.getEmail())
                .roles(user.getRoles())
                .build())
            .orElseThrow();
    }

    public UUID create(UserRegisterRequest request) {
        return create(UUID.randomUUID(),
            request.getEmail(),
            request.getPassword(),
            UserStatus.NEW,
            Set.of(UserRole.BASIC),
            null,
            null,
            Collections.emptyList());
    }

    public UUID create(User user) {
        return create(user.getId(), user.getEmail(), user.getPassword(), user.getStatus(), user.getRoles(), user.getFirstName(), user.getLastName(), user.getAuthProviderMeta());
    }

    public UUID createIfNotExists(User user) {
        return getByEmail(user.getEmail()).map(User::getId).orElseGet(() -> create(user));
    }

    public UUID register(UserRegisterRequest registerUserRequest) {
        getByEmail(registerUserRequest.getEmail()).ifPresent(user -> {
            throw new UserInputError(String.format("Email '%s' is already in use", user.getEmail()));
        });

        return create(registerUserRequest);
    }

    // todo #32 make @Cacheable
    public Optional<User> getByEmail(String email) {
        return userRepository.findFirstByEmail(email);
    }

    public Optional<User> getById(UUID id) {
        return userRepository.findFirstById(id);
    }

    public void update(UUID id, UserUpdateRequest request) {
        update(id, request.getFirstName(), request.getLastName());
    }

    public void update(UUID id, String firstName, String lastName) {
        userRepository.update(id, firstName, lastName);
    }

    public void updatePassword(UserUpdatePasswordRequest userUpdatePasswordRequest) {
        updatePassword(securityService.getCurrentUserId(), userUpdatePasswordRequest.getNewPassword());
    }

    public void updatePassword(UUID userId, String newPassword) {
        var hashedPassword = hashUtil.md5WithSalt(newPassword);
        userRepository.updatePassword(userId, hashedPassword);
    }

    @Cacheable("isUserExistsByIdCache")
    public boolean isUserExists(UUID userId) {
        return userRepository.findFirstById(userId).isPresent();
    }

    @SuppressWarnings("checkstyle:ParameterNumber")
    @SneakyThrows
    private UUID create(UUID id, String email, String pwd, UserStatus status, Set<UserRole> roles, String firstName, String lastName, List<User.AuthProviderMeta> authProviderMetaList) {
        var rolesJson = objectMapper.writeValueAsString(roles);
        var hashedPassword = hashUtil.md5WithSalt(pwd);
        var authProviderMetaString = objectMapper.writeValueAsString(authProviderMetaList);
        return userRepository.create(id, email, hashedPassword, status, rolesJson, LocalDateTime.now(), firstName, lastName, authProviderMetaString);
    }

    public User findByAuthProviderIdAndExternalUserId(String providerId, Long externalUserId) {
        return userRepository.findByAuthProviderIdAndExternalUserId(providerId, externalUserId);
    }

    public List<User> listAll(Integer limit, LocalDateTime cursorPosition) {
        return userRepository.findAll(limit, cursorPosition != null ? cursorPosition : LocalDateTime.MAX);
    }

    public void updateStatus(UUID id, UserStatus status) {
        if (securityService.getCurrentUserId() == id) {
            throw new UserInputError("Current user's can't be updated");
        }
        userRepository.updateStatus(id, status);
    }
}
