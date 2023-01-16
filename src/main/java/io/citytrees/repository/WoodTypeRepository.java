package io.citytrees.repository;

import io.citytrees.model.WoodType;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface WoodTypeRepository extends CrudRepository<WoodType, UUID> {

    @NotNull
    List<WoodType> findAll();

    @Query("""
        INSERT INTO ct_wood_type(id, name, user_id)
        VALUES (:id, :name, :userId)
        ON CONFLICT(name)
        DO NOTHING
        RETURNING id
        """)
    UUID create(UUID id, String name, UUID userId);
}
