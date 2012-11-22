/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
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

import com.vividsolutions.jts.geom.Envelope;

/**
 * An affine transformation between two parallel 
 * coordinate systems, one defined by an {@link Envelope}
 * and one defined by a discrete zero-based grid
 * representing the same area as the envelope.
 * The transformation incorporates an isotropic scaling and a translation.
 * <p>
 * By default output values are clamped to the input envelope.
 * This behaviour can be disabled, in which case the client
 * must check that values are in an acceptable range.
 * 
 * @author Martin Davis - OpenGeo
 * @author Kevin Smith - OpenGeo
 *
 */
class GridTransform {

    final private Envelope env;

    final private int xSize;

    final private int ySize;

    final private double dx;

    final private double dy;

    private boolean isClamped = true;

    /**
     * Creates a new transform.
     * 
     * @param env the envelope defining one coordinate system
     * @param xSize the number of cells along the X axis of the grid
     * @param ySize the number of cells along the Y axis of the grid
     */
    public GridTransform(Envelope env, int xSize, int ySize) {
        this.env = env;
        this.xSize = xSize;
        this.ySize = ySize;
        dx = env.getWidth() / (xSize);
        dy = env.getHeight() / (ySize);
    }
    

    /**
     * Returns a GridTransform with the same scale factors, but with an expanded envelope. Negative 
     * margins may be used to contract the envelope. 
     * 
     * For points within both envelopes or when clamping is off, the pixel coordinates in the new 
     * grid will be increased by the minI/J parameters given here, relative to the old grid.
     * 
     * @param minI margin in pixels to expand the lower I/X bound by.
     * @param minJ margin in pixels to expand the lower J/Y bound by.
     * @param maxI margin in pixels to expand the upper I/X bound by.
     * @param maxJ margin in pixels to expand the upper J/Y bound by.
     * @return 
     */
    public GridTransform expand(int minI, int minJ, int maxI, int maxJ){
        int newXSize = this.xSize+minI+maxI;
        int newYSize = this.ySize+minJ+maxJ;
        
        Envelope newEnv = new Envelope(
                env.getMinX()-minI*dx, 
                env.getMaxX()+maxI*dx,
                env.getMinY()-minJ*dy, 
                env.getMaxY()+maxJ*dy);

        GridTransform newGrid = new GridTransform(newEnv, newXSize, newYSize);
        newGrid.setClamp(isClamped); // New Transform should have same clamped state as old one.
        
        return newGrid;
    }
    
    /**
     * Returns a GridTransform equivalent to this one, but with an expanded envelope. A negative 
     * margin may be used to contract the envelope.
     * 
     * For points within both envelopes or when clamping is off, the pixel coordinates in the new 
     * grid will be increased by the margin parameter given here, relative to the old grid.
     * 
     * @param margin margin in pixels to expand the bounds by.
     * @return 
     */
    public GridTransform expand(int margin){
        
        return this.expand(margin, margin, margin, margin);
    }
    
    /**
     * Sets whether to clamp outputs from transform to input envelope.
     * Default is to clamp the outputs.
     * 
     * Clamped values will be set to -1 or x/ySize. 
     * 
     * @param isClamped true if input is to be clamped
     */
    public void setClamp(boolean isClamped)
    {
        this.isClamped  = isClamped;
    }

    /**
     * The envelope of the GridTransformation
     * @return
     */
    public Envelope getEnv() {
        return env;
    }


    /**
     * The width of the grid
     * @return
     */
    public int getXSize() {
        return xSize;
    }


    /**
     * The height of the grid
     * @return
     */
   public int getYSize() {
        return ySize;
    }


    /**
     * Computes the X ordinate of the centre of the Ith grid column.
     * @param i the index of a grid column
     * @return the X ordinate of the column
     */
    public double x(int i) {
        if (i >= xSize - 1)
            return env.getMaxX();
        // Get the centre of the cell
        return env.getMinX() + (2*i+1) * dx / 2;
    }

    /**
     * Computes the Y ordinate of the centre of the Jth grid row.
     * @param j the index of a grid row
     * @return the Y ordinate of the row
     */
    public double y(int j) {
        if (j >= ySize - 1)
            return env.getMaxY();
        // Get the centre of the cell
       return env.getMinY() + (2*j+1) * dy /2;
    }

    /**
     * Computes the column index of an X ordinate.
     * @param x the X ordinate
     * @return the column index
     */
    public int i(double x) {
        if (isClamped && x > env.getMaxX())
            return xSize;
        if (isClamped && x < env.getMinX())
            return -1;
        int i = (int) ((x - env.getMinX()) / dx);
        // have already check x is in bounds, so ensure returning a valid value
        if (isClamped && i >= xSize)
            i = xSize - 1;
        return i;
    }

    /**
     * Computes the column index of an Y ordinate.
     * @param y the Y ordinate
     * @return the column index
     */
    public int j(double y) {
        if (isClamped && y > env.getMaxY())
            return ySize;
        if (isClamped && y < env.getMinY())
            return -1;
        int j = (int) ((y - env.getMinY()) / dy);
        // have already check x is in bounds, so ensure returning a valid value
        if (isClamped && j >= ySize)
            j = ySize - 1;
        return j;
    }

    /**
     * Computes the column index of an X ordinate.  Out of bounds ordinates are set to the nearest edge.
     * @param x
     * @return
     */
    public int safeI(double x){
        int i = i(x);
        if(isClamped){
            if(i<0) return 0;
            if(i>=getXSize()) return i-1;
        }
        return i;
    }
    
    /**
     * Computes the row index of a Y ordinate.  Out of bounds ordinates are set to the nearest edge.
     * @param y
     * @return
     */
    public int safeJ(double y){
        int j = j(y);
        if(isClamped){
            if(j<0) return 0;
            if(j>=getYSize()) return j-1;
        }
        return j;
    }
}