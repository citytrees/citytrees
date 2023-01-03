package io.citytrees.service;

import io.citytrees.configuration.properties.SecurityProperties;
import io.citytrees.model.User;
import io.citytrees.v1.model.UserRole;
import io.citytrees.v1.model.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StartupService {
    private final SecurityProperties securityProperties;
    private final UserService userService;

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent() {
        createAdmin();
    }

    private void createAdmin() {
        userService.createIfNotExists(
            User.builder()
                .id(UUID.randomUUID())
                .email(securityProperties.getAdminEmail())
                .password(securityProperties.getAdminPassword())
                .status(UserStatus.APPROVED)
                .roles(Set.of(UserRole.ADMIN))
                .build()
        );
    }
}
