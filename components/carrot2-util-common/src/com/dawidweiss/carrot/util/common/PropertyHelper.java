/*
 * PropertyHelper.java Created on 2004-06-17
 */
package com.dawidweiss.carrot.util.common;

import java.util.*;

/**
 * Helps to implement properties of various objects such as
 * {@link com.stachoodev.carrot.filter.lingo.model.ExtendedToken}. The property
 * container is initialized in a lazy manner (i.e. when the first request for
 * setting a value is received).
 * 
 * @author stachoo
 */
public class PropertyHelper implements PropertyProvider
{
    /** Property container */
    private Map properties;

    /**
     * 
     */
    public PropertyHelper()
    {
    }

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
    public int getIntProperty(String propertyName)
    {
        return ((Integer) getProperty(propertyName)).intValue();
    }

    /**
     * Sets an <code>int</code> property.
     * 
     * @param propertyName
     * @param value
     * @return
     */
    public Object setIntProperty(String propertyName, int value)
    {
        return setProperty(propertyName, new Integer(value));
    }

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
    public double getDoubleProperty(String propertyName)
    {
        return ((Double) getProperty(propertyName)).doubleValue();
    }

    /**
     * Sets a <code>double</code> property.
     * 
     * @param propertyName
     * @param value
     * @return
     */
    public Object setDoubleProperty(String propertyName, double value)
    {
        return setProperty(propertyName, new Double(value));
    }

    /**
     * Returns a named property.
     * 
     * @param propertyName
     * @return
     */
    public Object getProperty(String propertyName)
    {
        if (properties != null)
        {
            return properties.get(propertyName);
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets a named property.
     * 
     * @param propertyName
     * @param property
     * @return previous value stored under given <code>propertyName</code> or
     *         <code>null</code>.
     */
    public Object setProperty(String propertyName, Object property)
    {
        synchronized (this)
        {
            if (properties == null)
            {
                properties = new HashMap();
            }

            return properties.put(propertyName, property);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        if (properties != null)
        {
            return properties.hashCode();
        }
        else
        {
            return super.hashCode();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (obj == null)
        {
            return false;
        }

        if (obj.getClass() != getClass())
        {
            return false;
        }
        else
        {
            if (properties != null)
            {
                return properties.equals(((PropertyHelper) obj).properties);
            }
            else
            {
                return super.equals(obj);
            }
        }
    }

    /**
     * Returns a {@link Comparator}for given <code>double</code> property.
     * 
     * @param propertyName
     * @param descending
     * @return
     */
    public static Comparator getComparatorForDoubleProperty(
        final String propertyName, final boolean descending)
    {
        return new Comparator()
        {

            public int compare(Object o1, Object o2)
            {
                if (!(o1 instanceof PropertyProvider))
                {
                    throw new ClassCastException(
                        "Object being compared must be instances of PropertyProvider");
                }

                if (!(o2 instanceof PropertyProvider))
                {
                    throw new ClassCastException(
                        "Object being compared must be instances of PropertyProvider");
                }

                PropertyProvider propertyStore1 = (PropertyProvider) o1;
                PropertyProvider propertyStore2 = (PropertyProvider) o2;

                if ((propertyStore1.getDoubleProperty(propertyName) < propertyStore2
                    .getDoubleProperty(propertyName))
                    ^ descending)
                {
                    return -1;
                }
                else if ((propertyStore1.getDoubleProperty(propertyName) > propertyStore2
                    .getDoubleProperty(propertyName))
                    ^ descending)
                {
                    return 1;
                }
                else
                {
                    return 0;
                }
            }
        };
    }


    /**
     * Returns a {@link Comparator}for given <code>int</code> property.
     * 
     * @param propertyName
     * @param descending
     * @return
     */
    public static Comparator getComparatorForIntProperty(
        final String propertyName, final boolean descending)
    {
        return new Comparator()
        {

            public int compare(Object o1, Object o2)
            {
                if (!(o1 instanceof PropertyProvider))
                {
                    throw new ClassCastException(
                        "Object being compared must be an instance of PropertyProvider");
                }

                if (!(o2 instanceof PropertyProvider))
                {
                    throw new ClassCastException(
                        "Object being compared must be an instance of PropertyProvider");
                }

                PropertyProvider propertyStore1 = (PropertyProvider) o1;
                PropertyProvider propertyStore2 = (PropertyProvider) o2;

                if ((propertyStore1.getDoubleProperty(propertyName) < propertyStore2
                    .getDoubleProperty(propertyName))
                    ^ descending)
                {
                    return -1;
                }
                else if ((propertyStore1.getDoubleProperty(propertyName) > propertyStore2
                    .getDoubleProperty(propertyName))
                    ^ descending)
                {
                    return 1;
                }
                else
                {
                    return 0;
                }
            }
        };
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        if (properties != null)
        {
            return properties.toString();
        }
        else
        {
            return "[empty]";
        }
    }
}