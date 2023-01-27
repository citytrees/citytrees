package io.citytrees.controller;

import io.citytrees.service.FileDownloadService;
import io.citytrees.service.TreesService;
import io.citytrees.v1.controller.TreesControllerApiDelegate;
import io.citytrees.v1.model.TreesClusterGetResponse;
import io.citytrees.v1.model.TreesGetResponseTree;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TreesController implements TreesControllerApiDelegate {

    private final FileDownloadService service;
    private final TreesService treesService;

    @Override
    public ResponseEntity<List<TreesGetResponseTree>> loadTreesByRegion(Double x1, Double y1, Double x2, Double y2) {
        // todo #32 cache for wood type
        List<TreesGetResponseTree> responseTreeList = treesService.listByRegion(x1, y1, x2, y2)
            .stream()
            .map(tree -> {
                    Set<UUID> fileIds = tree.getFileIds();
                    UUID fileId = fileIds.isEmpty() ? null : fileIds.iterator().next();
                    return new TreesGetResponseTree()
                        .id(tree.getId())
                        .latitude(tree.getGeoPoint().getX())
                        .longitude(tree.getGeoPoint().getY())
                        .status(tree.getStatus())
                        .woodTypeId(tree.getWoodTypeId())
                        .woodTypeName("tree.getWoodTypeId()")
                        .treeHeight(tree.getTreeHeight())
                        .trunkGirth(tree.getTrunkGirth())
                        .fileUrl(service.generateDownloadUrl(fileId));
                }
            )
            .collect(Collectors.toList());

        return ResponseEntity.ok(responseTreeList);
    }

    @Override
    @Deprecated
    public ResponseEntity<List<TreesClusterGetResponse>> loadTreesClustersByRegion(Double x1, Double y1, Double x2, Double y2) {
        List<TreesClusterGetResponse> response = treesService.listClustersByRegion(x1, y1, x2, y2)
            .stream()
            .map(cluster -> new TreesClusterGetResponse()
                .latitude(cluster.getGeoPoint().getX())
                .longitude(cluster.getGeoPoint().getY())
                .count(cluster.getCount()))
            .toList();

        return ResponseEntity.ok(response);
    }
}
