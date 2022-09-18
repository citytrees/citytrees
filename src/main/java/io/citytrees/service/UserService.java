package io.citytrees.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.citytrees.model.User;
import io.citytrees.repository.UserRepository;
import io.citytrees.util.HashUtil;
import io.citytrees.v1.model.RegisterUserRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final HashUtil hashUtil;
    private final ObjectMapper objectMapper;

    public UUID create(RegisterUserRequest request) {
        return create(UUID.randomUUID(),
            request.getEmail(),
            request.getPassword(),
            Set.of(User.Role.VOLUNTEER),
            null,
            null);
    }

    public UUID create(User user) {
        return create(user.getId(), user.getEmail(), user.getPassword(), user.getRoles(), user.getFirstName(), user.getLastName());
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /* TO BE USED ONLY IN TESTS! */
    @Deprecated
    public void drop(UUID id) {
        userRepository.deleteById(id);
    }

    @SneakyThrows
    @SuppressWarnings("ParameterNumber")
    private UUID create(UUID id, String email, String pwd, Set<User.Role> roles, String firstName, String lastName) {
        var rolesJson = objectMapper.writeValueAsString(roles);
        var hashedPassword = hashUtil.md5WithSalt(pwd);
        return userRepository.create(id, email, hashedPassword, rolesJson, firstName, lastName);
    }
}
