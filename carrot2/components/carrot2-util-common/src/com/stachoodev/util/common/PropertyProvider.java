/*
 * PropertyProvider.java Created on 2004-06-20
 */
package com.stachoodev.util.common;

/**
 * Defines a number of method for getting/setting named properties.
 * 
 * @author stachoo
 */
public interface PropertyProvider
{
    /**
     * Returns an <code>int</code> property.
     * 
     * @param propertyName
     * @throws {@link NullPointerException}when there is no such property in
     *             the map. Use {@link #getProperty(String)}to check property
     *             existence.
     * @throws {@link ClassCastException}when the object stored under given
     *             property name is not an instance if {@link Integer}.
     * @return
     */
    public abstract int getIntProperty(String propertyName);

    /**
     * Sets an <code>int</code> property.
     * 
     * @param propertyName
     * @param value
     * @return
     */
    public abstract Object setIntProperty(String propertyName, int value);

    /**
     * Returns a <code>double</code> property.
     * 
     * @param propertyName
     * @throws {@link NullPointerException}when there is no such property in
     *             the map. Use {@link #getProperty(String)}to check property
     *             existence.
     * @throws {@link ClassCastException}when the object stored under given
     *             property name is not an instance if {@link Double}.
     * @return
     */
    public abstract double getDoubleProperty(String propertyName);

    /**
     * Sets a <code>double</code> property.
     * 
     * @param propertyName
     * @param value
     * @return
     */
    public abstract Object setDoubleProperty(String propertyName, double value);

    /**
     * Returns a named property.
     * 
     * @param propertyName
     * @return
     */
    public abstract Object getProperty(String propertyName);

    /**
     * Sets a named property.
     * 
     * @param propertyName
     * @param property
     * @return previous value stored under given <code>propertyName</code> or
     *         <code>null</code>.
     */
    public abstract Object setProperty(String propertyName, Object property);
}