package io.citytrees.model;

import io.citytrees.constants.TableNames;
import io.citytrees.v1.model.UserPasswordResetStatus;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Value
@Builder
@Table(TableNames.USER_PASSWORD_RESET_TABLE)
public class UserPasswordResetToken {

    @Id
    @NotNull
    UUID userId;

    @NotNull
    String email;

    @NotNull
    String token;

    @NotNull
    UserPasswordResetStatus status;
}
