package io.citytrees.mapper;

import io.citytrees.model.Tree;
import io.citytrees.model.WoodType;
import io.citytrees.service.WoodTypeService;
import io.citytrees.util.FileDownloadUtil;
import io.citytrees.v1.model.TreeGetResponse;
import io.citytrees.v1.model.TreeShortGetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TreeMapper {

    private final WoodTypeService woodTypeService;
    private final FileDownloadUtil fileDownloadUtil;

    // TODO #32 fix mapper
    public TreeGetResponse responseFromTree(Tree tree) {
        Integer age = tree.getAge();
        UUID woodTypeId = tree.getWoodTypeId();
        WoodType woodType = woodTypeId != null ? woodTypeService.getById(woodTypeId) : null;
        return new TreeGetResponse()
            .id(tree.getId())
            .userId(tree.getUserId())
            .status(tree.getStatus())
            .latitude(tree.getGeoPoint().getX())
            .longitude(tree.getGeoPoint().getY())
            .woodTypeId(woodTypeId)
            .woodTypeName(woodType != null ? woodType.getName() : null)
            .fileIds(tree.getFileIds().stream().map(UUID::toString).toList())
            .state(tree.getState())
            .age(age)
            .condition(tree.getCondition())
            .barkCondition(tree.getBarkCondition().stream().toList())
            .branchesCondition(tree.getBranchesCondition().stream().toList())
            .plantingType(tree.getPlantingType())
            .comment(tree.getComment())
            .diameterOfCrown(tree.getDiameterOfCrown())
            .heightOfTheFirstBranch(tree.getHeightOfTheFirstBranch())
            .numberOfTreeTrunks(tree.getNumberOfTreeTrunks())
            .treeHeight(tree.getTreeHeight())
            .trunkGirth(tree.getTrunkGirth());
    }

    // TODO #32 fix mapper
    public TreeShortGetResponse shortResponseFromTree(Tree tree) {
        UUID woodTypeId = tree.getWoodTypeId();
        WoodType woodType = woodTypeId != null ? woodTypeService.getById(woodTypeId) : null;
        Set<UUID> fileIds = tree.getFileIds();

        return new TreeShortGetResponse()
            .id(tree.getId())
            .latitude(tree.getGeoPoint().getX())
            .longitude(tree.getGeoPoint().getY())
            .status(tree.getStatus())
            .woodTypeId(woodTypeId)
            .woodTypeName(woodType != null ? woodType.getName() : null)
            .treeHeight(tree.getTreeHeight())
            .trunkGirth(tree.getTrunkGirth())
            .fileUrls(fileIds.stream().map(fileDownloadUtil::generateDownloadUrl).toList())
            .userId(tree.getUserId());
    }
}
