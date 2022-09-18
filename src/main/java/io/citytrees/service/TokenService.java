package io.citytrees.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.citytrees.configuration.properties.SecurityProperties;
import io.citytrees.model.TokenPair;
import io.citytrees.model.User;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenService {

    private static final String ISSUER = "citytrees";

    private final SecurityProperties securityProperties;

    @NotNull
    public TokenPair generateNewPair(User user) {
        return TokenPair.builder()
            .accessToken(generateToken(user, securityProperties.getAccessTokenSecret(), securityProperties.getAccessTokenDuration()))
            .refreshToken(generateToken(user, securityProperties.getRefreshTokenSecret(), securityProperties.getRefreshTokenDuration()))
            .build();
    }

    private String generateToken(User user, String secret, Duration duration) {
        var algorithm = Algorithm.HMAC512(secret);
        var now = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        return JWT.create()
            .withIssuer(ISSUER)
            .withSubject(user.getId().toString())
            .withIssuedAt(now)
            .withExpiresAt(now.plus(duration))
            .withClaim("email", user.getEmail())
            .withClaim("roles", user.getRoles().stream().map(Enum::name).collect(Collectors.joining(",")))
            .withClaim("firstName", user.getFirstName())
            .withClaim("lastName", user.getLastName())
            .sign(algorithm);
    }
}
