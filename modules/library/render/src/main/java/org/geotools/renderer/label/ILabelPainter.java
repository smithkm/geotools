package org.geotools.renderer.label;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.List;

public interface ILabelPainter {

    LabelCacheItem getLabelItem();

    List<LineInfo> getLines();

    /**
     * Sets the current label. The label will be laid out according to the label
     * item settings (curved lines, auto wrapping, curved line usage) and the
     * painter will be ready to draw it.
     * 
     * @param labelItem
     */
    void setLabel(LabelCacheItem labelItem);

    /**
     * Returns the current label item
     * 
     * @return
     */
    LabelCacheItem getLabel();

    /**
     * Returns the line height for this label in pixels (for multiline labels,
     * it's the height of the first line)
     * 
     * @return
     */
    double getLineHeight();

    /**
     * The full size above the baseline 
     * @return
     */
    double getAscent();

    /**
     * Returns the width of the label, as painted in straight form (
     * 
     * @return
     */
    int getStraightLabelWidth();

    /**
     * Number of lines for this label (more than 1 if the label has embedded
     * newlines or if we're auto-wrapping it)
     * 
     * @return
     */
    int getLineCount();

    /**
     * Get the straight label bounds, taking into account halo, shield and line
     * wrapping
     * 
     * @return
     */
    Rectangle2D getFullLabelBounds();

    /**
     * Get the straight label bounds, without taking into account halo and
     * shield
     * 
     * @return
     */
    Rectangle2D getLabelBounds();

    /**
     * Paints the label as a non curved one. The positioning and rotation are
     * provided by the transformation
     * 
     * @param transform
     * @throws Exception
     */
    void paintStraightLabel(AffineTransform transform) throws Exception;

    /**
     * Paints a label that follows the line, centered in the current cursor
     * position
     * 
     * @param cursor
     */
    void paintCurvedLabel(LineStringCursor cursor);

    /**
     * Vertical centering is not trivial, because visually we want centering on
     * characters such as a,m,e, and not centering on d,g whose center is
     * affected by the full ascent or the full descent. This method tries to
     * computes the y anchor taking into account those.
     */
    double getLinePlacementYAnchor();

    /**
     * Add a representation of the given extent to the output for debugging.
     * @param labelEnvelope
     */
    void debug(Rectangle2D labelEnvelope);

    /**
     * Returns true if a label placed in the current cursor position would look
     * upwards or not, defining upwards a label whose bottom to top direction is
     * greater than zero, and less or equal to 180 degrees.
     * 
     * @param cursor
     * @return
     */
    boolean isLabelUpwards(LineStringCursor cursor);

}