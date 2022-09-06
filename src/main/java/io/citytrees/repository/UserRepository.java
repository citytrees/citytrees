package io.citytrees.repository;

import io.citytrees.model.User;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {

    @SuppressWarnings("ParameterNumber")
    @Query("""
        INSERT INTO ct_user(id, email, pwd, roles, first_name, last_name)
        VALUES (:id, :email, :pwd, :roles, :firstName, :lastName)
        RETURNING id
        """)
    UUID create(UUID id, String email, String pwd, User.Role roles, String firstName, String lastName);

    default UUID create(User user) {
        return create(user.getId(), user.getEmail(), user.getPassword(), user.getRoles(), user.getFirstName(), user.getLastName());
    }
}
