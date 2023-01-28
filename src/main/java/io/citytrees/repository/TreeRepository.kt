package io.citytrees.repository

import io.citytrees.constants.TableNames.TREE_TABLE
import io.citytrees.model.Tree
import io.citytrees.repository.extension.TreeRepositoryExtension
import io.citytrees.v1.model.TreeStatus
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import java.time.LocalDateTime
import java.util.*

interface TreeRepository : CrudRepository<Tree?, Long>, TreeRepositoryExtension {

    @Query("SELECT * FROM $TREE_TABLE WHERE id = :id")
    fun findFirstById(id: Long): Optional<Tree>

    @Query(
        """
        SELECT * FROM $TREE_TABLE
        WHERE ST_Within(geo_point, ST_MakeEnvelope(:x2, :y2, :x1, :y1, :srid))
        """
    )
    fun findAllByRegion(x1: Double, y1: Double, x2: Double, y2: Double, srid: Int): List<Tree>

    // todo #18 implement cursor based pagination, order by created_at
    @Query("SELECT * FROM $TREE_TABLE ORDER BY id LIMIT :limit OFFSET :offset")
    fun findAll(limit: Int, offset: Int): List<Tree>

    @Query(
        """
        INSERT INTO $TREE_TABLE(user_id, status, geo_point, creation_date_time)
        VALUES (:userId, :status, ST_SetSRID(ST_MakePoint(:x, :y), :srid, :creationDateTime))
        RETURNING id
        """
    )
    fun create(userId: UUID, status: TreeStatus, x: Double, y: Double, srid: Int, creationDateTime: LocalDateTime): Long

    @Modifying
    @Query("UPDATE $TREE_TABLE SET status = :status WHERE id = :id")
    fun updateStatus(id: Long, status: TreeStatus): Int

    @Modifying
    @Query("DELETE FROM $TREE_TABLE where id = :id")
    override fun deleteById(id: Long)
}
