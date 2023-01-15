package io.citytrees.service;

import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeometryService {
    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

    public Point createPoint(Double x, Double y) {
        return GEOMETRY_FACTORY.createPoint(new Coordinate(x, y));
    }
}
