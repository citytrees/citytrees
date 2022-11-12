package io.citytrees.controller;

import io.citytrees.service.FileService;
import io.citytrees.v1.controller.FileControllerApiDelegate;
import io.citytrees.v1.model.FileGetResponse;
import io.citytrees.v1.model.FileUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FileController extends BaseController implements FileControllerApiDelegate {

    private final FileService fileService;

    @Override
    @PreAuthorize("permitAll()")
    public ResponseEntity<FileGetResponse> getFile(UUID id) {
        var optionalCtFile = fileService.getById(id);
        if (optionalCtFile.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        var ctFile = optionalCtFile.get();
        return ResponseEntity.ok(
            new FileGetResponse()
                .id(ctFile.getId())
                .name(ctFile.getName())
                .mimeType(ctFile.getMimeType())
                .size(ctFile.getSize())
                .hash(ctFile.getHash())
                .userId(ctFile.getUserId())
        );
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FileUploadResponse> uploadFile(MultipartFile file) {
        var fileId = fileService.upload(file);
        var response = new FileUploadResponse()
            .fileId(fileId);

        return ResponseEntity.ok(response);
    }

    @Override
    @PreAuthorize("hasAnyRole(@Roles.ADMIN) || (isAuthenticated() && hasPermission(#id, @Domains.FILE, @Permissions.DELETE))")
    public ResponseEntity<Void> deleteFile(UUID id) {
        fileService.delete(id);
        return ResponseEntity.ok().build();
    }

    @Override
    @PreAuthorize("permitAll()")
    public ResponseEntity<Resource> downloadFile(UUID id) {
        var optionalFile = fileService.getById(id);
        if (optionalFile.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        var file = optionalFile.get();
        return ResponseEntity.ok(fileService.loadFromS3(file));
    }
}
