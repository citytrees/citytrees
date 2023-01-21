package io.citytrees.configuration.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static java.util.Collections.emptyList;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class CacheConfiguration {

    @Bean
    public CacheManager cacheManager(CaffeineCacheSpecificProperties props) {
        List<? extends Cache> caches = props.getCaches() == null
            ? emptyList()
            : props.getCaches()
            .stream()
            .map(definition -> new CaffeineCache(
                definition.getName(),
                Caffeine.from(definition.getSpec()).build())
            )
            .toList();

        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(caches);
        return manager;
    }

}
