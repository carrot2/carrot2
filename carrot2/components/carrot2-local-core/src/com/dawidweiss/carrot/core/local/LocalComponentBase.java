/*
 * Carrot2 Project Copyright (C) 2002-2005, Dawid Weiss Portions (C)
 * Contributors listed in carrot2.CONTRIBUTORS file. All rights reserved.
 * 
 * Refer to the full license file "carrot2.LICENSE" in the root folder of the
 * CVS checkout or at: http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 * 
 * Sponsored by: CCG, Inc.
 */

package com.dawidweiss.carrot.core.local;

import java.util.HashMap;
import java.util.Set;

/**
 * A reference implementation of a {@link LocalComponent}interface. This class
 * is abstract, but provides empty implementations of {@link
 * LocalComponent#init(LocalControllerContext)} and capabilities-related
 * methods. Some request context property conversion-related helper methods are
 * also provided.
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
public abstract class LocalComponentBase implements LocalComponent
{
    private final HashMap properties = new HashMap();
    
    /**
     * Provides an empty implementation.
     * 
     * @param context A controller context instance.
     * 
     * @see LocalComponent#init(LocalControllerContext)
     */
    public void init(LocalControllerContext context)
        throws InstantiationException
    {
    }

    /**
     * Provides an implementation that requires no capabilities of the successor
     * component.
     */
    public Set getRequiredSuccessorCapabilities()
    {
        return java.util.Collections.EMPTY_SET;
    }

    /**
     * Provides an implementation that requires no capabilities of the
     * predecessor component.
     */
    public Set getRequiredPredecessorCapabilities()
    {
        return java.util.Collections.EMPTY_SET;
    }

    /**
     * Provides an implementation that has no capabilities (an empty set).
     */
    public Set getComponentCapabilities()
    {
        return java.util.Collections.EMPTY_SET;
    }

    /**
     * Returns an integer value of a named attribute <code>paramName</code>
     * from the request context <code>context</code>. If the request context
     * has no mapping for the named attribute, the <code>defaultValue</code>
     * is returned. If the conversion is not possible (because the value is not
     * an integer number), the default value is returned.
     * 
     * @param context Request context used to look up the value of a named
     *            attribute.
     * @param paramName Name of the attribute.
     * @param defaultValue Default value returned if the attribute is not
     *            available, or conversion failed.
     * 
     * @return An integer value of the named parameter, or the default value.
     */
    protected final int getIntFromRequestContext(RequestContext context,
        String paramName, int defaultValue)
    {
        Object value = context.getRequestParameters().get(paramName);

        if (value == null)
        {
            return defaultValue;
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        } else {
            try
            {
                return Integer.parseInt((String) value);
            }
            catch (NumberFormatException e)
            {
                return defaultValue;
            }
            catch (ClassCastException e)
            {
                return defaultValue;
            }
        }
    }

    /**
     * The default implementation ignores the property.
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#setProperty(java.lang.String,
     *      java.lang.String)
     */
    public void setProperty(String key, String value)
    {
        this.properties.put(key,value);
    }

    /**
     * Retrieves a property previously set in the component
     * using {@link #setProperty(String, String)}.
     * 
     * @param key Key
     * @return Returns a string, or a <code>null</code> if the property
     * has no binding.
     */
    protected String getProperty(String key) {
	    return (String) this.properties.get(key);
    }

    /**
     * The default implementation returning <code>null</code>. In concrete
     * implementations override this method to provide the appropriate name.
     * 
     * @return <code>null</code> name
     */
    public String getName()
    {
        return null;
    }

    /**
     * The default implementation returning <code>null</code>. In concrete
     * implementations override this method to provide the appropriate
     * description.
     * 
     * @return <code>null</code> description
     */
    public String getDescription()
    {
        return null;
    }
}