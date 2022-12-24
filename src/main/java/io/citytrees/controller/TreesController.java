package io.citytrees.controller;

import io.citytrees.model.Tree;
import io.citytrees.service.TreesService;
import io.citytrees.v1.controller.TreesControllerApiDelegate;
import io.citytrees.v1.model.TreesByRegionRequest;
import io.citytrees.v1.model.TreesGetResponseTree;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TreesController implements TreesControllerApiDelegate {
    private final TreesService treesService;

    @Override
    public ResponseEntity<List<TreesGetResponseTree>> loadTreesByRegion(TreesByRegionRequest treesByRegionRequest) {
        List<Tree> trees = treesService.loadByRegion(treesByRegionRequest.getX1(), treesByRegionRequest.getY1(), treesByRegionRequest.getX2(), treesByRegionRequest.getY2());
        List<TreesGetResponseTree> responseTreeList = trees.stream()
            .map(tree -> new TreesGetResponseTree()
                .id(tree.getId())
                .latitude(tree.getGeoPoint().getX())
                .longitude(tree.getGeoPoint().getY())
                .status(tree.getStatus())
            )
            .collect(Collectors.toList());

        return ResponseEntity.ok(responseTreeList);
    }

//    @Override TODO #18
//    public ResponseEntity<List<TreesGetResponseTree>> loadTreesByRegion(String x1, String y1, String x2, String y2) {
//        List<Tree> trees = treesService.loadByRegion(Double.valueOf(x1), Double.valueOf(y2), Double.valueOf(x2), Double.valueOf(y2));
//        List<TreesGetResponseTree> responseTreeList = trees.stream()
//            .map(tree -> new TreesGetResponseTree().latitude(tree.getGeoPoint().getX()).longitude(tree.getGeoPoint().getY()))
//            .collect(Collectors.toList());
//
//        return ResponseEntity.ok(responseTreeList);
//    }
}
