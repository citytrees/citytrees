package io.citytrees.controller;

import io.citytrees.dto.OAuth2Props;
import io.citytrees.service.AuthService;
import io.citytrees.service.OAuth2ProviderRegistry;
import io.citytrees.service.oauth2.OAuth2Service;
import io.citytrees.v1.controller.AuthControllerApiDelegate;
import io.citytrees.v1.model.AuthGetAllProviderResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthController extends BaseController implements AuthControllerApiDelegate {

    private final AuthService authService;
    private final OAuth2ProviderRegistry oAuth2ProviderRegistry;

    @Override
    @PreAuthorize("permitAll()")
    public ResponseEntity<Void> handleBasicAuth(String authorization) {
        authService.handleBasicAuth(authorization, httpServletResponse);
        return ResponseEntity.ok(null);
    }

    @Override
    @PreAuthorize("permitAll()")
    public ResponseEntity<Void> refreshTokenPair(String refreshToken) {
        authService.refreshTokenPair(refreshToken, httpServletResponse);
        return ResponseEntity.ok(null);
    }

    @Override
    public ResponseEntity<Void> handleLogout(String ctRefreshToken) {
        authService.logout(httpServletResponse);
        return ResponseEntity.ok(null);
    }

    @Override
    @SneakyThrows
    public ResponseEntity<Void> handle0Auth2(String providerId) {
        var provider = oAuth2ProviderRegistry.getByProviderId(providerId);
        httpServletResponse.sendRedirect(provider.getOAuthUri());
        return ResponseEntity.status(HttpStatus.FOUND).build();
    }

    @Override
    public ResponseEntity<Void> handleCallback(String providerId, String authorizationCode) {
        var provider = oAuth2ProviderRegistry.getByProviderId(providerId);
        provider.handleOAuthFlow(authorizationCode, httpServletResponse);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<AuthGetAllProviderResponse> getAllProviders() {
        var items = oAuth2ProviderRegistry.getAll().stream()
            .map(OAuth2Service::getProps)
            .map(OAuth2Props::toResponse)
            .toList();

        return ResponseEntity.ok(new AuthGetAllProviderResponse().items(items));
    }
}
