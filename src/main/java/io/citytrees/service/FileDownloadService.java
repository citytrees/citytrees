package io.citytrees.service;

import io.citytrees.configuration.properties.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileDownloadService {

    private final ApplicationProperties applicationProperties;

    @Value("${openapi.citytreesPublic.base-path:/api/v1}")
    private String apiPrefix;

    public String generateDownloadUrl(UUID id) {
        // TODO #18 remove getBaseUrl()
        return applicationProperties.getBaseUrl() + apiPrefix + "/file/download/" + id;
    }
}
