package io.citytrees.configuration;

import io.citytrees.configuration.converter.AbstractReadingEnumConverter;
import io.citytrees.configuration.converter.AbstractWritingEnumConverter;
import io.citytrees.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class JdbcConfiguration extends AbstractJdbcConfiguration {
    @Override
    protected List<?> userConverters() {
        return List.of(
            new AbstractReadingEnumConverter<>(User.Role.class) { },
            new AbstractWritingEnumConverter<User.Role>() { }
        );
    }
}
