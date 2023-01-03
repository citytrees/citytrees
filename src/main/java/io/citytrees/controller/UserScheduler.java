package io.citytrees.controller;

import io.citytrees.repository.UserRepository;
import io.citytrees.service.UserEmailConfirmationService;
import io.citytrees.v1.model.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserScheduler {

    private final UserRepository userRepository;
    private final UserEmailConfirmationService userEmailConfirmationService;

    @Scheduled(fixedDelayString = "${scheduling.user.confirmation-email-delay}")
    public void sendConfirmationEmail() {
        userRepository
            .findByStatus(UserStatus.NEW, 100)
            .forEach(userEmailConfirmationService::sendConfirmationEmail);
    }
}
