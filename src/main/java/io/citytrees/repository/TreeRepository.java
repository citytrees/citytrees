package io.citytrees.repository;

import io.citytrees.model.Tree;
import io.citytrees.repository.extension.TreeRepositoryExtension;
import io.citytrees.v1.model.TreeCondition;
import io.citytrees.v1.model.TreeState;
import io.citytrees.v1.model.TreeStatus;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface TreeRepository extends CrudRepository<Tree, UUID>, TreeRepositoryExtension {
    @Query("""
        INSERT INTO ct_tree(id, user_id, status, geo_point)
        VALUES (:id, :userId, :status, ST_SetSRID(ST_MakePoint(:x, :y), :srid))
        RETURNING id
        """)
    UUID create(UUID id, UUID userId, TreeStatus status, double x, double y, Integer srid);

    @Modifying
    @Query("""
        UPDATE ct_tree
        SET status = :status, state = :state, condition = :condition, comment = :comment
        WHERE id = :id
        """)
    int update(UUID id, UUID userId, TreeStatus status, TreeState state, TreeCondition condition, String comment);

    @Modifying
    @Query("DELETE FROM ct_tree where id = :id")
    void deleteById(@NotNull UUID id);
}
