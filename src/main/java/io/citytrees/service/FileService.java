package io.citytrees.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import io.citytrees.configuration.properties.S3Properties;
import io.citytrees.model.CtFile;
import io.citytrees.model.User;
import io.citytrees.repository.FileRepository;
import io.citytrees.service.exception.FileServiceException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final S3Properties s3Properties;

    private final FileRepository fileRepository;
    private final SecurityService securityService;
    private final AmazonS3 s3;

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

    public Optional<CtFile> getById(UUID id) {
        return fileRepository.findById(id);
    }

    public UUID upload(MultipartFile file) {
        var s3File = saveToS3(file);
        return save(s3File);
    }

    public CtFile saveToS3(MultipartFile file) {
        return saveToS3(file, null);
    }

    @SneakyThrows
    public CtFile saveToS3(MultipartFile file, User user) {
        var fileBuilder = CtFile.builder();

        File tempFile = null;
        try (InputStream is = file.getInputStream()) {
            UUID uuid = UUID.randomUUID();
            tempFile = File.createTempFile(uuid.toString(), "tmp");
            FileUtils.copyInputStreamToFile(is, tempFile);

            var hash = DigestUtils.md5DigestAsHex(FileUtils.readFileToByteArray(tempFile));

            var savedFile = fileRepository.findCtFileByHash(hash);

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
            fileBuilder.userId(user != null ? user.getId() : securityService.getCurrentUserId());
        } catch (IOException e) {
            throw new FileServiceException(e.getMessage(), e);
        } finally {
            if (tempFile != null) {
                FileUtils.forceDelete(tempFile);
            }
        }

        return fileBuilder.build();
    }

    @SneakyThrows
    public ByteArrayResource loadFromS3(CtFile file) {
        byte[] content = s3.getObject(s3Properties.getBucket(), file.getHash()).getObjectContent().readAllBytes();
        return new ByteArrayResource(content);
    }

    public void delete(UUID id) {
        fileRepository.findById(id)
            .ifPresent(file -> {
                if (fileRepository.countByHash(file.getHash()) == 1) {
                    deleteFromS3(file);
                }

                fileRepository.deleteById(id);
            });
    }

    private void deleteFromS3(CtFile fileToDelete) {
        s3.deleteObject(s3Properties.getBucket(), fileToDelete.getHash());
    }

    /* TO BE USED ONLY IN TESTS! */
    @Deprecated
    public void drop(UUID id) {
        fileRepository.deleteById(id);
    }
}
