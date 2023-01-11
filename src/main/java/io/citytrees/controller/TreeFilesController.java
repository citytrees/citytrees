package io.citytrees.controller;

import io.citytrees.model.CtFile;
import io.citytrees.service.FileService;
import io.citytrees.service.TreeService;
import io.citytrees.v1.controller.TreeFilesControllerApiDelegate;
import io.citytrees.v1.model.FileUploadResponse;
import io.citytrees.v1.model.TreeGetAttachedFileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TreeFilesController implements TreeFilesControllerApiDelegate {
    private final FileService fileService;
    private final TreeService treeService;

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FileUploadResponse> attachFile(UUID treeId, MultipartFile file) {
        var fileId = treeService.attachFile(treeId, file);
        var response = new FileUploadResponse()
            .fileId(fileId)
            .url(fileService.generateDownloadUrl(fileId));

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<TreeGetAttachedFileResponse>> getAllAttachedFiles(UUID treeId) {
        List<CtFile> files = treeService.getAttachedFiles(treeId);

        List<TreeGetAttachedFileResponse> response = files.stream()
            .map(file -> new TreeGetAttachedFileResponse()
                .id(file.getId())
                .name(file.getName())
                .size(BigDecimal.valueOf(file.getSize()))
                .url(fileService.generateDownloadUrl(file.getId())))
            .toList();

        return ResponseEntity.ok(response);
    }
}
