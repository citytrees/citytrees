package io.citytrees.controller;

import io.citytrees.service.WoodTypeService;
import io.citytrees.v1.controller.WoodTypeControllerApiDelegate;
import io.citytrees.v1.model.WoodTypeCreateRequest;
import io.citytrees.v1.model.WoodTypeResponse;
import io.citytrees.v1.model.WoodTypeStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WoodTypeController implements WoodTypeControllerApiDelegate {

    private final WoodTypeService service;

    @Override
    @PreAuthorize("hasAuthority(@Roles.ADMIN)")
    public ResponseEntity<Void> createWoodType(WoodTypeCreateRequest woodTypeCreateRequest) {
        service.create(woodTypeCreateRequest.getName());
        return ResponseEntity.ok().build();
    }

    @Override
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<WoodTypeResponse>> getAllWoodTypes() {
        var response = service.getAllWoodTypes()
            .stream()
            .map(type -> new WoodTypeResponse()
                .id(type.getId())
                .name(type.getName())
                .status(type.getStatus())
                .userId(type.getUserId()))
            .toList();

        return ResponseEntity.ok(response);
    }

    @Override
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<WoodTypeResponse>> getAllWoodTypesByName(String name) {
        var response = service.getAllWoodTypesByName(name)
            .stream()
            .map(type -> new WoodTypeResponse()
                .id(type.getId())
                .name(type.getName())
                .status(type.getStatus())
                .userId(type.getUserId()))
            .toList();

        return ResponseEntity.ok(response);
    }

    @Override
    @PreAuthorize("hasAuthority(@Roles.ADMIN)")
    public ResponseEntity<Void> deleteWoodType(UUID id) {
        service.updateStatus(id, WoodTypeStatus.DELETED);
        return ResponseEntity.ok().build();
    }
}
