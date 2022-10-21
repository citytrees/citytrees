package io.citytrees.controller;

import io.citytrees.service.AuthService;
import io.citytrees.v1.controller.AuthControllerApiDelegate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthController extends BaseController implements AuthControllerApiDelegate {

    private final AuthService authService;

    @Override
    public ResponseEntity<Void> handleBasicAuth(String authorization) {
        authService.handleBasicAuth(authorization, httpServletResponse);
        return ResponseEntity.ok(null);
    }

    @Override
    public ResponseEntity<Void> refreshTokenPair(String refreshToken) {
        authService.refreshTokenPair(refreshToken, httpServletResponse);
        return ResponseEntity.ok(null);
    }
}
