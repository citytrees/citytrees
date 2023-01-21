package io.citytrees.repository

import io.citytrees.constants.TableNames.*
import io.citytrees.model.UserPasswordResetToken
import io.citytrees.v1.model.UserPasswordResetStatus
import org.springframework.data.domain.Pageable
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import java.util.*

interface UserPasswordResetRepository : CrudRepository<UserPasswordResetToken, UUID> {

    fun findByStatus(status: UserPasswordResetStatus, pageable: Pageable): List<UserPasswordResetToken>

    @Modifying
    @Query(
        """
        INSERT INTO $USER_PASSWORD_RESET_TABLE(user_id, email, token, status)
        VALUES (:userId, :email, :token, :status)
        ON CONFLICT(user_id)
        DO UPDATE SET token = :token, status = :status
        """
    )
    fun upsert(userId: UUID, email: String, token: UUID, status: UserPasswordResetStatus): Int

    @Modifying
    @Query("UPDATE $USER_PASSWORD_RESET_TABLE SET status = :status WHERE user_id = :userId")
    fun updateStatus(userId: UUID, status: UserPasswordResetStatus): Int
}
