package io.citytrees.repository.jdbctemplate;

import io.citytrees.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryExtension {
    Optional<User> findByUserId(UUID id);
}
