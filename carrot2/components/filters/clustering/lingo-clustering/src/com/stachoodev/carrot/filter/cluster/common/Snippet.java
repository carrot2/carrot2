

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.stachoodev.carrot.filter.cluster.common;


/**
 * Represents a document to be clustered.
 */
public class Snippet
{
    /** This snippet's title */
    private String title;

    /** This snippet's body text */
    private String body;

    /** Stores the concatenated text of the document */
    private String text;

    /** Document identifier */
    private String id;

    /** Document language */
    private String language;

    /**
     * Creates a new document.
     *
     * @param id the identifier of the new document
     * @param text text of the new document
     */
    public Snippet(String id, String title, String body)
    {
        this(id, title, body, MultilingualClusteringContext.UNIDENTIFIED_LANGUAGE_NAME);
    }


    /**
     * Creates a new document.
     *
     * @param id the identifier of the new document
     * @param text text of the new document
     */
    public Snippet(String id, String title, String body, String language)
    {
        if (title == null)
        {
            title = "";
        }

        if (body == null)
        {
            body = "";
        }

        this.id = id;
        this.title = title;
        this.body = body;
        this.language = language;
    }

    /**
     * Returns the identifier of this document.
     *
     * @return String the identifier of this document
     */
    public String getId()
    {
        return id;
    }


    /**
     * Returns text of this document.
     *
     * @return String text of this document
     */
    public String getText()
    {
        if (text == null)
        {
            text = (title.equals("") ? ""
                                     : (title + " ")) + "." + (body.equals("") ? ""
                                                                               : (" " + body));
        }

        return text;
    }


    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object s)
    {
        if (!(s instanceof Snippet))
        {
            return false;
        }

        if (!((Snippet) s).getId().equals(id))
        {
            return false;
        }

        if (!((Snippet) s).getText().equals(text))
        {
            return false;
        }

        return true;
    }


    /**
     * @return String
     */
    public String getBody()
    {
        return body;
    }


    /**
     * @return String
     */
    public String getTitle()
    {
        return title;
    }


    /**
     * Returns document language.
     *
     * @return
     */
    public String getLanguage()
    {
        return language;
    }


    /**
     * Sets document language.
     *
     * @param string document language
     */
    public void setLanguage(String string)
    {
        language = string;
    }


    public String toString()
    {
        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append(id);
        stringBuffer.append(" (");
        stringBuffer.append(language);
        stringBuffer.append(") ");
        stringBuffer.append(title);
        stringBuffer.append("\n");
        stringBuffer.append(body);
        stringBuffer.append("\n");

        return stringBuffer.toString();
    }
}
