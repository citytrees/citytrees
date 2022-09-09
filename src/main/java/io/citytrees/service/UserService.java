package io.citytrees.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.citytrees.model.User;
import io.citytrees.repository.UserRepository;
import io.citytrees.util.HashUtil;
import io.citytrees.v1.model.RegisterUserRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final HashUtil hashUtil;
    private final ObjectMapper objectMapper;

    public UUID create(RegisterUserRequest request) {
        var hashedPassword = hashUtil.md5WithSalt(request.getPassword());
        return create(User.builder()
            .id(UUID.randomUUID())
            .email(request.getEmail())
            .password(hashedPassword)
            .build());
    }

    @SneakyThrows
    public UUID create(User user) {
        var roles = objectMapper.writeValueAsString(user.getRoles());
        return userRepository.create(user.getId(), user.getEmail(), user.getPassword(), roles, user.getFirstName(), user.getLastName());
    }
}
