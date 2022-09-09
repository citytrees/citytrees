package io.citytrees.model;

import lombok.Builder;
import lombok.Value;
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
    UUID id;

    String email;

    @Column("pwd")
    String password;

    @Builder.Default
    Set<Role> roles = Set.of(Role.VOLUNTEER);

    String firstName;

    String lastName;

    public enum Role {
        VOLUNTEER,
        MODERATOR,
        SUPERUSER,
    }
}
