/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
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
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

/**
 * A rasterizer for lineal geometries that uses fast integer arithmetic.
 * 
 * @author Kevin Smith, smithkm@draconic.ca
 *
 */
public class FastLineGeometryRasterizer extends AbstractGeometryRasterizer {

    
    @Override
    public void rasterize(Geometry g, Object o) {
        if(g instanceof LineString){
            draw((LineString) g, o);
        } else if(g instanceof MultiLineString) {
            for(int i=0; i<g.getNumGeometries(); i++){
                draw((LineString) g.getGeometryN(i), o);
            }
        } else {
            throw new IllegalArgumentException("FastLineGeometryRasterizer requires LineString or MultiLineString geometries, not "+g.getGeometryType());
        }
    }
    
    protected void bresenham(int i0, int j0, int i1, int j1, Object o) {
        int di = Math.abs(i1-i0);
        int dj = Math.abs(j1-j0);
        
        int si = i0<i1 ? 1: -1;
        int sj = j0<j1 ? 1: -1;
        
        int err = di-dj;
        
        while (true){
            handler.point(i0, j0, o, null);
            if(i0==i1 && j0==j1) break;
            int e2 = err*2;
            if(e2>-dj){
                err -= dj;
                i0 += si;
            }
            if(e2<di){
                err += di;
                j0 += sj;
            }
        }
    }
    
    protected void draw(LineString string, Object o){
        LineSegment seg = new LineSegment();
        seg.p1=string.getCoordinateN(0);
        for(int n=1; n<string.getNumPoints(); n++){
            seg.p0 = seg.p1;
            seg.p1 = string.getCoordinateN(n);
            
            int i0 = trans.i(seg.p0.x);
            int j0 = trans.j(seg.p0.y);
            int i1 = trans.i(seg.p1.x);
            int j1 = trans.j(seg.p1.y);
            
            bresenham(i0, j0, i1, j1, o);
        }
    }

}
