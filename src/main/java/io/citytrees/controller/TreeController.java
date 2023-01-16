package io.citytrees.controller;

import io.citytrees.service.TreeService;
import io.citytrees.v1.controller.TreeControllerApiDelegate;
import io.citytrees.v1.model.TreeCreateRequest;
import io.citytrees.v1.model.TreeCreateResponse;
import io.citytrees.v1.model.TreeGetResponse;
import io.citytrees.v1.model.TreeUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TreeController implements TreeControllerApiDelegate {

    private final TreeService treeService;

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TreeCreateResponse> createTree(TreeCreateRequest treeCreateRequest) {
        UUID treeId = treeService.create(treeCreateRequest);
        TreeCreateResponse response = new TreeCreateResponse()
            .treeId(treeId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<TreeGetResponse> getTreeById(UUID id) {
        var optionalTree = treeService.getById(id);
        if (optionalTree.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        var tree = optionalTree.get();
        var response = new TreeGetResponse()
            .id(tree.getId())
            .userId(tree.getUserId())
            .status(tree.getStatus())
            .latitude(tree.getGeoPoint().getX())
            .longitude(tree.getGeoPoint().getY())
            .fileIds(tree.getFileIds().stream().map(UUID::toString).toList())
            .state(tree.getState())
            .condition(tree.getCondition())
            .barkCondition(tree.getBarkCondition().stream().toList())
            .branchesCondition(tree.getBranchesCondition().stream().toList())
            .comment(tree.getComment());

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
}
