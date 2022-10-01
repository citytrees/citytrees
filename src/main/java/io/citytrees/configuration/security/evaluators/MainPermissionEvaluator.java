package io.citytrees.configuration.security.evaluators;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MainPermissionEvaluator implements PermissionEvaluator {

    public static final String PERMISSION_EVALUATOR = "PermissionEvaluator";

    private final Map<String, DomainPermissionEvaluator> permissionEvaluators;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        throw new IllegalArgumentException("Вызов hasPermission с передачей объекта не поддерживается");
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        DomainPermissionEvaluator domainPermissionEvaluator = permissionEvaluators.get(targetType + PERMISSION_EVALUATOR);
        if (domainPermissionEvaluator == null) {
            throw new IllegalArgumentException("Необходимо добавить новый DomainPermissionEvaluator для этого домена");
        }
        return domainPermissionEvaluator.hasPermission(
            authentication, Long.valueOf(targetId.toString()), permission.toString()
        );
    }
}
