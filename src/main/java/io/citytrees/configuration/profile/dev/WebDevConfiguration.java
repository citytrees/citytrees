package io.citytrees.configuration.profile.dev;

import io.citytrees.controller.interceptor.NotImplementedResponseStatusInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Profile("dev")
@Configuration
@RequiredArgsConstructor
public class WebDevConfiguration implements WebMvcConfigurer {

    private final NotImplementedResponseStatusInterceptor notImplementedResponseStatusInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedHeaders("*")
            .allowedMethods("*")
            .allowedOriginPatterns("*")
            .allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(notImplementedResponseStatusInterceptor);
    }
}
