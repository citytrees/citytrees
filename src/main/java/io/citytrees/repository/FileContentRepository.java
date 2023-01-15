package io.citytrees.repository;

import io.citytrees.model.CtFileContent;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface FileContentRepository extends CrudRepository<CtFileContent, UUID> {

    @Query("""
        INSERT INTO ct_file_content(id, content, hash)
        VALUES (:id, :content, :hash)
        RETURNING id
        """)
    UUID save(UUID id, byte[] content, String hash);

    @Modifying
    @Query("""
        INSERT INTO ct_file_content(id, content, hash)
        VALUES (:id, :content, :hash)
        ON CONFLICT(hash)
        DO NOTHING;
        """)
    int upsert(UUID id, byte[] content, String hash);

    Optional<CtFileContent> findFirstByHash(String hash);

    void deleteByHash(String hash);
}
