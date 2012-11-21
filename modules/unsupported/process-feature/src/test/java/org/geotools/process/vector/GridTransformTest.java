package org.geotools.process.vector;

import static org.junit.Assert.*;

import org.junit.Test;

import com.vividsolutions.jts.geom.Envelope;

public class GridTransformTest {

    @Test
    public void testSimple() {
        Envelope env = new Envelope(500, 1600, 400, 1400);
        GridTransform trans = new GridTransform(env, 11, 10);
        
        assertEquals(2, trans.i(750));
        assertEquals(4, trans.j(850));
        
        assertEquals(750.0, trans.x(2), 0.001);
        assertEquals(850.0, trans.y(4), 0.001);
        
    }
    
    @Test
    public void testClamp() {
        Envelope env = new Envelope(500, 1600, 400, 1400);
        GridTransform trans = new GridTransform(env, 11, 10);
        
        assertEquals(-1, trans.i(450));
        assertEquals(-1, trans.j(350));
        assertEquals(trans.getXSize(), trans.i(1650));
        assertEquals(trans.getYSize(), trans.j(1450));
    }
    
    @Test
    public void testExpand() {
        Envelope env = new Envelope(500, 1600, 400, 1400);
        GridTransform trans = new GridTransform(env, 11, 10);
        GridTransform trans2 = trans.expand(2, 1, 4, 3);
        
        assertEquals(new Envelope(300, 2000, 300, 1700), trans2.getEnv());
        assertEquals(17, trans2.getXSize());
        assertEquals(14, trans2.getYSize());
        
        
        // point inside 
        assertEquals(4, trans2.i(750));
        assertEquals(5, trans2.j(850));
        
        // points inside the new margins
        assertEquals(1, trans2.i(450));
        assertEquals(0, trans2.j(350));
        assertEquals(13, trans2.i(1650));
        assertEquals(11, trans2.j(1450));
        
        // points outside the new margins
        assertEquals(-1, trans.i(250));
        assertEquals(-1, trans.j(150));
        assertEquals(trans2.getXSize(), trans2.i(2050));
        assertEquals(trans2.getYSize(), trans2.j(1750));
        
    }

}
