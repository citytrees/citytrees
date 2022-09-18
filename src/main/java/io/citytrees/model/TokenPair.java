package io.citytrees.model;

import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

@Value
@Builder
public class TokenPair {
    @NotNull
    String accessToken;

    @NotNull
    String refreshToken;
}
