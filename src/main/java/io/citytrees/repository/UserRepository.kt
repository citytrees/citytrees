package io.citytrees.repository

import io.citytrees.constants.TableNames.USER_TABLE
import io.citytrees.model.User
import io.citytrees.v1.model.UserStatus
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import java.time.LocalDateTime
import java.util.*

interface UserRepository : CrudRepository<User, UUID> {

    @Query(
        """
        SELECT * 
        FROM $USER_TABLE 
        WHERE creation_date_time < :cursorPosition
        ORDER BY creation_date_time DESC 
        LIMIT :limit 
        """
    )
    fun findAll(limit: Int, cursorPosition: LocalDateTime): List<User>

    @Query(
        """
        SELECT *
        FROM $USER_TABLE
        WHERE id = :id
    """
    )
    fun findFirstById(id: UUID): Optional<User>

    @Query(
        """
        SELECT *
        FROM $USER_TABLE
        WHERE email = :email
        LIMIT 1
    """
    )
    fun findFirstByEmail(email: String): Optional<User>

    @Query(
        """
        SELECT *
        FROM $USER_TABLE
        WHERE status = :status
        LIMIT 100
    """
    )
    fun findByStatus(status: UserStatus, limit: Int): List<User>

    @Query(
        """
        INSERT INTO $USER_TABLE(id, email, pwd, status, roles, creation_date_time, first_name, last_name, auth_provider_meta)
        VALUES (:id, :email, :pwd, :status, :roles::jsonb, :creationDateTime, :firstName, :lastName, :authProviderMetaString::jsonb)
        RETURNING id
        """
    )
    fun create(
        id: UUID,
        email: String,
        pwd: String,
        status: UserStatus,
        roles: String,
        creationDateTime: LocalDateTime,
        firstName: String?,
        lastName: String?,
        authProviderMetaString: String
    ): UUID

    @Modifying
    @Query(
        """
        UPDATE $USER_TABLE
        SET first_name = :firstName, last_name = :lastName
        WHERE id = :id
        """
    )
    fun update(id: UUID, firstName: String?, lastName: String?): Int

    @Modifying
    @Query("UPDATE $USER_TABLE SET pwd = :pwd WHERE id = :id")
    fun updatePassword(id: UUID, pwd: String): Int

    @Modifying
    @Query("UPDATE $USER_TABLE SET status = :status WHERE id = :id")
    fun updateStatus(id: UUID, status: UserStatus): Int

    @Query(
        """
        SELECT *
        FROM $USER_TABLE
        WHERE auth_provider_meta @> '[{"id": ":providerId", "params": {"id": ":externalUserId"}}]'::jsonb
    """
    )
    fun findByAuthProviderIdAndExternalUserId(providerId: String, externalUserId: String): User?

    @Modifying
    @Query("DELETE FROM $USER_TABLE where id = :id")
    override fun deleteById(id: UUID)
}
