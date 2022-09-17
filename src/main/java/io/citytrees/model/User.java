package io.citytrees.model;

import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;
import java.util.UUID;

@Value
@Builder
@Table("ct_user")
public class User {

    @Id
    @NotNull
    UUID id;

    @NotNull
    String email;

    @NotNull
    @Column("pwd")
    String password;

    @NotNull
    Set<Role> roles;

    @Nullable
    String firstName;

    @Nullable
    String lastName;

    public enum Role {
        VOLUNTEER,
        MODERATOR,
        SUPERUSER,
    }
}
