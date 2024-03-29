package io.citytrees.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import io.citytrees.configuration.properties.SecurityProperties;
import io.citytrees.configuration.security.JWTUserDetails;
import io.citytrees.model.TokenPair;
import io.citytrees.model.User;
import io.citytrees.v1.model.UserRole;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenService {

    private static final String ISSUER = "citytrees";
    private static final String EMAIL_CLAIM = "email";
    private static final String ROLES_CLAIM = "roles";
    private static final String FIRST_NAME_CLAIM = "firstName";
    private static final String LAST_NAME_CLAIM = "lastName";
    private static final String ROLES_SPLITTER = ",";

    private final SecurityProperties securityProperties;
    private final UserService userService;

    @NotNull
    public TokenPair generateNewPair(User user) {
        return TokenPair.builder()
            .accessToken(generateToken(user, securityProperties.getAccessTokenSecret(), securityProperties.getAccessTokenDuration()))
            .refreshToken(generateToken(user, securityProperties.getRefreshTokenSecret(), securityProperties.getRefreshTokenDuration()))
            .build();
    }

    @NotNull
    public String validateTokenAndExtractEmail(String refreshToken) {
        if (refreshToken == null) {
            throw new JWTVerificationException("Refresh token not present");
        }

        var algorithm = Algorithm.HMAC512(securityProperties.getRefreshTokenSecret());
        var verifier = JWT.require(algorithm)
            .withIssuer(ISSUER)
            .build();

        var decodedJWT = verifier.verify(refreshToken);

        return decodedJWT.getClaim(EMAIL_CLAIM).asString();
    }

    @NotNull
    public JWTUserDetails getUserDetailsFromToken(String token) {
        var algorithm = Algorithm.HMAC512(securityProperties.getAccessTokenSecret());
        var verifier = JWT.require(algorithm)
            .withIssuer(ISSUER)
            .build();

        var decodedJWT = verifier.verify(token);

        var roles = Arrays.stream(decodedJWT.getClaim(ROLES_CLAIM).asString().split(ROLES_SPLITTER))
            .map(UserRole::valueOf)
            .collect(Collectors.toSet());

        UUID id = UUID.fromString(decodedJWT.getSubject());
        if (!userService.isUserExists(id)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return JWTUserDetails.builder()
            .id(id)
            .roles(roles)
            .email(decodedJWT.getClaim(EMAIL_CLAIM).asString())
            .build();
    }

    private String generateToken(User user, String secret, Duration duration) {
        var algorithm = Algorithm.HMAC512(secret);
        var now = Instant.now();
        return JWT.create()
            .withIssuer(ISSUER)
            .withSubject(user.getId().toString())
            .withIssuedAt(now)
            .withExpiresAt(now.plus(duration))
            .withClaim(EMAIL_CLAIM, user.getEmail())
            .withClaim(ROLES_CLAIM, user.getRoles().stream().map(Enum::name).collect(Collectors.joining(ROLES_SPLITTER)))
            .withClaim(FIRST_NAME_CLAIM, user.getFirstName())
            .withClaim(LAST_NAME_CLAIM, user.getLastName())
            .sign(algorithm);
    }
}
