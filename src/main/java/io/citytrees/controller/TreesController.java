package io.citytrees.controller;

import io.citytrees.mapper.TreeMapper;
import io.citytrees.service.TreeService;
import io.citytrees.v1.controller.TreesControllerApiDelegate;
import io.citytrees.v1.model.TreeShortGetResponse;
import io.citytrees.v1.model.TreesClusterGetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TreesController implements TreesControllerApiDelegate {
    private final TreeService treeService;
    private final TreeMapper treeMapper;

    @Override
    public ResponseEntity<List<TreeShortGetResponse>> loadTreesByRegion(Double x1, Double y1, Double x2, Double y2) {
        List<TreeShortGetResponse> responseTreeList = treeService.listByRegion(x1, y1, x2, y2)
            .stream()
            .map(treeMapper::shortResponseFromTree)
            .collect(Collectors.toList());

        return ResponseEntity.ok(responseTreeList);
    }

    @Override
    @Deprecated
    public ResponseEntity<List<TreesClusterGetResponse>> loadTreesClustersByRegion(Double x1, Double y1, Double x2, Double y2) {
        List<TreesClusterGetResponse> response = treeService.listClustersByRegion(x1, y1, x2, y2)
            .stream()
            .map(cluster -> new TreesClusterGetResponse()
                .latitude(cluster.getGeoPoint().getX())
                .longitude(cluster.getGeoPoint().getY())
                .count(cluster.getCount()))
            .toList();

        return ResponseEntity.ok(response);
    }
}
