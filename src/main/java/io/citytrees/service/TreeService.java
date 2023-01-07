package io.citytrees.service;

import io.citytrees.configuration.properties.GeoProperties;
import io.citytrees.model.Tree;
import io.citytrees.repository.TreeRepository;
import io.citytrees.v1.model.TreeCreateRequest;
import io.citytrees.v1.model.TreeStatus;
import io.citytrees.v1.model.TreeUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TreeService {

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

    private final GeoProperties geoProperties;
    private final SecurityService securityService;
    private final TreeRepository treeRepository;
    private final FileService fileService;

    public UUID create(TreeCreateRequest request) {
        Point point = GEOMETRY_FACTORY.createPoint(new Coordinate(request.getLatitude(), request.getLongitude()));
        point.setSRID(geoProperties.getSrid());
        return create(UUID.randomUUID(), securityService.getCurrentUserId(), point);
    }

    public UUID create(UUID id, UUID userId, Point point) {
        return create(id, userId, TreeStatus.NEW, point);
    }

    public void delete(UUID id) {
        treeRepository.deleteById(id);
    }

    public Optional<Tree> getById(UUID treeId) {
        return treeRepository.findTreeById(treeId);
    }

    public void update(UUID id, TreeUpdateRequest treeUpdateRequest) {
        update(id, securityService.getCurrentUserId(), treeUpdateRequest.getStatus());
    }

    public void update(UUID id, UUID userId, TreeStatus status) {
        treeRepository.update(id, userId, status);
    }

    @Transactional
    public UUID attachFile(UUID treeId, MultipartFile file) {
        UUID fileId = fileService.upload(file);
        treeRepository.attachFile(treeId, fileId);
        return fileId;
    }

    private UUID create(UUID id, UUID userId, TreeStatus status, Point point) {
        return treeRepository.create(id, userId, status, point.getX(), point.getY(), geoProperties.getSrid());
    }
}
