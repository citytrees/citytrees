package io.citytrees.service;

import io.citytrees.model.User;
import io.citytrees.repository.UserRepository;
import io.citytrees.util.HashUtil;
import io.citytrees.v1.model.RegisterUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final HashUtil hashUtil;

    public UUID create(RegisterUserRequest request) {
        var hashedPassword = hashUtil.md5WithSalt(request.getPassword());
        return userRepository.create(User.builder()
            .id(UUID.randomUUID())
            .email(request.getEmail())
            .password(hashedPassword)
            .build());
    }
}
