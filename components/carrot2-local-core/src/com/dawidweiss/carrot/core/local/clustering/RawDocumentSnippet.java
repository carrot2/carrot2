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

import com.stachoodev.util.common.*;

/**
 * An implementation of the
 * {@link com.dawidweiss.carrot.core.local.clustering.RawDocument}interface
 * suitable for storing document references, so called snippets, returned by a
 * search engine. Does <b>not </b> provide document content.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class RawDocumentSnippet extends RawDocumentBase implements Cloneable
{
    /** Document title */
    private String title;

    /**
     * Creates a new RawDocumentSnippet with given title and description.
     * 
     * @param title
     * @param snippet
     */
    public RawDocumentSnippet(String title, String snippet)
    {
        this.title = title;
        setProperty(PROPERTY_SNIPPET, snippet);
    }

    /**
     * @param title
     * @param snippet
     */
    public RawDocumentSnippet(String title, String snippet, String language)
    {
        this(title, snippet);
        setProperty(RawDocument.PROPERTY_LANGUAGE, language);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.clustering.RawDocument#getId()
     */
    public Object getId()
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.clustering.RawDocument#getUrl()
     */
    public String getUrl()
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.clustering.RawDocument#getTitle()
     */
    public String getTitle()
    {
        return title;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "[" + title + "] " + getSnippet().toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        if (obj == this)
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

        RawDocumentSnippet otherSnippet = (RawDocumentSnippet) obj;
        boolean result = true;
        if (title != null)
        {
            result = result && title.equals(otherSnippet.title);
        }
        if (otherSnippet != null)
        {
            result = result && getSnippet().equals(otherSnippet.getSnippet());
        }

        return result && propertyHelper.equals(otherSnippet.propertyHelper);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        // Try with the title first
        if (title != null && !title.equals(""))
        {
            return title.hashCode();
        }

        // Snippet?
        if (getSnippet() != null && !getSnippet().equals(""))
        {
            return getSnippet().hashCode();
        }

        return propertyHelper.hashCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    public Object clone() throws CloneNotSupportedException
    {
        RawDocumentSnippet obj = new RawDocumentSnippet(title, null);
        obj.propertyHelper = (PropertyHelper) propertyHelper.clone();
        return obj;
    }
}