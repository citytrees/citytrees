package io.citytrees.configuration.security.evaluators;

import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface DomainPermissionEvaluator {
    default boolean hasPermission(Authentication authentication, UUID targetId, String permission) {
        return false;
    }

    default boolean hasPermission(Authentication authentication, Long targetId, String permission) {
        return false;
    }
}
