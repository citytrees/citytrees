package io.citytrees.repository;

import io.citytrees.model.User;
import io.citytrees.repository.extension.UserRepositoryExtension;
import io.citytrees.v1.model.UserStatus;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID>, UserRepositoryExtension {

    @SuppressWarnings("checkstyle:ParameterNumber")
    @Query("""
        INSERT INTO ct_user(id, email, pwd, status, roles, first_name, last_name)
        VALUES (:id, :email, :pwd, :status, :roles::jsonb, :firstName, :lastName)
        RETURNING id
        """)
    UUID create(UUID id, String email, String pwd, UserStatus status, String roles, String firstName, String lastName);

    @Modifying
    @Query("""
        UPDATE ct_user
        SET email = :email, first_name = :firstName, last_name = :lastName
        WHERE id = :id
        """)
    int update(UUID id, String email, String firstName, String lastName);

    @Modifying
    @Query("""
        UPDATE ct_user
        SET pwd = :pwd
        WHERE id = :id
        """)
    int updatePassword(UUID id, String pwd);

    @Modifying
    @Query("""
        UPDATE ct_user
        SET status = :status
        WHERE id = :id
        """)
    int updateStatus(UUID id, UserStatus status);
}
