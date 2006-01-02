
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.util.common;

/**
 * Defines the interface of a class capable of storing named properties of
 * arbitrary types. Utility methods are also defined for accessing
 * <code>int</code> and <code>double</code> primitives.
 * 
 * A default implementation of this interface, as well as some utility methods
 * are provided by the {@link com.stachoodev.util.common.PropertyHelper}class.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public interface PropertyProvider
{
    /**
     * Returns an <code>int</code> value of a named property
     * <code>propertyName</code>. If there is no such property in this
     * provider or it cannot be converted to an <code>int</code> value, the
     * <code>defaultValue</code> will be returned.
     * 
     * @param propertyName Name of the property
     * @param defaultValue Default value returned when the requested property
     *            does not exist or when it cannot be converted into an
     *            <code>int</code> value.
     * @return <code>int</code> value of the property or the default value
     */
    public abstract int getIntProperty(String propertyName, int defaultValue);

    /**
     * Adds a named property of type <code>int</code> to this provider. If the
     * provider contained some mapping for given <code>propertyName</code>,
     * the previous value will be returned and replaced by the new value.
     * 
     * @param propertyName Name of the property
     * @param value Value of the property
     * @return previous value of the <code>propertyName</code> property or
     *         <code>null</code>
     */
    public abstract Object setIntProperty(String propertyName, int value);

    /**
     * Returns a <code>double</code> value of a named property
     * <code>propertyName</code>. If there is no such property in this
     * provider or it cannot be converted to a <code>double</code> value, the
     * <code>defaultValue</code> will be returned.
     * 
     * @param propertyName Name of the property
     * @param defaultValue Default value returned when the requested property
     *            does not exist or when it cannot be converted into a
     *            <code>double</code> value.
     * @return <code>double</code> value of the property or the default value
     */
    public abstract double getDoubleProperty(String propertyName,
        double defaultValue);

    /**
     * Adds a named property of type <code>double</code> to this provider. If
     * the provider contained some mapping for given <code>propertyName</code>,
     * the previous value will be returned and replaced by the new value.
     * 
     * @param propertyName Name of the property
     * @param value Value of the property
     * @return previous value of the <code>propertyName</code> property or
     *         <code>null</code>
     */
    public abstract Object setDoubleProperty(String propertyName, double value);

    /**
     * Returns an <code>Object</code> value of a named property
     * <code>propertyName</code>. If there is no such property in this
     * provider <code>null</code> will be returned.
     * 
     * @param propertyName Name of the property
     * @return <code>Object</code> value of the property or <code>null</code>
     */
    public abstract Object getProperty(String propertyName);

    /**
     * Adds a named property to this provider. If the provider contained some
     * mapping for given <code>propertyName</code>, the previous value will
     * be returned and replaced by the new value.
     * 
     * @param propertyName Name of the property
     * @param value Value of the property
     * @return previous value of the <code>propertyName</code> property or
     *         <code>null</code>
     */
    public abstract Object setProperty(String propertyName, Object value);
}