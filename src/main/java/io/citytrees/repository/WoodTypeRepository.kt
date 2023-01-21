package io.citytrees.repository

import io.citytrees.constants.TableNames.WOOD_TYPE
import io.citytrees.model.WoodType
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import java.util.*

interface WoodTypeRepository : CrudRepository<WoodType, UUID> {

    override fun findAll(): List<WoodType>

    @Query(
        """
        INSERT INTO $WOOD_TYPE(id, name, user_id)
        VALUES (:id, :name, :userId)
        ON CONFLICT(name)
        DO NOTHING
        RETURNING id
        """
    )
    fun create(id: UUID, name: String, userId: UUID): UUID
}
