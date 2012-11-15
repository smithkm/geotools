/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008-2011 TOPP - www.openplans.org.
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


/**
 * Interface for an implementation of a rasterization algorithm
 * 
 * @author Kevin Smith, OpenGeo
 *
 */
public interface GeometryRasterizer {
    
    /**
     * Functor to abstract the raster implementation being written to
     *
     */
    public interface RasterHandler {
        /**
         * 
         * @param i the horizontal grid ordinate
         * @param j the vertical grid ordinate
         * @param o the user data passed in with the geometry
         */
        public void point(int i, int j, Object o);
    }
    
    /**
     * Set the GridTransformation used to transform from spatial to pixel coordinates
     * @param trans
     */
    public void setTransformation(GridTransform trans);
    
    /**
     * Set the handler used to write to the raster
     * @param handler
     */
    public void setHandler(RasterHandler handler);
    
    /**
     * Rasterize a geometry
     * @param g the geometry
     * @param o user data to pass to the handler
     */
    public void rasterize(Geometry g, Object o);
}
