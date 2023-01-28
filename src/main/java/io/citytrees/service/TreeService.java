package io.citytrees.service;

import io.citytrees.configuration.properties.GeoProperties;
import io.citytrees.model.CtFile;
import io.citytrees.model.Tree;
import io.citytrees.repository.TreeRepository;
import io.citytrees.service.exception.UserInputError;
import io.citytrees.util.GeometryUtil;
import io.citytrees.v1.model.TreeBarkCondition;
import io.citytrees.v1.model.TreeBranchCondition;
import io.citytrees.v1.model.TreeCondition;
import io.citytrees.v1.model.TreeCreateRequest;
import io.citytrees.v1.model.TreeGetResponse;
import io.citytrees.v1.model.TreePlantingType;
import io.citytrees.v1.model.TreeState;
import io.citytrees.v1.model.TreeStatus;
import io.citytrees.v1.model.TreeUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TreeService {

    private final GeoProperties geoProperties;
    private final GeometryUtil geometryUtil;
    private final SecurityService securityService;
    private final TreeRepository treeRepository;
    private final FileService fileService;

    public Long create(TreeCreateRequest request) {
        Point point = geometryUtil.createPoint(request.getLatitude(), request.getLongitude());
        point.setSRID(geoProperties.getSrid());
        return create(securityService.getCurrentUserId(), point);
    }

    public Long create(UUID userId, Point point) {
        return treeRepository.create(userId, TreeStatus.NEW, point.getX(), point.getY(), geoProperties.getSrid(), LocalDateTime.now());
    }

    public void delete(Long id) {
        treeRepository.updateStatus(id, TreeStatus.DELETED);
    }

    public Optional<Tree> getById(Long treeId) {
        return treeRepository.findFirstById(treeId);
    }

    public void update(Long id, TreeUpdateRequest treeUpdateRequest) {
        List<TreeBarkCondition> barkCondition = treeUpdateRequest.getBarkCondition();
        List<TreeBranchCondition> branchesCondition = treeUpdateRequest.getBranchesCondition();
        List<UUID> fileIds = treeUpdateRequest.getFileIds();

        update(id,
            securityService.getCurrentUserId(),
            treeUpdateRequest.getWoodTypeId(),
            treeUpdateRequest.getStatus(),
            treeUpdateRequest.getState(),
            treeUpdateRequest.getAge(),
            treeUpdateRequest.getCondition(),
            barkCondition != null ? new HashSet<>(barkCondition) : Collections.emptySet(),
            branchesCondition != null ? new HashSet<>(branchesCondition) : Collections.emptySet(),
            treeUpdateRequest.getPlantingType(),
            treeUpdateRequest.getComment(),
            fileIds != null ? fileIds : Collections.emptyList(),
            treeUpdateRequest.getDiameterOfCrown(),
            treeUpdateRequest.getHeightOfTheFirstBranch(),
            treeUpdateRequest.getNumberOfTreeTrunks(),
            treeUpdateRequest.getTreeHeight(),
            treeUpdateRequest.getTrunkGirth()
        );
    }

    @SuppressWarnings("checkstyle:ParameterNumber")
    public void update(Long id,
                       UUID userId,
                       UUID woodTypeId,
                       TreeStatus status,
                       TreeState state,
                       Integer age,
                       TreeCondition condition,
                       Set<TreeBarkCondition> barkCondition,
                       Set<TreeBranchCondition> branchesCondition,
                       TreePlantingType plantingType,
                       String comment,
                       List<UUID> fileIds,
                       Double diameterOfCrown,
                       Double heightOfTheFirstBranch,
                       Integer numberOfTreeTrunks,
                       Double treeHeight,
                       Double trunkGirth
    ) {
        treeRepository.update(id,
            userId,
            woodTypeId,
            status,
            state,
            age,
            condition,
            barkCondition,
            branchesCondition,
            plantingType,
            comment,
            fileIds,
            diameterOfCrown,
            heightOfTheFirstBranch,
            numberOfTreeTrunks,
            treeHeight,
            trunkGirth);
    }

    @Transactional
    public UUID attachFile(Long treeId, MultipartFile file) {
        UUID fileId = fileService.upload(file);
        treeRepository.attachFile(treeId, fileId);
        return fileId;
    }

    public List<CtFile> listAttachedFiles(Long treeId) {
        var optionalTree = treeRepository.findFirstById(treeId);
        if (optionalTree.isEmpty()) {
            throw new UserInputError("Tree with id '" + treeId + "' not found");
        }
        var tree = optionalTree.get();
        return fileService.listAllByIds(tree.getFileIds());
    }

    public void updateStatus(Long id, TreeStatus status) {
        treeRepository.updateStatus(id, status);
    }

    public List<Tree> listAll(Integer limit, Integer offset) {
        return treeRepository.findAll(limit, offset);
    }

    public Long countAll() {
        return treeRepository.count();
    }

    // TODO #32 add mapper
    public TreeGetResponse responseFromTree(Tree tree) {
        Integer age = tree.getAge();
        return new TreeGetResponse()
            .id(tree.getId())
            .userId(tree.getUserId())
            .status(tree.getStatus())
            .latitude(tree.getGeoPoint().getX())
            .longitude(tree.getGeoPoint().getY())
            .woodTypeId(tree.getWoodTypeId())
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
}
