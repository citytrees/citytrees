package io.citytrees.model;

import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.locationtech.jts.geom.Point;

@Value
@Builder
public class TreesCluster {

    @NotNull
    Point geoPoint;

    @NotNull
    Long count;
}
