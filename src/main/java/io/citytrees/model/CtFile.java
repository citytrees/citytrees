package io.citytrees.model;

import io.citytrees.constants.TableNames;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
@Table(TableNames.FILE_TABLE)
public class CtFile {

    @Id
    @NotNull
    UUID id;

    @NotNull
    String name;

    @NotNull
    String mimeType;

    @NotNull
    Long size;

    @NotNull
    String hash;

    @NotNull
    LocalDateTime creationDateTime;

    @NotNull
    UUID userId;
}
