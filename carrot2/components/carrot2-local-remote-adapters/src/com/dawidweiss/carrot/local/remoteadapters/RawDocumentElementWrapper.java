
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
package com.dawidweiss.carrot.local.remoteadapters;

import java.util.HashMap;

import org.dom4j.Element;

import com.dawidweiss.carrot.core.local.clustering.RawDocument;


/**
 *
 */
public class RawDocumentElementWrapper 
    implements RawDocument
{
    private Element documentElement;

    /**
     * Lazily initialize hash map of
     * object's properties.
     */
    private HashMap properties;

    /**
     * 
     */
    public RawDocumentElementWrapper(Element documentElement)
    {
        this.documentElement = documentElement;
    }

    public Object getId() {
        return documentElement.attributeValue("id");
    }

    /**
     * @see com.dawidweiss.carrot.core.local.clustering.RawDocument#getUrl()
     */
    public String getUrl()
    {
        return documentElement.elementText("url");
    }

    /**
     * @see com.dawidweiss.carrot.core.local.clustering.RawDocument#getTitle()
     */
    public String getTitle()
    {
        return documentElement.elementText("title");
    }

    /**
     * @see com.dawidweiss.carrot.core.local.clustering.RawDocument#getContent()
     */
    public String getSnippet()
    {
        return documentElement.elementText("snippet");
    }

    /**
     * Returns the value of a named property.
     * 
     * @see com.dawidweiss.carrot.core.local.clustering.RawCluster#getProperty(java.lang.String)
     */
    public Object getProperty(String propertyName) {
        // the synchronized block should be before 'properties != null'
        // for 100% correct concurrent solution... but it seems unlikely
        // that pointer access would cause us much trouble here.
        if (properties != null) {
            synchronized (this) {
                Object value = properties.get(propertyName);
                if (value != null)
                    return value;
            }
        }
        if (RawDocument.PROPERTY_URL.equals(propertyName)) {
            return getUrl();
        } else if (RawDocument.PROPERTY_TITLE.equals(propertyName)) {
			return getTitle();
		} else if (RawDocument.PROPERTY_SNIPPET.equals(propertyName)) {
            return getSnippet();
        }
        return null;
    }

    /**
     * Sets a named property for this document.
     * @see com.dawidweiss.carrot.core.local.clustering.RawDocument#setProperty(java.lang.String)
     */
    public Object setProperty(String propertyName, Object value) {
        synchronized (this) {
            if (properties == null) {
                properties = new HashMap();
            }
            return properties.put(propertyName, value);
        }
    }


	/**
     * Returns the score of this document. 
     * Currently always returns -1 from this implementation.
     * 
	 * @see com.dawidweiss.carrot.core.local.clustering.RawDocument#getScore()
	 */
	public float getScore() {
        return -1;
	}

}
