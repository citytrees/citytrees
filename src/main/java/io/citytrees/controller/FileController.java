package io.citytrees.controller;

import io.citytrees.service.FileDownloadService;
import io.citytrees.service.FileService;
import io.citytrees.v1.controller.FileControllerApiDelegate;
import io.citytrees.v1.model.FileGetResponse;
import io.citytrees.v1.model.FileUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private final FileDownloadService fileDownloadService;

    @Override
    @PreAuthorize("permitAll()")
    public ResponseEntity<FileGetResponse> getFile(UUID id) {
        var optionalCtFile = fileService.getFile(id);
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
            .fileId(fileId)
            .url(fileDownloadService.generateDownloadUrl(fileId));

        return ResponseEntity.ok(response);
    }

    @Override
    @PreAuthorize("hasAuthority(@Roles.ADMIN) || (isAuthenticated() && hasPermission(#id, @Domains.FILE, @Permissions.DELETE))")
    public ResponseEntity<Void> deleteFile(UUID id) {
        fileService.delete(id);
        return ResponseEntity.ok().build();
    }

    @Override
    @PreAuthorize("permitAll()")
    public ResponseEntity<Resource> downloadFile(UUID id) {
        var optionalFile = fileService.getFile(id);
        if (optionalFile.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        var file = optionalFile.get();
        var optionalFileContent = fileService.getFileContent(file.getHash());
        if (optionalFileContent.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        var fileContent = optionalFileContent.get();
        return ResponseEntity
            .ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
            .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
            .header(HttpHeaders.PRAGMA, "no-cache")
            .header(HttpHeaders.EXPIRES, "0")
            .contentLength(file.getSize())
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(new ByteArrayResource(fileContent.getContent()));
    }
}
