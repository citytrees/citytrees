package io.citytrees.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import io.citytrees.model.TokenPair;
import io.citytrees.util.HashUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;

import static io.citytrees.constants.CookieNames.ACCESS_TOKEN;
import static io.citytrees.constants.CookieNames.REFRESH_TOKEN;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final int COOKIES_MAX_AGE = (int) Duration.ofDays(365).toSeconds();

    private final UserService userService;
    private final TokenService tokenService;
    private final HashUtil hashUtil;

    public void handleBasicAuth(String authorizationHeaderValue, HttpServletResponse httpServletResponse) {
        var loginAndPassword = new String(Base64Utils.decodeFromUrlSafeString(authorizationHeaderValue.substring(6)))
            .split(":");

        var email = loginAndPassword[0];
        var password = loginAndPassword[1];

        userService.findByEmail(email)
            .filter(user -> user.getPassword().equals(hashUtil.md5WithSalt(password)))
            .ifPresentOrElse(
                user -> setResponseCookies(tokenService.generateNewPair(user), httpServletResponse),
                () -> {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
                });
    }

    public void refreshTokenPair(String refreshToken, HttpServletResponse httpServletResponse) {
        String email;
        try {
            email = tokenService.validateTokenAndExtractEmail(refreshToken);
        } catch (JWTVerificationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage(), e);
        }

        userService.findByEmail(email)
            .ifPresentOrElse(
                user -> setResponseCookies(tokenService.generateNewPair(user), httpServletResponse),
                () -> {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
                }
            );
    }

    private void setResponseCookies(TokenPair tokenPair, HttpServletResponse httpServletResponse) {
        var accessTokenCookie = new Cookie(ACCESS_TOKEN, tokenPair.getAccessToken());
        accessTokenCookie.setMaxAge(COOKIES_MAX_AGE);
        accessTokenCookie.setPath("/");

        var refreshTokenCookie = new Cookie(REFRESH_TOKEN, tokenPair.getRefreshToken());
        refreshTokenCookie.setMaxAge(COOKIES_MAX_AGE);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");

        httpServletResponse.addCookie(accessTokenCookie);
        httpServletResponse.addCookie(refreshTokenCookie);
    }
}
