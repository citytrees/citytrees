package io.citytrees.repository;

import io.citytrees.model.UserPasswordResetToken;
import io.citytrees.v1.model.UserPasswordResetStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface UserPasswordResetRepository extends CrudRepository<UserPasswordResetToken, UUID> {

    List<UserPasswordResetToken> findByStatus(UserPasswordResetStatus status, Pageable pageable);

    @Modifying
    @Query("""
        INSERT INTO ct_user_password_reset(user_id, email, token, status)
        VALUES (:userId, :email, :token, :status)
        ON CONFLICT(user_id)
        DO UPDATE SET token = :token, status = :status
        """)
    int upsert(UUID userId, String email, UUID token, UserPasswordResetStatus status);

    @Modifying
    @Query("""
        UPDATE ct_user_password_reset
        SET status = :status
        WHERE user_id = :userId
        """)
    int updateStatus(UUID userId, UserPasswordResetStatus status);
}
