package io.citytrees.controller;

import io.citytrees.service.TreeService;
import io.citytrees.v1.controller.TreeControllerApiDelegate;
import io.citytrees.v1.model.TreeCreateRequest;
import io.citytrees.v1.model.TreeCreateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

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
    @PreAuthorize("hasAnyRole(@Roles.ADMIN) || (isAuthenticated() && hasPermission(#id, @Domains.TREE, @Permissions.DELETE))")
    public ResponseEntity<Void> deleteTree(UUID id) {
        treeService.delete(id);
        return ResponseEntity.ok().build();
    }
}
