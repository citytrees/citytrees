package io.citytrees.repository

import io.citytrees.constants.TableNames.FILE_TABLE
import io.citytrees.model.CtFile
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import java.time.LocalDateTime
import java.util.*

interface FileRepository : CrudRepository<CtFile, UUID> {
    @Query(
        """
        INSERT INTO $FILE_TABLE(id, name, mime_type, size, hash, creation_date_time, user_id)
        VALUES (:id, :name, :mimeType, :size, :hash, :creationDateTime, :userId)
        RETURNING id
        """
    )
    fun save(id: UUID, name: String, mimeType: String, size: Long, hash: String, creationDateTime: LocalDateTime, userId: UUID): UUID

    fun findFirstByHash(hash: String): Optional<CtFile>

    fun countByHash(hash: String): Long

    fun findAllByIdIn(uuids: Set<UUID>): List<CtFile>
}