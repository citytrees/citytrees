package io.citytrees.repository;

import io.citytrees.model.User;
import io.citytrees.repository.extension.UserRepositoryExtension;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID>, UserRepositoryExtension {

    @SuppressWarnings("ParameterNumber")
    @Query("""
        INSERT INTO ct_user(id, email, pwd, roles, first_name, last_name)
        VALUES (:id, :email, :pwd, :roles::jsonb, :firstName, :lastName)
        RETURNING id
        """)
    UUID create(UUID id, String email, String pwd, String roles, String firstName, String lastName);
}
