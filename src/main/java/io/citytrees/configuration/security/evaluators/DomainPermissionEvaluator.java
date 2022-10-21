package io.citytrees.configuration.security.evaluators;

import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface DomainPermissionEvaluator {
    boolean hasPermission(Authentication authentication, UUID targetId, String permission);
}
