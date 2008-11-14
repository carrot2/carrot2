
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core.clustering;

import org.carrot2.core.linguistic.tokens.TokenSequence;

/**
 * Represents a tokenized snippet of a document. Does <b>not </b> provide
 * document's content.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class TokenizedDocumentSnippet extends TokenizedDocumentBase
{
    /** Thie snippet's title */
    private TokenSequence title;

    /** This snippets id */
    private Object id;

    /** This snippets score */
    private float score;

    /**
     * Creates a new <code>TokenizedDocumentSnippet</code> with a
     * <code>null</code> id and url. The score is set to -1.
     * 
     * @param title
     * @param snippet
     */
    public TokenizedDocumentSnippet(TokenSequence title, TokenSequence snippet)
    {
        this(null, title, snippet, null, -1);
    }

    /**
     * Creates a new <code>TokenizedDocumentSnippet</code> with given id,
     * title, snippet, URL and score.
     * 
     * @param id
     * @param title
     * @param snippet
     * @param url
     * @param score
     */
    public TokenizedDocumentSnippet(Object id, TokenSequence title,
        TokenSequence snippet, String url, float score)
    {
        this.id = id;
        this.title = title;
        this.score = score;
        setProperty(PROPERTY_SNIPPET, snippet);
        setProperty(PROPERTY_URL, url);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.util.tokenizer.TokenizedDocumentBase#getSnippet()
     */
    public TokenSequence getSnippet()
    {
        return (TokenSequence) getProperty(PROPERTY_SNIPPET);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.clustering.TokenizedDocument#getId()
     */
    public Object getId()
    {
        return id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.clustering.TokenizedDocument#getUrl()
     */
    public String getUrl()
    {
        return (String) getProperty(PROPERTY_URL);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.clustering.TokenizedDocument#getTitle()
     */
    public TokenSequence getTitle()
    {
        return title;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.clustering.TokenizedDocument#getScore()
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
        String title = "";
        if (getTitle() != null)
        {
            title = getTitle().toString();
        }

        String snippet = "";
        if (getSnippet() != null)
        {
            snippet = getSnippet().toString();
        }
        
        return "[" + title + "] " + snippet;
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

        TokenizedDocumentSnippet otherSnippet = (TokenizedDocumentSnippet) obj;
        
        // Try the id first
        if (id != null && otherSnippet.id != null)
        {
            return id.equals(otherSnippet.id);
        }
        
        boolean result = true;
        if (title != null)
        {
            result = result && title.equals(otherSnippet.title);
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
        if (title != null && title.getLength() > 0)
        {
            return title.hashCode();
        }

        // Snippet?
        if (getSnippet() != null && getSnippet().getLength() > 0)
        {
            return getSnippet().hashCode();
        }

        return getPropertyHelper().hashCode();
    }
}