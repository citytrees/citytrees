package io.citytrees.service;

import io.citytrees.configuration.properties.GeoProperties;
import io.citytrees.model.Tree;
import io.citytrees.repository.TreeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TreesService {

    private final GeoProperties geoProperties;
    private final TreeRepository repository;

    public List<Tree> loadByRegion(Double x1, Double y1, Double x2, Double y2) {
        return repository.findByRegion(x1, y1, x2, y2, geoProperties.getSrid());
    }
}
