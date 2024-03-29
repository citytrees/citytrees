package io.citytrees.repository.extension.rowmapper;

import io.citytrees.model.TreesCluster;
import io.citytrees.service.GeometryService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import static com.amazonaws.util.json.Jackson.fromJsonString;

@Component
@RequiredArgsConstructor
public class TreesClusterRowMapper implements RowMapper<TreesCluster> {
    private final GeometryService geometryService;

    @SuppressWarnings("unchecked")
    @Override
    @SneakyThrows
    public TreesCluster mapRow(ResultSet rs, int rowNum) {
        Map<String, Object> pointInfo = (Map<String, Object>) fromJsonString(rs.getString(1), Map.class);
        List<Double> coordinatesInfo = ((List<Object>) pointInfo.get("coordinates"))
            .stream().map(number -> Double.parseDouble(number.toString())).toList();

        return TreesCluster.builder()
            .geoPoint(geometryService.createPoint(Double.valueOf(coordinatesInfo.get(0)), Double.valueOf(coordinatesInfo.get(1))))
            .count((long) rs.getInt(2))
            .build();
    }
}
