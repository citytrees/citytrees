package io.citytrees.configuration.security.constants;

import io.citytrees.model.User;
import org.springframework.stereotype.Component;

@Component("Roles")
public final class Roles {
    public static final String ADMIN = User.Role.SUPERUSER.name();
    public static final String BASIC = User.Role.VOLUNTEER.name();
}
