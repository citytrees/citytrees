package io.citytrees.controller;

import io.citytrees.repository.UserPasswordResetRepository;
import io.citytrees.repository.UserRepository;
import io.citytrees.service.UserEmailConfirmationService;
import io.citytrees.service.UserPasswordResetService;
import io.citytrees.v1.model.UserPasswordResetStatus;
import io.citytrees.v1.model.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserScheduler {

    private final UserRepository userRepository;
    private final UserPasswordResetRepository userPasswordResetRepository;
    private final UserEmailConfirmationService userEmailConfirmationService;
    private final UserPasswordResetService userPasswordResetService;

    @Scheduled(fixedDelayString = "${scheduling.user.confirmation-email-delay}")
    public void sendConfirmationEmail() {
        userRepository
            .findByStatus(UserStatus.NEW, 100)
            .forEach(userEmailConfirmationService::sendConfirmationEmail);
    }

    @Scheduled(fixedDelayString = "${scheduling.user.password-reset-delay}")
    public void sendPasswordResetEmail() {
        userPasswordResetRepository
            .findByStatus(UserPasswordResetStatus.NEW, Pageable.ofSize(100))
            .forEach(userPasswordResetService::sendPasswordResetEmail);
    }
}
