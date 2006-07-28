
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

package org.carrot2.core.clustering;


/**
 * An implementation of the
 * {@link org.carrot2.core.clustering.RawDocument}interface
 * suitable for storing document references, so called snippets, returned by a
 * search engine. Does <b>not </b> provide document content.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class RawDocumentSnippet extends RawDocumentBase implements Cloneable
{
    /** Document id */
    private Object id;

    /** Document score */
    private float score;

    /**
     * Creates a new RawDocumentSnippet with given title and snippet.
     * 
     * @param title
     * @param snippet
     */
    public RawDocumentSnippet(String title, String snippet)
    {
        this(null, title, snippet, null, -1);
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

    /**
     * Creates a new RawDocumentSnippet with given id, title, snippet url and
     * score.
     * 
     * @param id
     * @param title
     * @param snippet
     * @param url
     * @param score
     */
    public RawDocumentSnippet(Object id, String title, String snippet, 
            String url, float score)
    {
        super(url, title, snippet);
        this.id = id;
        this.score = score;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.clustering.RawDocument#getId()
     */
    public Object getId()
    {
        return id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.clustering.RawDocument#getUrl()
     */
    public String getUrl()
    {
        return (String) getProperty(PROPERTY_URL);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.clustering.RawDocument#getScore()
     */
    public float getScore()
    {
        return score;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "[" + getTitle() + "] " + getSnippet();
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

        // Try the id first
        if (id != null && otherSnippet.id != null)
        {
            return id.equals(otherSnippet.id);
        }

        boolean result = true;
        if (getTitle() != null)
        {
            result = result && getTitle().equals(otherSnippet.getTitle());
        }
        if (getSnippet() != null)
        {
            result = result && getSnippet().equals(otherSnippet.getSnippet());
        }

        return result && super.getPropertyHelper().equals(otherSnippet.getPropertyHelper());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        // Try the id first
        if (id != null)
        {
            return id.hashCode();
        }
        
        // Try with the title first
        if (getTitle() != null && !getTitle().equals(""))
        {
            return getTitle().hashCode();
        }

        // Snippet?
        if (getSnippet() != null && !getSnippet().equals(""))
        {
            return getSnippet().hashCode();
        }

        return super.getPropertyHelper().hashCode();
    }

    /*
     * (non-Javadoc)
     */
    public Object clone() throws CloneNotSupportedException
    {
        RawDocumentSnippet obj = new RawDocumentSnippet(id, getTitle(), null, null, score);
        obj.clonePropertiesFrom(this);
        return obj;
    }
}