package org.geotools.process.vector;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

/**
 * Simple geometry rasterizer which sets a single pixel at the centroid of the geometry.
 * @author Kevin Smith, OpenGeo
 *
 */
public class CentroidGeometryRasterizer extends AbstractGeometryRasterizer {

    @Override
    public void rasterize(Geometry g, Object o) {
        Point p = g.getCentroid();
        
        int i = trans.i(p.getX());
        int j = trans.j(p.getY());
        
        handler.point(i, j, o);
    }

}
