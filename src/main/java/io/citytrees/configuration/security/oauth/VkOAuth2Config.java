package io.citytrees.configuration.security.oauth;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@Value
@Validated
@ConstructorBinding
@ConfigurationProperties(prefix = "oauth2.vk")
public class VkOAuth2Config {

    @NotEmpty
    String clientId;

    @NotEmpty
    String clientSecret;

    @Bean
    public VkApiClient vkApiClient() {
        return new VkApiClient(HttpTransportClient.getInstance());
    }
}
