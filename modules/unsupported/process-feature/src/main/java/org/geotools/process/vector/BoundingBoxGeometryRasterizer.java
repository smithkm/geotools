package org.geotools.process.vector;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Simple rasterizer which covers the entire bounding box of the geometry.
 * @author Kevin Smith, OpenGeo
 *
 */
public class BoundingBoxGeometryRasterizer extends AbstractGeometryRasterizer {

    @Override
    public void rasterize(Geometry g, Object o) {
        final Envelope geomBounds = g.getEnvelopeInternal();
        
        
        final int minI = trans.i(geomBounds.getMinX());
        final int minJ = trans.j(geomBounds.getMinY());
        final int maxI = trans.i(geomBounds.getMaxX());
        final int maxJ = trans.j(geomBounds.getMaxY());
        
        
        iLoop: for (int i = minI;  i <= maxI;  i++) {
                for (int j = minJ;  j <= maxJ;  j++) {
                    try{
                        handler.point(i, j, o);
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        continue iLoop;
                        // Handle silently.  It's not pretty but it works.
                        // Should probably add a method to GridTransform that allows the bounds to
                        // be expended while leaving clamp on.  Then this shouldn't be needed.
                    }
                }
        }
    }

}
