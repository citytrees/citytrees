package io.citytrees.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.citytrees.model.Tree;
import io.citytrees.model.User;
import io.citytrees.repository.extension.rowmapper.TreeRowMapper;
import io.citytrees.repository.extension.rowmapper.UserRowMapper;
import io.citytrees.service.GeometryService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.QueryMappingConfiguration;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.DefaultQueryMappingConfiguration;

@Configuration
@RequiredArgsConstructor
public class JdbcConfiguration extends AbstractJdbcConfiguration {

    private final ObjectMapper objectMapper;
    private final GeometryService geometryService;

    @Bean
    QueryMappingConfiguration rowMappers() {
        return new DefaultQueryMappingConfiguration()
            .registerRowMapper(Tree.class, new TreeRowMapper(geometryService, objectMapper))
            .registerRowMapper(User.class, new UserRowMapper(objectMapper));
    }
}
