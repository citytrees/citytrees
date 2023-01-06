package io.citytrees.repository.extension;

import io.citytrees.model.User;
import io.citytrees.repository.extension.rowmapper.UserRowMapper;
import io.citytrees.v1.model.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    @Override
    public List<User> findByStatus(UserStatus status, int limit) {
        var sql = """
            SELECT *
            FROM ct_user
            WHERE status = :status
            LIMIT :limit
            """;

        var params = Map.of(
            "status", status.name(),
            "limit", limit
        );

        return jdbcTemplate.query(sql, params, userRowMapper);
    }
}
