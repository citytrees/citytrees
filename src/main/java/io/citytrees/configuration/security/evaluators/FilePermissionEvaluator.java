package io.citytrees.configuration.security.evaluators;

import io.citytrees.configuration.security.constants.Domains;
import io.citytrees.configuration.security.constants.Permissions;
import io.citytrees.service.FileService;
import io.citytrees.service.SecurityService;
import kotlin.NotImplementedError;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component(Domains.FILE + MainPermissionEvaluator.PERMISSION_EVALUATOR)
@RequiredArgsConstructor
public class FilePermissionEvaluator implements DomainPermissionEvaluator {
    private final FileService fileService;
    private final SecurityService securityService;

    @Override
    public boolean hasPermission(Authentication authentication, UUID targetId, String permission) {
        return switch (permission) {
            case Permissions.DELETE -> {
                UUID userId = securityService.getCurrentUserId();
                yield fileService.getById(targetId).orElseThrow().getUserId().equals(userId);
            }
            default -> throw new NotImplementedError("Permission check is not yet implemented for that permission: " + permission);
        };
    }
}
