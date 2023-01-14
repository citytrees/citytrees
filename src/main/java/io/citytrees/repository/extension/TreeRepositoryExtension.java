package io.citytrees.repository.extension;

import io.citytrees.model.Tree;
import io.citytrees.model.TreesCluster;
import io.citytrees.v1.model.TreeCondition;
import io.citytrees.v1.model.TreeState;
import io.citytrees.v1.model.TreeStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TreeRepositoryExtension {

    Optional<Tree> findTreeById(UUID id);

    List<Tree> findByRegion(Double x1, Double y1, Double x2, Double y2, Integer srid);

    void attachFile(UUID treeId, UUID fileId);

    @SuppressWarnings("checkstyle:ParameterNumber")
    void update(UUID id, UUID userId, TreeStatus status, TreeState state, TreeCondition condition, String comment, List<UUID> fileIds);

    List<TreesCluster> findClustersByRegion(Double x1, Double y1, Double x2, Double y2, Double clusterDistance, Integer srid);
}
