package io.citytrees.service;

import io.citytrees.model.CtFile;
import io.citytrees.model.CtFileContent;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

public interface FileService {

    UUID upload(MultipartFile multipartFile);

    Optional<CtFile> getFile(UUID id);

    Optional<CtFileContent> getFileContent(String hash);

    void delete(UUID id);
}
