

/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.dawidweiss.carrot.filter.stc.algorithm;


import java.util.List;


/**
 * Class representing a single document reference obtained from Google.
 */
public class DocReference
{
    /** Document url */
    String url;

    /** Document title */
    String title;

    /** List of sentences (List) consisting of words (Strings) */
    List snippet;

    /** Stemmed snippet */
    ArrayStemmedSnippet stemmedSnippet = null;

    /** Original snippet string. Needed for later presentation */
    String originalSnippet;

    /**
     * Nah, you can't instantiate empty doc reference
     */
    protected DocReference()
    {
    }

    /**
     * The url of this document
     */
    public String getURLAsString()
    {
        return url;
    }


    /**
     * The title of this document as determined by datasource
     */
    public String getTitle()
    {
        return title;
    }


    /**
     * The url of this document
     */
    public List getSnippet()
    {
        return snippet;
    }


    /**
     * Returns original snippet text
     */
    public String getUnprocessedSnippet()
    {
        return this.originalSnippet;
    }


    /**
     * Returns StemmedSnippet object, if any, or null. This DocReference object must be processed
     * with some other class in order to contain StemmedSnippet object.
     */
    public ArrayStemmedSnippet getStemmedSnippet()
    {
        return this.stemmedSnippet;
    }


    /**
     * Sets the value of StemmedSnippet for this object.
     */
    public void setStemmedSnippet(ArrayStemmedSnippet s)
    {
        stemmedSnippet = s;
    }

    /**
     * Legal constructor
     */
    public DocReference(String url, String title, List snippet, String originalSnippet)
    {
        this.url = url;
        this.title = title;
        this.snippet = snippet;
        this.originalSnippet = originalSnippet;
    }

    public String toString()
    {
        return "(" + url + " : \"" + this.title + "\" : \"" + this.snippet + "\")";
    }
}
