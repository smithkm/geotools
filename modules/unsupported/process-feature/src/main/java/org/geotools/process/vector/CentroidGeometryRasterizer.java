/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

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
    public void rasterize(Geometry g, Object userData) {
        Point p = g.getCentroid();
        
        int i = trans.i(p.getX());
        int j = trans.j(p.getY());
        
        if(i<0 || i>= trans.getXSize()) return;
        if(j<0 || j>= trans.getYSize()) return;
        
        handler.point(i, j, userData, null);
    }

}
