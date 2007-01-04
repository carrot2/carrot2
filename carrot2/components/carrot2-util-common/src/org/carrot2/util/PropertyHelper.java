
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util;

import java.util.*;

import org.apache.commons.collections.map.Flat3Map;

/**
 * Helps to implement properties of various objects. The property
 * container is initialized in a lazy manner (i.e. when the first request for
 * setting a value is received).
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class PropertyHelper implements PropertyProvider, Cloneable
{
    /** Property container */
    private Map properties;

    /**
     */
    public Map getProperties()
    {
        return properties;
    }
    
    /**
     * 
     */
    public void clear()
    {
        if (properties != null)
        {
            properties.clear();
        }
    }
    
    /*
     * (non-Javadoc)
     */
    public int getIntProperty(String propertyName, int defaultValue)
    {
        try
        {
            return ((Integer) getProperty(propertyName)).intValue();
        }
        catch (NullPointerException e)
        {
            return defaultValue;
        }
        catch (ClassCastException e)
        {
            return defaultValue;
        }
    }

    /*
     * (non-Javadoc)
     */
    public Object setIntProperty(String propertyName, int value)
    {
        return setProperty(propertyName, new Integer(value));
    }

    /*
     * (non-Javadoc)
     */
    public double getDoubleProperty(String propertyName, double defaultValue)
    {
        try
        {
            return ((Double) getProperty(propertyName)).doubleValue();
        }
        catch (NullPointerException e)
        {
            return defaultValue;
        }
        catch (ClassCastException e)
        {
            return defaultValue;
        }
    }

    /*
     * (non-Javadoc)
     */
    public Object setDoubleProperty(String propertyName, double value)
    {
        return setProperty(propertyName, new Double(value));
    }

    /*
     * (non-Javadoc)
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

    /*
     * (non-Javadoc)
     */
    public Object setProperty(String propertyName, Object property)
    {
        synchronized (this)
        {
            if (properties == null)
            {
                properties = new Flat3Map();
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
            // Assume that empty property helpers are equal, so return a
            // constant value
            return 31;
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
                // Assume empty property helpers are equal so return true
                return true;
            }
        }
    }

    /**
     * Copies all content of the <code>properties</code> to the
     * <code>propertyProvider</code>. The Map's keys will be treated as
     * property names and its values as property values.
     * 
     * @param propertyProvider The {@link PropertyProvider}to which the
     *            properties will be copied
     * @param properties The {@link Map}from which the properties will be
     *            copied
     */
    public static void setProperties(PropertyProvider propertyProvider,
        Map properties)
    {
        for (Iterator iter = properties.keySet().iterator(); iter.hasNext();)
        {
            Object key = iter.next();
            propertyProvider.setProperty(key.toString(), properties.get(key));
        }
    }
    
    /**
     * Sets pairs of <code>String,Object</code> properties in a given
     * {@link PropertyProvider}.
     * 
     * @param propertyProvider The {@link PropertyProvider}to which the
     *            properties will be copied
     * @param properties An array of two-element arrays, where the first
     * element is a {@link String} and the second is an {@link Object}.
     */
    public static void setProperties(PropertyProvider propertyProvider, Object [][] properties) {
        try {
            for (int i = 0; i < properties.length; i++) {
                final Object [] subarray = properties[i];
                if (subarray.length != 2) {
                    throw new IllegalArgumentException("An array of two-element arrays is required.");
                }
                propertyProvider.setProperty((String) subarray[0], subarray[1]);
            }
        } catch (ClassCastException ex) {
            throw new IllegalArgumentException("Key is not a string.");
        }
    }

    /**
     * Returns a {@link Comparator}for given <code>double</code> property. If
     * there is no property <code>propertyName</code> in either of the
     * compared {@link PropertyProvider}s, the result is unpredictable.
     * 
     * @param propertyName Name of the property which will be compared by the
     *            returned {@link Comparator}
     * @param reverse if <code>true</code> the 'reverse' comparator will be
     *            returned, e.g. it will yield descending sorting.
     */
    public static Comparator getComparatorForDoubleProperty(
        final String propertyName, final boolean reverse)
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

                if ((propertyStore1.getDoubleProperty(propertyName, 0) < propertyStore2
                    .getDoubleProperty(propertyName, 0))
                    ^ reverse)
                {
                    return -1;
                }
                else if ((propertyStore1.getDoubleProperty(propertyName, 0) > propertyStore2
                    .getDoubleProperty(propertyName, 0))
                    ^ reverse)
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
     * Returns a {@link Comparator}for given <code>int</code> property. If
     * there is no property <code>propertyName</code> in either of the
     * compared {@link PropertyProvider}s, the result is unpredictable.
     * 
     * @param propertyName Name of the property which will be compared by the
     *            returned {@link Comparator}
     * @param descending if <code>true</code> the 'reverse' comparator will be
     *            returned, e.g. it will yield descending sorting.
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

                if ((propertyStore1.getDoubleProperty(propertyName, 0) < propertyStore2
                    .getDoubleProperty(propertyName, 0))
                    ^ descending)
                {
                    return -1;
                }
                else if ((propertyStore1.getDoubleProperty(propertyName, 0) > propertyStore2
                    .getDoubleProperty(propertyName, 0))
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

    /**
     * Creates a <b>shallow</b> copy of this object's properties map.
     */
    public Object clone() throws CloneNotSupportedException
    {
        final PropertyHelper propertyHelper = new PropertyHelper();
        propertyHelper.copyFrom(this);
        return propertyHelper;
    }

    /**
     * Copies properties from another {@link PropertyHelper}, replacing
     * any existing properties if they overlap.
     */
    public void copyFrom(PropertyHelper other) {
        if (other.properties != null) {
            if (this.properties == null) {
                this.properties = new Flat3Map();
            }
            this.properties.putAll(other.properties);
        }
    }
}