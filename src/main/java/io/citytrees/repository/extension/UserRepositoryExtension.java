package io.citytrees.repository.extension;

import io.citytrees.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryExtension {
    Optional<User> findByUserId(UUID id);

    Optional<User> findByEmail(String email);
}
