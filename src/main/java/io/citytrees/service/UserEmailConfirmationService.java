package io.citytrees.service;

import io.citytrees.configuration.properties.ApplicationProperties;
import io.citytrees.configuration.properties.SecurityProperties;
import io.citytrees.model.EmailMessage;
import io.citytrees.model.User;
import io.citytrees.repository.UserRepository;
import io.citytrees.service.exception.UserEmailConfirmationException;
import io.citytrees.util.HashUtil;
import io.citytrees.v1.model.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserEmailConfirmationService {

    private final HashUtil hashUtil;
    private final ApplicationProperties applicationProperties;
    private final SecurityProperties securityProperties;
    private final EmailService emailService;
    private final UserRepository userRepository;

    public void sendConfirmationEmail(User user) {
        EmailMessage message = EmailMessage.builder()
            .address(user.getEmail())
            .subject("Citytress email confirmation")
            .text(generateText(user))
            .build();

        emailService.send(message);
        if (user.getStatus() == UserStatus.NEW) {
            userRepository.updateStatus(user.getId(), UserStatus.TO_BE_APPROVED);
        }
    }

    public String generateConfirmationString(User user) {
        return hashUtil.md5WithSalt(user.getEmail(), securityProperties.getEmailConfirmationSalt());
    }

    public void confirmEmail(UUID userId, String confirmationId) {
        var optionalUser = userRepository.findByUserId(userId);
        if (optionalUser.isEmpty()) {
            throw new UserEmailConfirmationException("Invalid parameters");
        }
        User user = optionalUser.get();

        if (!confirmationId.equals(generateConfirmationString(user))) {
            throw new UserEmailConfirmationException("Invalid parameters");
        }
        userRepository.updateStatus(userId, UserStatus.APPROVED);
    }

    @SneakyThrows
    private String generateText(User user) {
        String confirmationString = generateConfirmationString(user);

        var uri = new URIBuilder(applicationProperties.getBaseUrl());
        uri.setPath("/user/confirm");
        uri.addParameter("userId", user.getId().toString());
        uri.addParameter("confirmationId", confirmationString);

        return String.format("<p>To confirm your email please open the <a href='%s'>link</a></p>", uri);
    }
}
