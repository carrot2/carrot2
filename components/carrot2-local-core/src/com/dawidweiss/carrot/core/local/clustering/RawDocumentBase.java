
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

package com.dawidweiss.carrot.core.local.clustering;

import com.stachoodev.util.common.*;

/**
 * An abstract implementation of some of the basic methods of the {@link
 * RawDocument} interface.
 * 
 * @author Dawid Weiss
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public abstract class RawDocumentBase implements RawDocument, PropertyProvider
{
    /** Stores this document's properties */
    protected PropertyHelper propertyHelper;

    /**
     * Initializes the internal property storage.
     */
    public RawDocumentBase()
    {
        propertyHelper = new PropertyHelper();
    }

    /**
     * Creates a new raw document.
     * 
     * @param url
     * @param title
     * @param snippet
     */
    public RawDocumentBase(String url, String title, String snippet) {
        this();
	    setProperty(PROPERTY_URL, url);
	    setProperty(PROPERTY_TITLE, title);
	    setProperty(PROPERTY_SNIPPET, snippet);
    }
    
    /** 
     * Cloning constructor.
     */ 
    public RawDocumentBase(RawDocument r) {
	    this();
	    
	    if (r instanceof RawDocumentBase) {
		    try {
		    	this.propertyHelper = (PropertyHelper) ((RawDocumentBase)r).propertyHelper.clone();
	    	} catch (CloneNotSupportedException e) {
		    	throw new RuntimeException();
	    	}
	    } else {
		    setProperty(PROPERTY_URL, r.getProperty(PROPERTY_URL));
		    setProperty(PROPERTY_SNIPPET, r.getProperty(PROPERTY_SNIPPET));
		    setProperty(PROPERTY_TITLE, r.getProperty(PROPERTY_TITLE));
	    } 
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.clustering.RawDocument#getSnippet()
     */
    public String getSnippet()
    {
        return (String) propertyHelper.getProperty(PROPERTY_SNIPPET);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.clustering.RawDocument#getUrl()
     */
    public String getUrl()
    {
        return (String) propertyHelper.getProperty(PROPERTY_URL);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.clustering.RawDocument#getTitle()
     */
    public String getTitle()
    {
        return (String) propertyHelper.getProperty(PROPERTY_TITLE);
    }
	
    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.util.common.PropertyProvider#getProperty(java.lang.String)
     */
    public Object getProperty(String name)
    {
        return propertyHelper.getProperty(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.util.common.PropertyProvider#setProperty(java.lang.String,
     *      java.lang.Object)
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
        return propertyHelper.getDoubleProperty(propertyName,  defaultValue);
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

    /**
     * @return Returns 'no score' constant.
     */
    public float getScore()
    {
        return -1;
    }

    /**
     * Returns a stringified version of this raw document.
     */
    public String toString()
    {
        return "[URL=" + getUrl() + ", title=" + getTitle() + ", snippet: " + getSnippet() + "]"; 
    }
}