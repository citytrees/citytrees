package io.citytrees.configuration.sceduler;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@Configuration
@ConditionalOnProperty(name = "scheduling.enabled", havingValue = "true")
public class SchedulingConfiguration {
}
