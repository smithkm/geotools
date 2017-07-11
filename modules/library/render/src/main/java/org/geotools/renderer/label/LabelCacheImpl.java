/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2004-2015, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.renderer.label;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotools.geometry.jts.GeometryClipper;
import org.geotools.renderer.RenderListener;
import org.geotools.renderer.lite.LabelCache;
import org.geotools.styling.TextSymbolizer;
import org.geotools.util.logging.Logging;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Default LabelCache Implementation.
 * 
 * <p>The label cache sports a number of features that are enabled depending on
 * the programmatic configuration and the TextSymbolizer options.</p>
 * <p>The basic functionality of the label cache consist in finding the 
 * best label position for each Feature according to the {@link TextSymbolizer} 
 * specifications, and drawing it, provided it does not overlap with other labels.</p>
 * <p>This basic behaviour can be customised in a number of ways.</p>
 *  
 * 
 * <h2>Priority</h2>
 * <p>{@link TextSymbolizer#getPriority()} OGC Expression controls a label priority.</p>
 * <p>A label with high priority will be drawn before others, increasing its likeliness
 * to appear on the screen</p>
 *
 * @author jeichar
 * @author dblasby
 * @author Andrea Aime - OpenGeo
 *
 *
 * @source $URL$
 */
public final class LabelCacheImpl extends AbstractLabelCache<Graphics2D> {
    
    static final Logger LOGGER = Logging.getLogger(LabelCacheImpl.class);

    /**
     * @see org.geotools.renderer.lite.LabelCache#endLayer(String,Graphics2D,Rectangle)
     */
    @Override
    public void endLayer(String layerId, Graphics2D graphics, Rectangle displayArea) {
        activeLayers.remove(layerId);
    }

    @Override
    protected void paintLabels(Graphics2D graphics, Rectangle displayArea) {
        if (!activeLayers.isEmpty()) {
            throw new IllegalStateException(activeLayers
                    + " are layers that started rendering but have not completed,"
                    + " stop() or endLayer() must be called before end() is called");
        }
        LabelIndex glyphs = new LabelIndex();
        glyphs.reserveArea( reserved );

        //Used to check the paintLineLabel function
        boolean painted;
        int nonPaintedLineLabels = 0;
        int paintedLineLabels = 0;

        // Hack: let's reduce the display area width and height by one pixel.
        // If the rendered image is 256x256, proper rendering of polygons and
        // lines occurr only if the display area is [0,0; 256,256], yet if you
        // try to render anything at [x,256] or [256,y] it won't show.
        // So, to avoid labels that happen to touch the border being cut
        // by one pixel, we reduce the display area.
        // Feels hackish, don't have a better solution at the moment thought
        displayArea = new Rectangle(displayArea);
        displayArea.width -= 1;
        displayArea.height -= 1;
        
        // prepare the geometry clipper
        clipper = new GeometryClipper(new Envelope(displayArea.getMinX(), displayArea.getMaxX(), displayArea.getMinY(), displayArea.getMaxY()));

        List<LabelCacheItem> items; // both grouped and non-grouped
        if (needsOrdering) {
            items = orderedLabels();
        } else {
            items = getActiveLabels();
        }
        ILabelPainter painter = new LabelPainter(graphics, labelRenderingMode);
        for (LabelCacheItem labelItem : items) {
            if (stop)
                return;
            
            try {
                painter.setLabel(labelItem);
                // LabelCacheItem labelItem = (LabelCacheItem)
                // labelCache.get(labelIter.next());

                // DJB: simplified this. Just send off to the point,line,or
                // polygon routine
                // NOTE: labelItem.getGeometry() returns the FIRST geometry, so
                // we're assuming that lines & points arent mixed
                // If they are, then the FIRST geometry determines how its
                // rendered (which is probably bad since it should be in
                // area,line,point order
                // TOD: as in NOTE above

                /*
                 * Just use identity for tempTransform because display area is
                 * 0,0,width,height and oldTransform may have a different
                 * origin. OldTransform will be used later for drawing. -rg & je
                 */
                AffineTransform tempTransform = new AffineTransform();

                Geometry geom = labelItem.getGeometry();
                if ((geom instanceof Point) || (geom instanceof MultiPoint))
                    paintPointLabel(painter, tempTransform, displayArea, glyphs);
                else if (((geom instanceof LineString) && !(geom instanceof LinearRing))
                        || (geom instanceof MultiLineString)){
                     if(!DISABLE_LETTER_LEVEL_CONFLICT)
                         painted = paintLineLabelsWithLetterConflict(painter, tempTransform, displayArea, glyphs);
                     else
                         painted = paintLineLabels(painter, tempTransform, displayArea, glyphs);
                     if (!painted){
                         nonPaintedLineLabels++;
                     } else paintedLineLabels++;
                }
                else if (geom instanceof Polygon || geom instanceof MultiPolygon
                        || geom instanceof LinearRing)
                    paintPolygonLabel(painter, tempTransform, displayArea, glyphs);
            } catch (Exception e) {
                if(LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "Failure while painting labels", e);
                }
                for (RenderListener listener : renderListeners) {
                    listener.errorOccurred(e);
                }
            }
        }
        //Output for line labels
        LOGGER.log(Level.FINE, "TOTAL LINE LABELS : {0}", items.size());
        LOGGER.log(Level.FINE, "PAINTED LINE LABELS : {0}", paintedLineLabels);
        LOGGER.log(Level.FINE, "REMAINING LINE LABELS : {0}", nonPaintedLineLabels);
    }

    @Override
    public void end(Graphics2D graphics, Rectangle displayArea) {
        final Object antialiasing = graphics.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        final Object textAntialiasing = graphics
                .getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
        try {
            // if we are asked to antialias only text but we're drawing using
            // the outline
            // method, we need to re-enable graphics antialiasing during label
            // painting
            if (labelRenderingMode != LabelRenderingMode.STRING 
                    && antialiasing == RenderingHints.VALUE_ANTIALIAS_OFF
                    && textAntialiasing == RenderingHints.VALUE_TEXT_ANTIALIAS_ON) {
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
            }
            paintLabels(graphics, displayArea);
        } finally {
            if (antialiasing != null) {
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        antialiasing);
            }
        }
    }


}
