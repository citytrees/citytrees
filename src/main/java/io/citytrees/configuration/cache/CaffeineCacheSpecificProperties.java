package io.citytrees.configuration.cache;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.List;

@Getter
@RequiredArgsConstructor
@ConstructorBinding
@ConfigurationProperties(prefix = "caffeine")
public class CaffeineCacheSpecificProperties {
    private final List<CaffeineCacheDefinition> caches;

    @Getter
    @RequiredArgsConstructor
    public static class CaffeineCacheDefinition {
        private final String name;
        private final String spec;
    }
}
