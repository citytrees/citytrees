package io.citytrees.controller;

import io.citytrees.model.WoodType;
import io.citytrees.util.FileDownloadUtil;
import io.citytrees.service.TreesService;
import io.citytrees.service.WoodTypeService;
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

    private final FileDownloadUtil service;
    private final TreesService treesService;
    private final WoodTypeService woodTypeService;

    @Override
    public ResponseEntity<List<TreesGetResponseTree>> loadTreesByRegion(Double x1, Double y1, Double x2, Double y2) {
        // todo #32 cache for wood type
        List<TreesGetResponseTree> responseTreeList = treesService.listByRegion(x1, y1, x2, y2)
            .stream()
            .map(tree -> {
                    Set<UUID> fileIds = tree.getFileIds();
                    UUID fileId = fileIds.isEmpty() ? null : fileIds.iterator().next();
                    UUID woodTypeId = tree.getWoodTypeId();
                    WoodType woodType = woodTypeId != null ? woodTypeService.getById(woodTypeId) : null;

                    return new TreesGetResponseTree()
                        .id(tree.getId())
                        .latitude(tree.getGeoPoint().getX())
                        .longitude(tree.getGeoPoint().getY())
                        .status(tree.getStatus())
                        .woodTypeId(woodTypeId)
                        .woodTypeName(woodType != null ? woodType.getName() : null)
                        .treeHeight(tree.getTreeHeight())
                        .trunkGirth(tree.getTrunkGirth())
                        .fileUrl(fileId != null ? service.generateDownloadUrl(fileId) : null);
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
