package io.citytrees.controller;

import io.citytrees.model.Tree;
import io.citytrees.service.TreesService;
import io.citytrees.v1.controller.TreesControllerApiDelegate;
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
        List<Tree> trees = treesService.loadByRegion(x1.doubleValue(), y1.doubleValue(), x2.doubleValue(), y2.doubleValue());
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
}
