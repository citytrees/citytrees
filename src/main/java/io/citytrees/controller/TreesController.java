package io.citytrees.controller;

import io.citytrees.service.TreesService;
import io.citytrees.v1.controller.TreesControllerApiDelegate;
import io.citytrees.v1.model.TreesClusterGetResponse;
import io.citytrees.v1.model.TreesGetResponseTree;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TreesController implements TreesControllerApiDelegate {
    private final TreesService treesService;

    @Override
    public ResponseEntity<List<TreesGetResponseTree>> loadTreesByRegion(BigDecimal x1, BigDecimal y1, BigDecimal x2, BigDecimal y2) {
        List<TreesGetResponseTree> responseTreeList = treesService.loadByRegion(x1.doubleValue(), y1.doubleValue(), x2.doubleValue(), y2.doubleValue())
            .stream()
            .map(tree -> new TreesGetResponseTree()
                .id(tree.getId())
                .latitude(tree.getGeoPoint().getX())
                .longitude(tree.getGeoPoint().getY())
                .status(tree.getStatus())
            )
            .collect(Collectors.toList());

        return ResponseEntity.ok(responseTreeList);
    }

    @Override
    @Deprecated
    public ResponseEntity<List<TreesClusterGetResponse>> loadTreesClustersByRegion(BigDecimal x1, BigDecimal y1, BigDecimal x2, BigDecimal y2) {
        List<TreesClusterGetResponse> response = treesService.loadClustersByRegion(x1.doubleValue(), y1.doubleValue(), x2.doubleValue(), y2.doubleValue())
            .stream()
            .map(cluster -> new TreesClusterGetResponse()
                .latitude(cluster.getGeoPoint().getX())
                .longitude(cluster.getGeoPoint().getY())
                .count(BigDecimal.valueOf(cluster.getCount())))
            .toList();

        return ResponseEntity.ok(response);
    }
}
