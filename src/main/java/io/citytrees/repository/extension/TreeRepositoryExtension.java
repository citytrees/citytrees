package io.citytrees.repository.extension;

import io.citytrees.model.Tree;
import io.citytrees.model.TreesCluster;
import io.citytrees.v1.model.TreeBarkCondition;
import io.citytrees.v1.model.TreeBranchCondition;
import io.citytrees.v1.model.TreeCondition;
import io.citytrees.v1.model.TreePlantingType;
import io.citytrees.v1.model.TreeState;
import io.citytrees.v1.model.TreeStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface TreeRepositoryExtension {

    Optional<Tree> findTreeById(UUID id);

    List<Tree> findByRegion(Double x1, Double y1, Double x2, Double y2, Integer srid);

    List<Tree> findAllTrees(BigDecimal limit, BigDecimal offset);

    void attachFile(UUID treeId, UUID fileId);

    @SuppressWarnings("checkstyle:ParameterNumber")
    void update(UUID id,
                UUID userId,
                UUID woodTypeId,
                TreeStatus status,
                TreeState state,
                Integer age,
                TreeCondition condition,
                Set<TreeBarkCondition> barkCondition,
                Set<TreeBranchCondition> branchesCondition,
                TreePlantingType plantingType,
                String comment,
                List<UUID> fileIds);

    List<TreesCluster> findClustersByRegion(Double x1, Double y1, Double x2, Double y2, Double clusterDistance, Integer srid);
}
