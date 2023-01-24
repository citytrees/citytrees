package io.citytrees.repository

import io.citytrees.constants.TableNames.FILE_CONTENT_TABLE
import io.citytrees.model.CtFileContent
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import java.util.*

interface FileContentRepository : CrudRepository<CtFileContent, UUID> {

    fun findFirstByHash(hash: String): Optional<CtFileContent>

    fun deleteByHash(hash: String)

    @Modifying
    @Query(
        """
        INSERT INTO $FILE_CONTENT_TABLE(id, content, hash)
        VALUES (:id, :content, :hash)
        ON CONFLICT(hash)
        DO NOTHING
        """
    )
    fun upsert(id: UUID, content: ByteArray, hash: String): Int
}