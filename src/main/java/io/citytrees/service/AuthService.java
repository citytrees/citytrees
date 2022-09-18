package io.citytrees.service;

import io.citytrees.model.User;
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
                user -> setResponseCookies(user, httpServletResponse),
                () -> {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
                });
    }

    private void setResponseCookies(User user, HttpServletResponse httpServletResponse) {
        var tokenPair = tokenService.generateNewPair(user);

        var accessTokenCookie = new Cookie(ACCESS_TOKEN, tokenPair.getAccessToken());
        accessTokenCookie.setMaxAge(COOKIES_MAX_AGE);

        var refreshTokenCookie = new Cookie(REFRESH_TOKEN, tokenPair.getRefreshToken());
        refreshTokenCookie.setMaxAge(COOKIES_MAX_AGE);
        refreshTokenCookie.setHttpOnly(true);

        httpServletResponse.addCookie(accessTokenCookie);
        httpServletResponse.addCookie(refreshTokenCookie);
    }
}
