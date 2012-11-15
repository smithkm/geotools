package org.geotools.process.vector;

public abstract class AbstractGeometryRasterizer implements GeometryRasterizer {
    
    GridTransform trans;
    RasterHandler handler;
    
    @Override
    public void setTransformation(GridTransform trans) {
        this.trans = trans;
    }

    @Override
    public void setHandler(RasterHandler handler) {
        this.handler = handler;
    }

}
