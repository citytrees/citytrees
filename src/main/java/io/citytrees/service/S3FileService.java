package io.citytrees.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import io.citytrees.configuration.properties.S3Properties;
import io.citytrees.constants.FileStorageType;
import io.citytrees.model.CtFile;
import io.citytrees.model.CtFileContent;
import io.citytrees.repository.FileRepository;
import io.citytrees.service.exception.FileServiceException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "file-storage.type", havingValue = FileStorageType.S3)
public class S3FileService implements FileService {
    private final AmazonS3 s3;
    private final FileRepository fileRepository;
    private final S3Properties s3Properties;
    private final SecurityService securityService;

    public UUID save(CtFile file) {
        return fileRepository.save(
            file.getId(),
            file.getName(),
            file.getMimeType(),
            file.getSize(),
            file.getHash(),
            file.getUserId()
        );
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
    public UUID upload(MultipartFile multipartFile) {
        var s3File = saveToS3(multipartFile);
        return save(s3File);
    }

    @Override
    @SneakyThrows
    // todo # id, optional
    public Optional<CtFileContent> getFileContent(String hash) {
        byte[] content = s3.getObject(s3Properties.getBucket(), hash).getObjectContent().readAllBytes();
        return Optional.of(CtFileContent.builder()
            .id(UUID.randomUUID())
            .content(content)
            .hash(hash)
            .build());
    }

    @Override
    public void delete(UUID id) {
        fileRepository.findById(id)
            .ifPresent(file -> {
                if (fileRepository.countByHash(file.getHash()) == 1) {
                    deleteFromS3(file);
                }

                fileRepository.deleteById(id);
            });
    }

    @SneakyThrows
    private CtFile saveToS3(MultipartFile file) {
        var fileBuilder = CtFile.builder();

        File tempFile = null;
        try (InputStream is = file.getInputStream()) {
            UUID uuid = UUID.randomUUID();
            tempFile = File.createTempFile(uuid.toString(), "tmp");
            FileUtils.copyInputStreamToFile(is, tempFile);

            var hash = DigestUtils.md5DigestAsHex(FileUtils.readFileToByteArray(tempFile));

            var savedFile = fileRepository.findFirstByHash(hash);

            if (savedFile.isEmpty()) {
                s3.putObject(
                    new PutObjectRequest(s3Properties.getBucket(), hash, tempFile)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
                );
            }

            fileBuilder.id(uuid);
            fileBuilder.name(file.getOriginalFilename());
            fileBuilder.size(tempFile.length());
            fileBuilder.mimeType(file.getContentType());
            fileBuilder.hash(hash);
            fileBuilder.userId(securityService.getCurrentUserId());
        } catch (IOException e) {
            throw new FileServiceException(e.getMessage(), e);
        } finally {
            if (tempFile != null) {
                FileUtils.forceDelete(tempFile);
            }
        }

        return fileBuilder.build();
    }

    private void deleteFromS3(CtFile fileToDelete) {
        s3.deleteObject(s3Properties.getBucket(), fileToDelete.getHash());
    }

}
