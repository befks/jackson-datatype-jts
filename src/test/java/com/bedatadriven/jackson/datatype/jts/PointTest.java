package com.bedatadriven.jackson.datatype.jts;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

/**
 * Created by mihaildoronin on 11/11/15.
 */
public class PointTest extends BaseJtsModuleTest<Point> {
    @Override
    protected Class<Point> getType() {
        return Point.class;
    }

    @Override
    protected String createGeometryAsGeoJson() {
        return "{\"type\":\"Point\",\"coordinates\":[1.2345678,2.3456789]}";
    }

    @Override
    protected Point createGeometry() {
        return gf.createPoint(new Coordinate(1.2345678, 2.3456789));
    }
}
