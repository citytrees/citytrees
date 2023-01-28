package io.citytrees.repository.extension.rowmapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.citytrees.model.Tree;
import io.citytrees.util.GeometryUtil;
import io.citytrees.v1.model.TreeCondition;
import io.citytrees.v1.model.TreePlantingType;
import io.citytrees.v1.model.TreeState;
import io.citytrees.v1.model.TreeStatus;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.postgis.PGgeometry;
import org.postgis.Point;
import org.postgresql.util.PGobject;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TreeRowMapper implements RowMapper<Tree> {

    private final GeometryUtil geometryUtil;
    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public Tree mapRow(ResultSet rs, int rowNum) {
        Tree.TreeBuilder builder = Tree.builder();
        PGobject geoPoint = rs.getObject("geo_point", PGobject.class);
        Point point = (Point) PGgeometry.geomFromString(Objects.requireNonNull(geoPoint.getValue()));
        String state = rs.getObject("state", String.class);
        String condition = rs.getObject("condition", String.class);
        String plantingType = rs.getObject("planting_type", String.class);

        BigDecimal diameterOfCrown = rs.getBigDecimal("diameter_of_crown");
        BigDecimal heightOfTheFirstBranch = rs.getBigDecimal("height_of_the_first_branch");
        BigDecimal numberOfTreeTrunks = rs.getBigDecimal("number_of_tree_trunks");
        BigDecimal treeHeight = rs.getBigDecimal("tree_height");
        BigDecimal trunkGirth = rs.getBigDecimal("trunk_girth");

        return builder
            .id(rs.getLong("id"))
            .userId(rs.getObject("user_id", UUID.class))
            .status(TreeStatus.valueOf(rs.getObject("status", String.class)))
            .geoPoint(geometryUtil.createPoint(point.getX(), point.getY()))
            .woodTypeId(rs.getObject("wood_type_id", UUID.class))
            .fileIds(objectMapper.readValue(rs.getString("file_ids"), new TypeReference<>() {}))
            .state(state != null ? TreeState.valueOf(state) : null)
            .age(rs.getObject("age", Integer.class))
            .condition(condition != null ? TreeCondition.valueOf(condition) : null)
            .barkCondition(objectMapper.readValue(rs.getString("bark_condition"), new TypeReference<>() {}))
            .branchesCondition(objectMapper.readValue(rs.getString("branches_condition"), new TypeReference<>() {}))
            .plantingType(plantingType != null ? TreePlantingType.valueOf(plantingType) : null)
            .comment(rs.getString("comment"))
            .diameterOfCrown(diameterOfCrown != null ? diameterOfCrown.doubleValue() : null)
            .heightOfTheFirstBranch(heightOfTheFirstBranch != null ? heightOfTheFirstBranch.doubleValue() : null)
            .numberOfTreeTrunks(numberOfTreeTrunks != null ? numberOfTreeTrunks.intValue() : null)
            .treeHeight(treeHeight != null ? treeHeight.doubleValue() : null)
            .trunkGirth(trunkGirth != null ? trunkGirth.doubleValue() : null)
            .creationDateTime(rs.getTimestamp("creation_date_time").toLocalDateTime())
            .build();
    }
}
