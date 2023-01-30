package io.citytrees.configuration.security;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import org.springframework.context.annotation.Bean;

public class OAuth2Config {

    @Bean
    public VkApiClient vkApiClient() {
        return new VkApiClient(HttpTransportClient.getInstance());
    }

    public String getClientId(String providerId) {
        return "";
    }

    public String getClientSecret(String providerId) {
        return "";
    }
}
