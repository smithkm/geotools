/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2016 Open Source Geospatial Foundation (OSGeo)
 *    (C) 2014-2016 Boundless Spatial
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
package org.geotools.ysld.parse;

import static org.geotools.ysld.Ysld.transform;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;

import java.awt.*;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import org.geotools.styling.*;
import org.geotools.ysld.YsldTests;
import org.hamcrest.Matchers;
import org.junit.Test;

public class YsldNamedLayerTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testPointSimpleSLDTransform() throws Exception {
        StyledLayerDescriptor style = parse("named", "simple.sld");
        assertThat(
                style.getStyledLayers(),
                arrayContaining(
                        both(hasProperty("name", equalTo("SimpleLayer")))
                                .and(Matchers.instanceOf(NamedLayer.class))
                                .and(
                                        hasProperty(
                                                "styles",
                                                arrayContaining(
                                                        hasProperty(
                                                                "name",
                                                                equalTo("SimpleStyle")))))));
    }

    StyledLayerDescriptor parse(String dir, String file) throws IOException {
        StringWriter writer = new StringWriter();
        transform(YsldTests.sld(dir, file), writer);
        System.out.println(writer.toString());
        YsldParser p = new YsldParser(new StringReader(writer.toString()));
        return p.parse();
    }

    Color color(String hex) {
        return new Color(
                Integer.valueOf(hex.substring(0, 2), 16),
                Integer.valueOf(hex.substring(2, 4), 16),
                Integer.valueOf(hex.substring(4, 6), 16));
    }
}
