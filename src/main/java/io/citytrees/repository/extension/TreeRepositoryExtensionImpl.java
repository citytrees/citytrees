package io.citytrees.repository.extension;

import io.citytrees.model.Tree;
import io.citytrees.repository.extension.rowmapper.TreeRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TreeRepositoryExtensionImpl implements TreeRepositoryExtension {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final TreeRowMapper treeMapper;

    @Override
    public Optional<Tree> findTreeById(UUID id) {
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
        // TODO #18
        var fileIdElement = "[" + "\"" + fileId + "\"" + "]";

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
}
