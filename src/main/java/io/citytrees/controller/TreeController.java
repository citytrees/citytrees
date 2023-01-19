package io.citytrees.controller;

import io.citytrees.model.CtFile;
import io.citytrees.model.Tree;
import io.citytrees.service.FileDownloadService;
import io.citytrees.service.TreeService;
import io.citytrees.v1.controller.TreeControllerApiDelegate;
import io.citytrees.v1.model.FileUploadResponse;
import io.citytrees.v1.model.TreeCreateRequest;
import io.citytrees.v1.model.TreeCreateResponse;
import io.citytrees.v1.model.TreeGetAttachedFileResponse;
import io.citytrees.v1.model.TreeGetResponse;
import io.citytrees.v1.model.TreeStatus;
import io.citytrees.v1.model.TreeUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TreeController implements TreeControllerApiDelegate {

    private final TreeService treeService;
    private final FileDownloadService fileDownloadService;

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TreeCreateResponse> createTree(TreeCreateRequest treeCreateRequest) {
        UUID treeId = treeService.create(treeCreateRequest);
        TreeCreateResponse response = new TreeCreateResponse()
            .treeId(treeId);
        return ResponseEntity.ok(response);
    }

    @Override
    @PreAuthorize("permitAll()")
    public ResponseEntity<TreeGetResponse> getTreeById(UUID id) {
        var optionalTree = treeService.getById(id);
        if (optionalTree.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        var tree = optionalTree.get();

        var response = responseFromTree(tree);

        return ResponseEntity.ok(response);
    }

    @Override
    @PreAuthorize("hasAuthority(@Roles.ADMIN) || (isAuthenticated() && hasPermission(#id, @Domains.TREE, @Permissions.EDIT))")
    public ResponseEntity<Void> updateTreeById(UUID id, TreeUpdateRequest treeUpdateRequest) {
        treeService.update(id, treeUpdateRequest);
        return ResponseEntity.ok().build();
    }

    @Override
    @PreAuthorize("hasAuthority(@Roles.ADMIN) || (isAuthenticated() && hasPermission(#id, @Domains.TREE, @Permissions.DELETE))")
    public ResponseEntity<Void> deleteTree(UUID id) {
        treeService.delete(id);
        return ResponseEntity.ok().build();
    }

    @Override
    @PreAuthorize("hasAnyAuthority(@Roles.ADMIN, @Roles.MODERATOR)")
    public ResponseEntity<Void> approveTree(UUID treeId) {
        treeService.updateStatus(treeId, TreeStatus.APPROVED);
        return ResponseEntity.ok().build();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FileUploadResponse> attachFile(UUID treeId, MultipartFile file) {
        var fileId = treeService.attachFile(treeId, file);
        var response = new FileUploadResponse()
            .fileId(fileId)
            .url(fileDownloadService.generateDownloadUrl(fileId));

        return ResponseEntity.ok(response);
    }

    @Override
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<TreeGetAttachedFileResponse>> getAllAttachedFiles(UUID treeId) {
        List<CtFile> files = treeService.listAttachedFiles(treeId);

        List<TreeGetAttachedFileResponse> response = files.stream()
            .map(file -> new TreeGetAttachedFileResponse()
                .id(file.getId())
                .name(file.getName())
                .size(BigDecimal.valueOf(file.getSize()))
                .url(fileDownloadService.generateDownloadUrl(file.getId())))
            .toList();

        return ResponseEntity.ok(response);
    }

    @Override
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<TreeGetResponse>> getAll(BigDecimal page, BigDecimal size) {
        var response = treeService.listAll(page.intValue(), size.intValue()).stream()
            .map(TreeController::responseFromTree)
            .toList();

        return ResponseEntity.ok(response);
    }

    private static TreeGetResponse responseFromTree(Tree tree) {
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
            .age(age != null ? BigDecimal.valueOf(age) : null)
            .condition(tree.getCondition())
            .barkCondition(tree.getBarkCondition().stream().toList())
            .branchesCondition(tree.getBranchesCondition().stream().toList())
            .plantingType(tree.getPlantingType())
            .comment(tree.getComment());
    }
}
