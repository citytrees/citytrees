package io.citytrees.service;

import io.citytrees.model.Tree;
import io.citytrees.repository.TreeRepository;
import io.citytrees.v1.model.TreeCreateRequest;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TreeService {

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

    private final SecurityService securityService;
    private final TreeRepository treeRepository;

    public UUID create(TreeCreateRequest request) {
        Point point = GEOMETRY_FACTORY.createPoint(new Coordinate(request.getLatitude(), request.getLongitude()));
        point.setSRID(4326);
        return create(UUID.randomUUID(), securityService.getCurrentUserId(), point);
    }

    public UUID create(UUID id, UUID userId, Point point) {
        return treeRepository.create(id, userId, point.getX(), point.getY(), 4326);
    }

    public void delete(UUID id) {
        treeRepository.deleteById(id);
    }

    public Optional<Tree> getById(UUID treeId) {
        return treeRepository.findTreeById(treeId);
    }
}
