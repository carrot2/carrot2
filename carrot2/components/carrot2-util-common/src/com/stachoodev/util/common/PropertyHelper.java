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
package com.stachoodev.util.common;

import java.util.*;

import org.apache.commons.collections.map.*;

/**
 * Helps to implement properties of various objects such as
 * {@link com.dawidweiss.carrot.core.local.linguistic.tokens.ExtendedToken}. The property
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
     * @return
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
     * 
     * @see com.stachoodev.util.common.PropertyProvider#getIntProperty(java.lang.String,
     *      int)
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
     * 
     * @see com.stachoodev.util.common.PropertyProvider#setIntProperty(java.lang.String,
     *      int)
     */
    public Object setIntProperty(String propertyName, int value)
    {
        return setProperty(propertyName, new Integer(value));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.util.common.PropertyProvider#getDoubleProperty(java.lang.String,
     *      double)
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
     * 
     * @see com.stachoodev.util.common.PropertyProvider#setDoubleProperty(java.lang.String,
     *      double)
     */
    public Object setDoubleProperty(String propertyName, double value)
    {
        return setProperty(propertyName, new Double(value));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.util.common.PropertyProvider#getProperty(java.lang.String)
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
     * 
     * @see com.stachoodev.util.common.PropertyProvider#setProperty(java.lang.String,
     *      java.lang.Object)
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
     * Returns a {@link Comparator}for given <code>double</code> property. If
     * there is no property <code>propertyName</code> in either of the
     * compared {@link PropertyProvider}s, the result is unpredictable.
     * 
     * @param propertyName Name of the property which will be compared by the
     *            returned {@link Comparator}
     * @param reverse if <code>true</code> the 'reverse' comparator will be
     *            returned, e.g. it will yield descending sorting.
     * @return
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
     * @param reverse if <code>true</code> the 'reverse' comparator will be
     *            returned, e.g. it will yield descending sorting.
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
     * Creates a <b>shallow </b> copy of this PropertyHelper.
     *  
     */
    public Object clone() throws CloneNotSupportedException
    {
        PropertyHelper propertyHelper = new PropertyHelper();

        // Make a shallow copy of the properties
        if (properties != null)
        {
            propertyHelper.properties = new Flat3Map();
            propertyHelper.properties.putAll(properties);
        }

        return propertyHelper;
    }
}