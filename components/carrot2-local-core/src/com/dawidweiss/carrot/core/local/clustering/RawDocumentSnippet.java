/*
 * RawDocumentSnippet.java Created on 2004-06-17
 */
package com.dawidweiss.carrot.core.local.clustering;


/**
 * @author stachoo
 */
public class RawDocumentSnippet extends RawDocumentBase
{
    /** Document title */
    private String title;

    /** Document snippet */
    private String snippet;

    /**
     * @param title
     * @param snippet
     */
    public RawDocumentSnippet(String title, String snippet)
    {
        this.title = title;
        this.snippet = snippet;
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
     * @see com.dawidweiss.carrot.core.local.clustering.RawDocument#getSnippet()
     */
    public String getSnippet()
    {
        return snippet;
    }
}