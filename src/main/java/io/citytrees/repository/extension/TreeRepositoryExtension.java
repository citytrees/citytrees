package io.citytrees.repository.extension;

import io.citytrees.model.Tree;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TreeRepositoryExtension {

    Optional<Tree> findTreeById(UUID id);

    List<Tree> findByRegion(Double x1, Double y1, Double x2, Double y2, Integer srid);
}
