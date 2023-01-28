package io.citytrees.model;

import io.citytrees.constants.TableNames;
import io.citytrees.v1.model.UserRole;
import io.citytrees.v1.model.UserStatus;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Value
@Builder
@Table(TableNames.USER_TABLE)
public class User {

    @Id
    @NotNull
    UUID id;

    @NotNull
    Set<UserRole> roles;

    @NotNull
    UserStatus status;

    @NotNull
    LocalDateTime creationDateTime;

    @NotNull
    List<AuthProviderMeta> authProviderMeta;

    @Nullable
    @Column("pwd")
    String password;

    @Nullable
    String email;

    @Nullable
    String firstName;

    @Nullable
    String lastName;

    @Value
    @Builder
    public static class AuthProviderMeta {
        String id;
        Map<String, Object> params;
    }
}
