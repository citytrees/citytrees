package io.citytrees.configuration.properties;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@Value
@Validated
@ConstructorBinding
@ConfigurationProperties(prefix = "scheduling")
public class SchedulingProperties {

    Boolean enabled;
    UserProperties user;

    @Value
    @Validated
    @ConstructorBinding
    public static class UserProperties {
        String confirmationEmailDelay;
    }
}
