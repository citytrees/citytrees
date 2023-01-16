package io.citytrees.repository.extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.citytrees.model.Tree;
import io.citytrees.model.TreesCluster;
import io.citytrees.repository.extension.rowmapper.TreeRowMapper;
import io.citytrees.repository.extension.rowmapper.TreesClusterRowMapper;
import io.citytrees.v1.model.TreeBarkCondition;
import io.citytrees.v1.model.TreeBranchCondition;
import io.citytrees.v1.model.TreeCondition;
import io.citytrees.v1.model.TreeState;
import io.citytrees.v1.model.TreeStatus;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.intellij.lang.annotations.Language;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TreeRepositoryExtensionImpl implements TreeRepositoryExtension {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final TreeRowMapper treeMapper;
    private final TreesClusterRowMapper treesClusterRowMapper;
    private final ObjectMapper objectMapper;

    @Override
    public Optional<Tree> findTreeById(UUID id) {
        @Language("SQL")
        var sql = """
            SELECT *
            FROM ct_tree
            WHERE id = :id
            """;

        var params = Map.of("id", id);

        return jdbcTemplate.query(sql, params, treeMapper).stream().findFirst();
    }

    @Override
    public List<Tree> findByRegion(Double x1, Double y1, Double x2, Double y2, Integer srid) {
        @Language("SQL")
        var sql = """
            SELECT * FROM ct_tree
            WHERE ST_Within(ct_tree.geo_point, ST_MakeEnvelope(:x2, :y2, :x1, :y1, :srid))
            """;

        var params = Map.of(
            "x1", x1,
            "y1", y1,
            "x2", x2,
            "y2", y2,
            "srid", srid
        );

        return jdbcTemplate.query(sql, params, treeMapper);
    }

    @Override
    @SneakyThrows
    public void attachFile(UUID treeId, UUID fileId) {
        var fileIdElement = objectMapper.writeValueAsString(List.of(fileId));

        @Language("SQL")
        var sql = """
            UPDATE ct_tree
            SET file_ids = file_ids || :fileId::jsonb
            WHERE id = :treeId;
            """;

        var params = Map.of(
            "treeId", treeId,
            "fileId", fileIdElement
        );

        jdbcTemplate.update(sql, params);
    }

    @SuppressWarnings("checkstyle:ParameterNumber")
    @Override
    @SneakyThrows
    public void update(UUID id,
                       UUID userId,
                       TreeStatus status,
                       TreeState state,
                       TreeCondition condition,
                       Set<TreeBarkCondition> barkCondition,
                       Set<TreeBranchCondition> branchesCondition,
                       String comment,
                       List<UUID> fileIds
    ) {
        @Language("SQL")
        var sql = """
            UPDATE ct_tree
            SET
            status = :status,
            state = :state,
            condition = :condition,
            bark_condition = :barkCondition::jsonb,
            branches_condition = :branchesCondition::jsonb,
            comment = :comment,
            file_ids = :fileIds::jsonb
            WHERE id = :id
            """;

        // todo #18 fix enums
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("userId", userId);
        params.put("status", status != null ? status.getValue() : null);
        params.put("state", state != null ? state.getValue() : null);
        params.put("condition", condition != null ? condition.getValue() : null);
        params.put("barkCondition", barkCondition != null ? objectMapper.writeValueAsString(barkCondition) : null);
        params.put("branchesCondition", branchesCondition != null ? objectMapper.writeValueAsString(branchesCondition) : null);
        params.put("comment", comment);
        params.put("fileIds", objectMapper.writeValueAsString(fileIds));

        jdbcTemplate.update(sql, params);
    }

    @Override
    public List<TreesCluster> findClustersByRegion(Double x1, Double y1, Double x2, Double y2, Double clusterDistance, Integer srid) {
        @Language("SQL")
        var sql = """
             SELECT cast(json(ST_Centroid(points)) as text) AS centre, ST_NumGeometries(points) AS count
             FROM (SELECT unnest(ST_ClusterWithin(geo_point, :clusterDistance)) AS points
                   FROM ct_tree AS tree
                   WHERE ST_Within(
                                 tree.geo_point,
                                 ST_MakeEnvelope(:x2, :y2, :x1, :y1, :srid)
                             )) as cluster;
            """;

        var params = Map.of(
            "x1", x1,
            "y1", y1,
            "x2", x2,
            "y2", y2,
            "clusterDistance", clusterDistance,
            "srid", srid
        );

        return jdbcTemplate.query(sql, params, treesClusterRowMapper);
    }
}
