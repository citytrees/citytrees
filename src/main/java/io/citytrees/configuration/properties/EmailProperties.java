package io.citytrees.configuration.properties;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Value
@Validated
@ConstructorBinding
@ConfigurationProperties(prefix = "email")
public class EmailProperties {

    @NotEmpty
    String user;

    @NotEmpty
    String senderEmail;

    @NotEmpty
    String password;

    @NotNull
    Map<String, String> smtpProperties;
}
