package io.citytrees.repository

import io.citytrees.constants.TableNames.WOOD_TYPE
import io.citytrees.model.WoodType
import io.citytrees.v1.model.WoodTypeStatus
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import java.util.*

interface WoodTypeRepository : CrudRepository<WoodType, UUID> {

    @Query("""SELECT * FROM $WOOD_TYPE ORDER BY name""")
    override fun findAll(): List<WoodType>

    fun findAllByNameContainingIgnoreCaseOrderByName(nameSubstring: String): List<WoodType>

    @Query(
        """
        INSERT INTO $WOOD_TYPE(id, name, status, user_id)
        VALUES (:id, :name, :status, :userId)
        ON CONFLICT(name)
        DO NOTHING
        RETURNING id
        """
    )
    fun create(id: UUID, name: String, status: WoodTypeStatus, userId: UUID): UUID

    @Query("""UPDATE $WOOD_TYPE SET status = :status WHERE id = :id""")
    @Modifying
    fun updateStatus(id: UUID, status: WoodTypeStatus)
}
