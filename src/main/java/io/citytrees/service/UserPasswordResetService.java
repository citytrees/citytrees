package io.citytrees.service;

import io.citytrees.configuration.properties.ApplicationProperties;
import io.citytrees.model.EmailMessage;
import io.citytrees.model.User;
import io.citytrees.model.UserPasswordResetToken;
import io.citytrees.repository.UserPasswordResetRepository;
import io.citytrees.repository.UserRepository;
import io.citytrees.service.exception.UserPasswordResetException;
import io.citytrees.v1.model.UserPasswordResetStatus;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserPasswordResetService {

    private final ApplicationProperties applicationProperties;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final UserPasswordResetRepository userPasswordResetRepository;
    private final UserService userService;

    public void requestReset(String userEmail) {
        var optionalUser = userRepository.findByEmail(userEmail);
        if (optionalUser.isEmpty()) {
            return;
        }
        User user = optionalUser.get();
        userPasswordResetRepository.upsert(user.getId(), user.getEmail(), UUID.randomUUID(), UserPasswordResetStatus.NEW);
    }

    @Transactional
    public void reset(String userEmail, String token, String newPassword) {
        var optionalUser = userRepository.findByEmail(userEmail);
        if (optionalUser.isEmpty()) {
            throw new UserPasswordResetException("Password reset request not found");
        }
        User user = optionalUser.get();

        Optional<UserPasswordResetToken> optionalToken = userPasswordResetRepository.findById(user.getId());
        if (optionalToken.isEmpty()) {
            throw new UserPasswordResetException("Invalid token");
        }
        UserPasswordResetToken resetToken = optionalToken.get();
        if (!resetToken.getToken().equals(token)) {
            throw new UserPasswordResetException("Invalid token");
        }

        userService.updatePassword(user.getId(), newPassword);
        userPasswordResetRepository.deleteById(user.getId());
    }

    public void sendPasswordResetEmail(UserPasswordResetToken resetToken) {
        EmailMessage message = EmailMessage.builder()
            .address(resetToken.getEmail())
            .subject("Citytress email confirmation")
            .text(generateText(resetToken))
            .build();

        emailService.send(message);
        if (resetToken.getStatus() == UserPasswordResetStatus.NEW) {
            userPasswordResetRepository.updateStatus(resetToken.getUserId(), UserPasswordResetStatus.SENT);
        }
    }

    @SneakyThrows
    private String generateText(UserPasswordResetToken userPasswordResetToken) {
        var uri = new URIBuilder(applicationProperties.getBaseUrl());
        uri.setPath("/password/reset");
        uri.addParameter("userEmail", userPasswordResetToken.getEmail());
        uri.addParameter("resetId", userPasswordResetToken.getToken());

        return String.format("<p>To reset password please open the <a href='%s'>link</a></p>", uri);
    }
}
