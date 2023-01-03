package io.citytrees.service;

import io.citytrees.configuration.properties.ApplicationProperties;
import io.citytrees.configuration.properties.SecurityProperties;
import io.citytrees.model.EmailMessage;
import io.citytrees.model.User;
import io.citytrees.repository.UserRepository;
import io.citytrees.util.HashUtil;
import io.citytrees.v1.model.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserEmailConfirmationService {

    private final HashUtil hashUtil;
    private final ApplicationProperties applicationProperties;
    private final SecurityProperties securityProperties;
    private final EmailService emailService;
    private final UserRepository userRepository;

    @Value("${openapi.citytreesPublic.base-path:/api/v1}")
    private String apiPrefix;

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

    private String generateText(User user) {
        String confirmationString = hashUtil.md5WithSalt(user.getId().toString(), securityProperties.getEmailConfirmationSalt());
        String link = applicationProperties.getBaseUrl() + apiPrefix + "/user/" + user.getId() + "/confirm?confirmationId=" + confirmationString;

        return String.format("<p>To confirm your email please open the <a href='%s'>link</a></p>", link);
    }
}
