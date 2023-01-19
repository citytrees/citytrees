package io.citytrees.repository;

import io.citytrees.model.Tree;
import io.citytrees.repository.extension.TreeRepositoryExtension;
import io.citytrees.v1.model.TreeStatus;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TreeRepository extends CrudRepository<Tree, UUID>, TreeRepositoryExtension {

    @Query("SELECT * FROM ct_tree WHERE id = :id")
    Optional<Tree> findFirstById(UUID id);

    @Query("""
        SELECT * FROM ct_tree
        WHERE ST_Within(ct_tree.geo_point, ST_MakeEnvelope(:x2, :y2, :x1, :y1, :srid))
        """)
    List<Tree> findAllByRegion(Double x1, Double y1, Double x2, Double y2, Integer srid);

    // todo #18
    @Query("SELECT * FROM ct_tree")
    List<Tree> findAll(Pageable pageable);

    @Query("""
        INSERT INTO ct_tree(id, user_id, status, geo_point)
        VALUES (:id, :userId, :status, ST_SetSRID(ST_MakePoint(:x, :y), :srid))
        RETURNING id
        """)
    UUID create(UUID id, UUID userId, TreeStatus status, double x, double y, Integer srid);

    @Modifying
    @Query("DELETE FROM ct_tree where id = :id")
    void deleteById(@NotNull UUID id);

    @Modifying
    @Query("""
        UPDATE ct_tree
        SET status = :status
        WHERE id = :id
        """)
    int updateStatus(UUID id, TreeStatus status);
}
