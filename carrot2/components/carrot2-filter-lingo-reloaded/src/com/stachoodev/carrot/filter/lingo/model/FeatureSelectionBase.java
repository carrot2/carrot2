/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.filter.lingo.model;

import com.stachoodev.util.common.*;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public abstract class FeatureSelectionBase implements FeatureSelection,
    PropertyProvider
{
    /** Handles properties */
    private PropertyHelper propertyHelper = new PropertyHelper();

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.util.common.PropertyProvider#getDoubleProperty(java.lang.String,
     *      double)
     */
    public double getDoubleProperty(String propertyName, double defaultValue)
    {
        return propertyHelper.getDoubleProperty(propertyName, defaultValue);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.util.common.PropertyProvider#getIntProperty(java.lang.String,
     *      int)
     */
    public int getIntProperty(String propertyName, int defaultValue)
    {
        return propertyHelper.getIntProperty(propertyName, defaultValue);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.util.common.PropertyProvider#getProperty(java.lang.String)
     */
    public Object getProperty(String propertyName)
    {
        return propertyHelper.getProperty(propertyName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.util.common.PropertyProvider#setDoubleProperty(java.lang.String,
     *      double)
     */
    public Object setDoubleProperty(String propertyName, double value)
    {
        return propertyHelper.setDoubleProperty(propertyName, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.util.common.PropertyProvider#setIntProperty(java.lang.String,
     *      int)
     */
    public Object setIntProperty(String propertyName, int value)
    {
        return propertyHelper.setIntProperty(propertyName, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.util.common.PropertyProvider#setProperty(java.lang.String,
     *      java.lang.Object)
     */
    public Object setProperty(String propertyName, Object value)
    {
        return propertyHelper.setProperty(propertyName, value);
    }
}