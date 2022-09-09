package io.citytrees.repository.jdbctemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.citytrees.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryExtensionImpl implements UserRepositoryExtension {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Optional<User> findByUserId(UUID id) {
        var sql = """
            SELECT *
            FROM ct_user
            WHERE id = :id
            """;

        var params = Map.of("id", id);

        return jdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> {
            try {
                return Optional.of(User.builder()
                    .id(rs.getObject("id", UUID.class))
                    .email(rs.getString("email"))
                    .password(rs.getString("pwd"))
                    .roles(objectMapper.readValue(rs.getString("roles"), new TypeReference<>() {}))
                    .firstName(rs.getString("first_name"))
                    .lastName(rs.getString("last_name"))
                    .build());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
