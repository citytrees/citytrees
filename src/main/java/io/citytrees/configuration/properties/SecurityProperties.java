package io.citytrees.configuration.properties;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Duration;

@Value
@Validated
@ConstructorBinding
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {
    /**
     * Salt that is used in passwords.
     */
    @NotEmpty
    String passwordSalt;

    /**
     * Secret for generating and validating access tokens
     */
    @NotEmpty
    String accessTokenSecret;

    /**
     * Secret for generating and validating refresh tokens
     */
    @NotEmpty
    String refreshTokenSecret;

    /**
     * Validity duration of access tokens
     */
    @NotNull
    Duration accessTokenDuration;

    /**
     * Validity duration of refresh tokens
     */
    @NotNull
    Duration refreshTokenDuration;

    @NotNull
    String adminEmail;

    @NotNull
    String adminPassword;
}
