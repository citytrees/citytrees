package io.citytrees.repository.extension;

import io.citytrees.model.User;
import io.citytrees.v1.model.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryExtension {
    Optional<User> findByUserId(UUID id);

    Optional<User> findByEmail(String email);

    List<User> findByStatus(UserStatus status, int limit);
}
