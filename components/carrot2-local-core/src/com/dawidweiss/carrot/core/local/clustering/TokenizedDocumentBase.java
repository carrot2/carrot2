/*
 * TokenizedDocumentBase.java Created on 2004-06-15
 */
package com.dawidweiss.carrot.core.local.clustering;

import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
import com.dawidweiss.carrot.util.common.*;

/**
 * An abstract implementation of the {@link TokenizedDocument}interface.
 * 
 * @author stachoo
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
    public double getDoubleProperty(String propertyName)
    {
        return propertyHelper.getDoubleProperty(propertyName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.util.common.PropertyProvider#getIntProperty(java.lang.String)
     */
    public int getIntProperty(String propertyName)
    {
        return propertyHelper.getIntProperty(propertyName);
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