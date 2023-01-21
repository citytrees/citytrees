package io.citytrees.repository.extension

import com.fasterxml.jackson.databind.ObjectMapper
import io.citytrees.constants.TableNames
import io.citytrees.model.TreesCluster
import io.citytrees.repository.extension.rowmapper.TreesClusterRowMapper
import io.citytrees.v1.model.*
import org.intellij.lang.annotations.Language
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.util.*

@Repository
open class TreeRepositoryExtensionImpl(
    val jdbcTemplate: NamedParameterJdbcTemplate,
    val treesClusterRowMapper: TreesClusterRowMapper,
    val objectMapper: ObjectMapper,
) : TreeRepositoryExtension {
    override fun attachFile(treeId: UUID, fileId: UUID) {
        val fileIdElement = objectMapper.writeValueAsString(listOf(fileId))

        @Language("SQL")
        val sql = """
            UPDATE ${TableNames.TREE_TABLE}
            SET file_ids = file_ids || :fileId::jsonb
            WHERE id = :treeId;
            """

        val params: Map<String, Any> = mapOf(
            "tableName" to TableNames.TREE_TABLE,
            "treeId" to treeId,
            "fileId" to fileIdElement
        )

        jdbcTemplate.update(sql, params)
    }

    override fun update(
        id: UUID,
        userId: UUID,
        woodTypeId: UUID?,
        status: TreeStatus?,
        state: TreeState?,
        age: Int?,
        condition: TreeCondition?,
        barkCondition: Set<TreeBarkCondition>,
        branchesCondition: Set<TreeBranchCondition>,
        plantingType: TreePlantingType?,
        comment: String?,
        fileIds: List<UUID?>
    ) {
        @Language("SQL")
        val sql = """
                UPDATE ${TableNames.TREE_TABLE} 
                SET wood_type_id = :woodTypeId, 
                status = :status, 
                state = :state, 
                age = :age, 
                condition = :condition, 
                bark_condition = :barkCondition::jsonb, 
                branches_condition = :branchesCondition::jsonb, 
                planting_type = :plantingType, 
                comment = :comment, 
                file_ids = :fileIds::jsonb 
                WHERE id = :id"""

        val params: Map<String, Any?> = mapOf(
            "id" to id,
            "userId" to userId,
            "woodTypeId" to woodTypeId,
            "status" to status?.value,
            "state" to state?.value,
            "age" to age,
            "condition" to condition?.value,
            "barkCondition" to objectMapper.writeValueAsString(barkCondition),
            "branchesCondition" to objectMapper.writeValueAsString(branchesCondition),
            "plantingType" to plantingType?.value,
            "comment" to comment,
            "fileIds" to objectMapper.writeValueAsString(fileIds),
        )

        jdbcTemplate.update(sql, params)
    }

    override fun findClustersByRegion(x1: Double, y1: Double, x2: Double, y2: Double, clusterDistance: Double, srid: Int): List<TreesCluster?>? {
        @Language("SQL")
        val sql = """
             SELECT cast(json(ST_Centroid(points)) as text) AS centre, ST_NumGeometries(points) AS count
             FROM (SELECT unnest(ST_ClusterWithin(geo_point, :clusterDistance)) AS points
                   FROM ${TableNames.TREE_TABLE} AS tree
                   WHERE ST_Within(tree.geo_point, ST_MakeEnvelope(:x2, :y2, :x1, :y1, :srid))) as cluster
            """

        val params: Map<String, Any> = mapOf(
            "x1" to x1,
            "y1" to y1,
            "x2" to x2,
            "y2" to y2,
            "clusterDistance" to clusterDistance,
            "srid" to srid
        )

        return jdbcTemplate.query(sql, params, treesClusterRowMapper)
    }
}