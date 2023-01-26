package io.citytrees.service;

import io.citytrees.model.CtFile;
import io.citytrees.model.CtFileContent;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface FileService {

    UUID upload(MultipartFile multipartFile);

    Optional<CtFile> getFile(UUID id);

    List<CtFile> listAllByIds(Set<UUID> uuids);

    Optional<CtFileContent> getFileContent(String hash);

    void delete(UUID id);
}
