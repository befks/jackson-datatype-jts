package com.bedatadriven.jackson.datatype.jts;

import com.bedatadriven.jackson.datatype.jts.parsers.*;
import com.bedatadriven.jackson.datatype.jts.serialization.GeometryDeserializer;
import com.bedatadriven.jackson.datatype.jts.serialization.GeometrySerializer;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.vividsolutions.jts.geom.*;

import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.FactoryException;

public class JtsModule3D extends SimpleModule {

    private MathTransform transformToJson = null;
    private MathTransform transformFromJson = null;

    public JtsModule3D() {
        this(new GeometryFactory(), 0, 0);
    }
    
    public JtsModule3D(GeometryFactory geometryFactory) {
        this(geometryFactory, 0, 0);
    }

    public JtsModule3D(int javaSRID, int jsonSRID) {
        this(new GeometryFactory(), javaSRID, jsonSRID);
    }

    public JtsModule3D(GeometryFactory geometryFactory, int javaSRID, int jsonSRID) {
        super("JtsModule3D", new Version(1, 0, 0, null,"com.bedatadriven","jackson-datatype-jts"));

        if (javaSRID > 0 && jsonSRID > 0)
        {
            try
            {
                CoordinateReferenceSystem javaCRS = CRS.decode("EPSG:" + javaSRID);
                CoordinateReferenceSystem jsonCRS = CRS.decode("EPSG:" + jsonSRID);
                transformToJson = CRS.findMathTransform(javaCRS, jsonCRS, false);
                transformFromJson = CRS.findMathTransform(jsonCRS, javaCRS, false);
            }
            catch (NoSuchAuthorityCodeException e)
            {
                throw new RuntimeException(e);
            }
            catch(FactoryException e)
            {
                throw new RuntimeException(e);
            }
        }

        addSerializer(Geometry.class, new GeometrySerializer(transformToJson));
        GenericGeometryParser genericGeometryParser = new GenericGeometryParser(geometryFactory);
        addDeserializer(Geometry.class, new GeometryDeserializer<Geometry>(genericGeometryParser, transformFromJson, javaSRID));
        addDeserializer(Point.class, new GeometryDeserializer<Point>(new PointParser(geometryFactory), transformFromJson, javaSRID));
        addDeserializer(MultiPoint.class, new GeometryDeserializer<MultiPoint>(new MultiPointParser(geometryFactory), transformFromJson, javaSRID));
        addDeserializer(LineString.class, new GeometryDeserializer<LineString>(new LineStringParser(geometryFactory), transformFromJson, javaSRID));
        addDeserializer(MultiLineString.class, new GeometryDeserializer<MultiLineString>(new MultiLineStringParser(geometryFactory), transformFromJson, javaSRID));
        addDeserializer(Polygon.class, new GeometryDeserializer<Polygon>(new PolygonParser(geometryFactory), transformFromJson, javaSRID));
        addDeserializer(MultiPolygon.class, new GeometryDeserializer<MultiPolygon>(new MultiPolygonParser(geometryFactory), transformFromJson, javaSRID));
        addDeserializer(GeometryCollection.class, new GeometryDeserializer<GeometryCollection>(new GeometryCollectionParser(geometryFactory, genericGeometryParser), transformFromJson, javaSRID));
    }

    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);
    }
}
