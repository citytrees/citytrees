package io.citytrees.model;

import io.citytrees.constants.TableNames;
import io.citytrees.v1.model.TreeBarkCondition;
import io.citytrees.v1.model.TreeBranchCondition;
import io.citytrees.v1.model.TreeCondition;
import io.citytrees.v1.model.TreeState;
import io.citytrees.v1.model.TreeStatus;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.locationtech.jts.geom.Point;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;
import java.util.UUID;

@Value
@Builder
@Table(TableNames.TREE_TABLE)
public class Tree {

    @Id
    @NotNull
    UUID id;

    @NotNull
    UUID userId;

    @NotNull
    TreeStatus status;

    @NotNull
    @Column("geo_point")
    Point geoPoint;

    @NotNull
    Set<UUID> fileIds;

    TreeState state;

    TreeCondition condition;

    Set<TreeBarkCondition> barkCondition;

    Set<TreeBranchCondition> branchesCondition;

    String comment;
}
