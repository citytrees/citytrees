package io.citytrees.configuration.security.evaluators;

import io.citytrees.configuration.security.JWTUserDetails;
import io.citytrees.configuration.security.constants.Domains;
import io.citytrees.configuration.security.constants.Permissions;
import kotlin.NotImplementedError;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component(Domains.USER + MainPermissionEvaluator.PERMISSION_EVALUATOR)
@RequiredArgsConstructor
public class UserPermissionEvaluator implements DomainPermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication authentication, UUID targetId, String permission) {
        return switch (permission) {
            case Permissions.EDIT -> ((JWTUserDetails) authentication.getPrincipal()).getId().equals(targetId);
            default -> throw new NotImplementedError("Permission check is not yet implemented for that permission: " + permission);
        };
    }

}
