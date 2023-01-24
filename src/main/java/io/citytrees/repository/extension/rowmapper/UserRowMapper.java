package io.citytrees.repository.extension.rowmapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.citytrees.model.User;
import io.citytrees.v1.model.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRowMapper implements RowMapper<User> {

    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public User mapRow(ResultSet rs, int rowNum) {
        return User.builder()
            .id(rs.getObject("id", UUID.class))
            .email(rs.getString("email"))
            .password(rs.getString("pwd"))
            .status(UserStatus.valueOf(rs.getString("status")))
            .roles(objectMapper.readValue(rs.getString("roles"), new TypeReference<>() {}))
            .creationDateTime(rs.getObject("creation_date_time", LocalDateTime.class))
            .firstName(rs.getString("first_name"))
            .lastName(rs.getString("last_name"))
            .build();
    }
}
