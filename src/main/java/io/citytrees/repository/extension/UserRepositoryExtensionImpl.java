package io.citytrees.repository.extension;

import io.citytrees.model.User;
import io.citytrees.repository.extension.rowmapper.UserRowMapper;
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
    private final UserRowMapper userRowMapper;

    @Override
    public Optional<User> findByUserId(UUID id) {
        var sql = """
            SELECT *
            FROM ct_user
            WHERE id = :id
            """;

        var params = Map.of("id", id);

        return jdbcTemplate.query(sql, params, userRowMapper).stream().findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        var sql = """
            SELECT *
            FROM ct_user
            WHERE email = :email
            """;

        var params = Map.of("email", email);

        return jdbcTemplate.query(sql, params, userRowMapper).stream().findFirst();
    }
}
