package com.bedatadriven.jackson.datatype.jts.serialization;

import com.bedatadriven.jackson.datatype.jts.parsers.GeometryParser;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.vividsolutions.jts.geom.Geometry;

import org.geotools.geometry.jts.JTS;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import java.io.IOException;

/**
 * Created by mihaildoronin on 11/11/15.
 */
public class GeometryDeserializer<T extends Geometry> extends JsonDeserializer<T> {

    private GeometryParser<T> geometryParser;

    private MathTransform transform;
    private int srid;

    public GeometryDeserializer(GeometryParser<T> geometryParser, MathTransform transform, int srid) {
        this.geometryParser = geometryParser;
        this.transform = transform;
        this.srid = srid;
    }

    @Override
    public T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode root = oc.readTree(jsonParser);
        T value = geometryParser.geometryFromJson(root);
        if (transform != null)
        {
            try
            {
                value = (T) JTS.transform(value, transform);
                value.setSRID(srid);
            }
            catch (TransformException e)
            {
                throw new IOException(e);
            }
        }
        return value;
    }
}
