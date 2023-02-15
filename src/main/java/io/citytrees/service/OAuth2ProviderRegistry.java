package io.citytrees.service;

import io.citytrees.service.oauth2.OAuth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuth2ProviderRegistry {
    private final Map<String, OAuth2Service> serviceByProvider;

    public OAuth2Service getByProviderId(String providerId) {
        String serviceClassName = OAuth2Service.class.getSimpleName();
        OAuth2Service oAuth2Service = serviceByProvider.get(providerId + serviceClassName);

        if (oAuth2Service == null) {
            throw new IllegalArgumentException(serviceClassName + " by providerId=" + providerId + " not found");
        }

        return oAuth2Service;
    }

    public Collection<OAuth2Service> getAll() {
        return serviceByProvider.values();
    }
}
