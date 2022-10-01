package io.citytrees.configuration.security.evaluators;

import io.citytrees.configuration.security.constants.Domains;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component(Domains.USER + MainPermissionEvaluator.PERMISSION_EVALUATOR)
@RequiredArgsConstructor
public class UserPermissionEvaluator implements DomainPermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication authentication, Long targetId, String permission) {
        return false;
    }

}
