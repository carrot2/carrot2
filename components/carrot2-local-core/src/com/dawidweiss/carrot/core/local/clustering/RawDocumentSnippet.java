/*
 * RawDocumentSnippet.java Created on 2004-06-17
 */
package com.dawidweiss.carrot.core.local.clustering;

import com.stachoodev.util.common.*;

/**
 * @author stachoo
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
        if (title != null)
        {
            return title.hashCode();
        }

        // Snippet?
        if (getSnippet() != null)
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