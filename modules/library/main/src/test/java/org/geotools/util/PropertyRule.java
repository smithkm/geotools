/* (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geotools.util;

import java.util.Properties;

/**
 * Rule which allows a property to be set, and will return it to its original value.
 * 
 * @author Kevin Smith, Boundless
 *
 */
public class PropertyRule extends org.junit.rules.ExternalResource {
    final Properties props;
    final String name;
    String oldValue;
    
    /**
     * Create a rule to manipulate a system property
     * @param name the name of the system property
     * @return
     */
    public static PropertyRule system(String name) {
        return new PropertyRule(System.getProperties(), name);
    }
    
    /**
     * Create a rule to manipulate a property
     * @param props The properties map containing the property
     * @param name The name of the property
     */
    public PropertyRule(Properties props, String name) {
        super();
        this.props = props;
        this.name = name;
    }
    
    /**
     * 
     * @return the value of the property before it was modified
     */
    public Object getOldValue() {
        return oldValue;
    }
    
    /**
     * 
     * Set the value of the property
     */
    public void setValue(String value) {
        props.setProperty(name, value);
    }
    
    /**
     * 
     * @return The name of the property
     */
    public String getName() {
        return name;
    }
    
    @Override
    protected void before() throws Throwable {
        this.oldValue = props.getProperty(name);
    }

    @Override
    protected void after() {
        if(this.oldValue==null) {
            props.remove(name);
        } else {
            props.setProperty(name, oldValue);
        }
    }
    
}