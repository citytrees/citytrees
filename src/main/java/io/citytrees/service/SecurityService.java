package io.citytrees.service;

import io.citytrees.configuration.security.JWTUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SecurityService {

    public UUID getCurrentUserId() {
        return ((JWTUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }

}
