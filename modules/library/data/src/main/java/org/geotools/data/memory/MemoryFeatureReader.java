/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2015, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.data.memory;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.geotools.data.DataSourceException;
import org.geotools.data.FeatureReader;
import org.geotools.data.Query;
import org.geotools.data.store.ContentState;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public class MemoryFeatureReader implements FeatureReader<SimpleFeatureType, SimpleFeature>{
    ContentState state;
    SimpleFeatureType featureType;
    Iterator<SimpleFeature> iterator;

    public MemoryFeatureReader(ContentState state, Query query) throws IOException {
        this.state = state;
        featureType = state.getFeatureType();
        String typeName = getFeatureType().getTypeName();
        MemoryDataStore store = (MemoryDataStore) state.getEntry().getDataStore();
        iterator = store.features(typeName).values().iterator();
    }

    public SimpleFeatureType getFeatureType() {
        return state.getFeatureType();
    }

    public SimpleFeature next()
        throws IOException, IllegalAttributeException, NoSuchElementException {
        if (iterator == null) {
            throw new IOException("Feature Reader has been closed");
        }

        try {
            return SimpleFeatureBuilder.copy((SimpleFeature) iterator.next());
        } catch (NoSuchElementException end) {
            throw new DataSourceException("There are no more Features", end);
        }
    }

    public boolean hasNext(){
        return (iterator != null) && iterator.hasNext();
    }

    public void close(){
        if (iterator != null) {
            iterator = null;
        }

        if (featureType != null) {
            featureType = null;
        }
    }
}
