package io.citytrees.repository.extension

import io.citytrees.model.TreesCluster
import io.citytrees.v1.model.*
import java.util.*

interface TreeRepositoryExtension {
    fun attachFile(treeId: Long, fileId: UUID)

    fun update(
        id: Long,
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
        fileIds: List<UUID>,
        diameterOfCrown: Double?,
        heightOfTheFirstBranch: Double?,
        numberOfTreeTrunks: Int?,
        treeHeight: Double?,
        trunkGirth: Double?,
    )

    fun findClustersByRegion(x1: Double, y1: Double, x2: Double, y2: Double, clusterDistance: Double, srid: Int): List<TreesCluster?>?
}