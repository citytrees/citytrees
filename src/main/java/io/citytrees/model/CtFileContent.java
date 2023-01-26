package io.citytrees.model;

import io.citytrees.constants.TableNames;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Value
@Builder
@Table(TableNames.FILE_CONTENT_TABLE)
public class CtFileContent {

    @Id
    @NotNull
    UUID id;

    @NotNull
    byte[] content;

    @NotNull
    String hash;
}
