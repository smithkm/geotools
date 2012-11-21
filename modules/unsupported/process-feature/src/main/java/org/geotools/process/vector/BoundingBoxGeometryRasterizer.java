package org.geotools.process.vector;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Simple rasterizer which covers the entire bounding box of the geometry.
 * 
 * @author Kevin Smith, OpenGeo
 *
 */
public class BoundingBoxGeometryRasterizer extends AbstractGeometryRasterizer {

    @Override
    public void rasterize(Geometry g, Object o) {
        final Envelope geomBounds = g.getEnvelopeInternal();
        
        
        final int minI = trans.safeI(geomBounds.getMinX());
        final int minJ = trans.safeJ(geomBounds.getMinY());
        final int maxI = trans.safeI(geomBounds.getMaxX());
        final int maxJ = trans.safeJ(geomBounds.getMaxY());
        
        
        for (int i = minI;  i <= maxI;  i++) {
            for (int j = minJ;  j <= maxJ;  j++) {
                handler.point(i, j, o);
            }
        }
    }

}
