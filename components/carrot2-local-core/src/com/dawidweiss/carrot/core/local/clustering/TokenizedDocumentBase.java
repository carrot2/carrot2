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
package com.dawidweiss.carrot.core.local.clustering;

import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
import com.stachoodev.util.common.*;

/**
 * An abstract implementation of the {@link TokenizedDocument}interface.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public abstract class TokenizedDocumentBase implements TokenizedDocument,
    PropertyProvider
{
    /** Stores this document's properties */
    protected PropertyHelper propertyHelper;

    /**
     * 
     */
    public TokenizedDocumentBase()
    {
        propertyHelper = new PropertyHelper();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.clustering.TokenizedDocument#getSnippet()
     */
    public TokenSequence getSnippet()
    {
        return (TokenSequence) propertyHelper.getProperty(PROPERTY_SNIPPET);
    }

    /**
     * Provides a template of implementation of the named property getter.
     */
    public Object getProperty(String name)
    {
        return propertyHelper.getProperty(name);
    }

    /**
     * Sets a value for a named property in this document.
     */
    public Object setProperty(String propertyName, Object value)
    {
        return propertyHelper.setProperty(propertyName, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.util.common.PropertyProvider#getDoubleProperty(java.lang.String)
     */
    public double getDoubleProperty(String propertyName, double defaultValue)
    {
        return propertyHelper.getDoubleProperty(propertyName, defaultValue);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.util.common.PropertyProvider#getIntProperty(java.lang.String)
     */
    public int getIntProperty(String propertyName, int defaultValue)
    {
        return propertyHelper.getIntProperty(propertyName, defaultValue);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.util.common.PropertyProvider#setDoubleProperty(java.lang.String,
     *      double)
     */
    public Object setDoubleProperty(String propertyName, double value)
    {
        return propertyHelper.setDoubleProperty(propertyName, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.util.common.PropertyProvider#setIntProperty(java.lang.String,
     *      int)
     */
    public Object setIntProperty(String propertyName, int value)
    {
        return propertyHelper.setIntProperty(propertyName, value);
    }
}