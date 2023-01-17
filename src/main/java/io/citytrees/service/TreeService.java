package io.citytrees.service;

import io.citytrees.configuration.properties.GeoProperties;
import io.citytrees.model.CtFile;
import io.citytrees.model.Tree;
import io.citytrees.repository.TreeRepository;
import io.citytrees.v1.model.TreeBarkCondition;
import io.citytrees.v1.model.TreeBranchCondition;
import io.citytrees.v1.model.TreeCondition;
import io.citytrees.v1.model.TreeCreateRequest;
import io.citytrees.v1.model.TreePlantingType;
import io.citytrees.v1.model.TreeState;
import io.citytrees.v1.model.TreeStatus;
import io.citytrees.v1.model.TreeUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
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
    private final GeometryService geometryService;
    private final SecurityService securityService;
    private final TreeRepository treeRepository;
    private final FileService fileService;

    public UUID create(TreeCreateRequest request) {
        Point point = geometryService.createPoint(request.getLatitude(), request.getLongitude());
        point.setSRID(geoProperties.getSrid());
        return create(UUID.randomUUID(), securityService.getCurrentUserId(), TreeStatus.NEW, point);
    }

    public void delete(UUID id) {
        treeRepository.deleteById(id);
    }

    public Optional<Tree> getById(UUID treeId) {
        return treeRepository.findTreeById(treeId);
    }

    public void update(UUID id, TreeUpdateRequest treeUpdateRequest) {
        List<TreeBarkCondition> barkCondition = treeUpdateRequest.getBarkCondition();
        List<TreeBranchCondition> branchesCondition = treeUpdateRequest.getBranchesCondition();
        List<UUID> fileIds = treeUpdateRequest.getFileIds();
        BigDecimal age = treeUpdateRequest.getAge();

        update(id,
            securityService.getCurrentUserId(),
            treeUpdateRequest.getWoodTypeId(),
            treeUpdateRequest.getStatus(),
            treeUpdateRequest.getState(),
            age != null ? age.intValue() : null,
            treeUpdateRequest.getCondition(),
            barkCondition != null ? new HashSet<>(barkCondition) : Collections.emptySet(),
            branchesCondition != null ? new HashSet<>(branchesCondition) : Collections.emptySet(),
            treeUpdateRequest.getPlantingType(),
            treeUpdateRequest.getComment(),
            fileIds != null ? fileIds : Collections.emptyList());
    }

    @SuppressWarnings("checkstyle:ParameterNumber")
    public void update(UUID id,
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
                       List<UUID> fileIds) {
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
            fileIds);
    }

    @Transactional
    public UUID attachFile(UUID treeId, MultipartFile file) {
        UUID fileId = fileService.upload(file);
        treeRepository.attachFile(treeId, fileId);
        return fileId;
    }

    public List<CtFile> getAttachedFiles(UUID treeId) {
        // todo #18 in list
        return treeRepository.findTreeById(treeId).map(tree -> tree.getFileIds().stream()
            .map(fileService::getFile)
            .flatMap(Optional::stream)
            .toList()).orElse(Collections.emptyList());
    }

    private UUID create(UUID id, UUID userId, TreeStatus status, Point point) {
        return treeRepository.create(id, userId, status, point.getX(), point.getY(), geoProperties.getSrid());
    }

    public void updateStatus(UUID id, TreeStatus status) {
        treeRepository.updateStatus(id, status);
    }
}
