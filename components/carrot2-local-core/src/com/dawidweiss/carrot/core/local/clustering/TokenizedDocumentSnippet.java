/*
 * TokenizedDocumentBase.java Created on 2004-06-15
 */
package com.dawidweiss.carrot.core.local.clustering;

import com.dawidweiss.carrot.core.local.linguistic.tokens.*;

/**
 * Represents a tokenized snippet of a document. Does <b>not </b> provide
 * document's content.
 * 
 * @author stachoo
 */
public class TokenizedDocumentSnippet extends TokenizedDocumentBase
{
    /** Thie snippet's title */
    private TokenSequence title;

    /** This snippets id */
    private String id;

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
    public TokenizedDocumentSnippet(String id, TokenSequence title,
        TokenSequence snippet, String url, float score)
    {
        this.id = id;
        this.title = title;
        this.score = score;
        propertyHelper.setProperty(PROPERTY_SNIPPET, snippet);
        propertyHelper.setProperty(PROPERTY_URL, url);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.util.tokenizer.TokenizedDocumentBase#getSnippet()
     */
    public TokenSequence getSnippet()
    {
        return (TokenSequence) propertyHelper.getProperty(PROPERTY_SNIPPET);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.clustering.TokenizedDocument#getId()
     */
    public Object getId()
    {
        return id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.clustering.TokenizedDocument#getUrl()
     */
    public String getUrl()
    {
        return (String) propertyHelper.getProperty(PROPERTY_URL);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.clustering.TokenizedDocument#getTitle()
     */
    public TokenSequence getTitle()
    {
        return title;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.clustering.TokenizedDocument#getScore()
     */
    public float getScore()
    {
        return score;
    }
}