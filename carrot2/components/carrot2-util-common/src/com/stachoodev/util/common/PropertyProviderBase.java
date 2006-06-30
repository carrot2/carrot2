
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package com.stachoodev.util.common;

import java.util.Map;

/**
 * Base class implementing methods of {@link PropertyProvider}.
 *
 * Note that extending this class may be against the application logic,
 * but is often useful to keep the number of overriden methods smaller.
 *
 * @author Dawid Weiss
 */
public abstract class PropertyProviderBase implements PropertyProvider {
    /** Lazily initialized properties. */
    private final PropertyHelper propertyHelper = new PropertyHelper();

    public final int getIntProperty(String propertyName, int defaultValue) {
        return propertyHelper.getIntProperty(propertyName, defaultValue);
    }

    public final Object setIntProperty(String propertyName, int value) {
        return propertyHelper.setIntProperty(propertyName, value);
    }

    public final double getDoubleProperty(String propertyName, double defaultValue) {
        return propertyHelper.getDoubleProperty(propertyName, defaultValue);
    }

    public final Object setDoubleProperty(String propertyName, double value) {
        return propertyHelper.setDoubleProperty(propertyName, value);
    }

    public final Object getProperty(String propertyName) {
        return propertyHelper.getProperty(propertyName);
    }

    public final Object setProperty(String propertyName, Object value) {
        return propertyHelper.setProperty(propertyName, value);
    }

    /**
     * A shortcut to {@link PropertyHelper#setProperties(PropertyProvider, Object[][])}.
     */
    public final void setProperties(Object [][] props) {
        PropertyHelper.setProperties(this, props);
    }

    /**
     * Returns the properties stored in this class. 
     */
    protected Map getProperties() {
        return this.propertyHelper.getProperties();
    }

    /**
     * Replaces current properties with a shallow copy of properties
     * from another {@link PropertyProviderBase}. 
     */
    protected final void clonePropertiesFrom(final PropertyProviderBase base) {
        this.propertyHelper.clear();
        this.propertyHelper.copyFrom(base.propertyHelper);
    }

    /**
     * Returns the internal property helper used to store properties. 
     */
    protected final PropertyHelper getPropertyHelper() {
        return this.propertyHelper;
    }
}
