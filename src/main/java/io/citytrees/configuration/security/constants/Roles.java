package io.citytrees.configuration.security.constants;

import io.citytrees.v1.model.UserRole;
import org.springframework.stereotype.Component;

@Component("Roles")
public final class Roles {
    public static final String ADMIN = UserRole.ADMIN.name();
    public static final String MODERATOR = UserRole.MODERATOR.name();
    public static final String BASIC = UserRole.BASIC.name();
}
