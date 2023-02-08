package io.citytrees.model;

import io.citytrees.constants.TableNames;
import io.citytrees.v1.model.WoodTypeStatus;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Value
@Builder
@Table(TableNames.WOOD_TYPE)
public class WoodType {

    @Id
    @NotNull
    UUID id;

    @NotNull
    String name;

    @NotNull
    WoodTypeStatus status;

    @Nullable
    UUID userId;
}
