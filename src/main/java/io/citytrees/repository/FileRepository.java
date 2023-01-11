package io.citytrees.repository;

import io.citytrees.model.CtFile;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface FileRepository extends CrudRepository<CtFile, UUID> {
    @Query("""
        INSERT INTO ct_file(id, name, mime_type, size, hash, user_id)
        VALUES (:id, :name, :mimeType, :size, :hash, :userId)
        RETURNING id
        """)
    UUID save(UUID id, String name, String mimeType, Long size, String hash, UUID userId);

    Optional<CtFile> findFirstByHash(String hash);

    long countByHash(String hash);
}
