package io.citytrees.service;

import io.citytrees.constants.FileStorageType;
import io.citytrees.model.CtFile;
import io.citytrees.model.CtFileContent;
import io.citytrees.repository.FileContentRepository;
import io.citytrees.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "file-storage.type", havingValue = FileStorageType.DATABASE)
public class DbFileService implements FileService {
    private final FileRepository fileRepository;
    private final FileContentRepository fileContentRepository;
    private final SecurityService securityService;

    @Override
    @SneakyThrows
    @Transactional
    public UUID upload(MultipartFile multipartFile) {
        var uuid = UUID.randomUUID();
        var content = multipartFile.getBytes();
        var hash = DigestUtils.md5DigestAsHex(content);

        fileRepository.save(
            uuid,
            Objects.requireNonNull(multipartFile.getOriginalFilename()),
            Objects.requireNonNull(multipartFile.getContentType()),
            content.length,
            hash,
            LocalDateTime.now(),
            securityService.getCurrentUserId());

        fileContentRepository.upsert(UUID.randomUUID(), content, hash);

        return uuid;
    }

    @Override
    public Optional<CtFile> getFile(UUID id) {
        return fileRepository.findById(id);
    }

    @Override
    public List<CtFile> listAllByIds(Set<UUID> uuids) {
        return fileRepository.findAllByIdIn(uuids);
    }

    @Override
    public Optional<CtFileContent> getFileContent(String hash) {
        return fileContentRepository.findFirstByHash(hash);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        var optionalFile = fileRepository.findById(id);
        if (optionalFile.isEmpty()) {
            return;
        }
        var ctFile = optionalFile.get();

        var hash = ctFile.getHash();
        if (fileRepository.countByHash(hash) == 1) {
            deleteContent(hash);
        }

        fileRepository.deleteById(id);
    }

    private void deleteContent(String hash) {
        fileContentRepository.deleteByHash(hash);
    }
}
