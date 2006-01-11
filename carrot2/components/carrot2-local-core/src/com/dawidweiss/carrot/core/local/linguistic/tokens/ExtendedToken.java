
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

package com.dawidweiss.carrot.core.local.linguistic.tokens;

import com.stachoodev.util.common.*;

/**
 * A class that wraps around any
 * {@link com.dawidweiss.carrot.core.local.linguistic.tokens.Token}and adds to
 * it support for storing/retrieving properties.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class ExtendedToken implements Token, PropertyProvider
{
    /** Token delegate */
    private Token token;

    /** Properties of this token */
    private PropertyHelper propertyHelper;

    /** Document frequency */
    public static final String PROPERTY_DF = "df";

    /** Term frequency across the whole collection */
    public static final String PROPERTY_TF = "tf";

    /** Weighted term frequency across the whole collection */
    public static final String PROPERTY_WEIGHTED_TF = "wtf";

    /** Inverse document frequency factor */
    public static final String PROPERTY_IDF = "idf";

    /** The index of this token on the selected features list */
    public static final String PROPERTY_INDEX = "idx";

    /**
     * If present, for a generalized (i.e. stemmed) extended token returns all
     * original variants as a list of {@link ExtendedToken}s.
     */
    public static final String PROPERTY_ORIGINAL_TOKENS = "originalTokens";

    /** */
    public static final String PROPERTY_MOST_FREQUENT_ORIGINAL_TOKEN = "mfot";

    /**
     *  
     */
    public ExtendedToken()
    {
        this(null);
    }

    /**
     * Creates an ExtendedToken wrapped around the provided
     * {@link com.dawidweiss.carrot.core.local.linguistic.tokens.Token}.
     * 
     * @param token
     */
    public ExtendedToken(Token token)
    {
        this.token = token;

        // we don't expect ExtendedTokens not defining any properties, hence
        // no lazy initalization of the property container
        this.propertyHelper = new PropertyHelper();
    }

    /**
     * Returns the underlying {@link Token}.
     * 
     */
    public Token getToken()
    {
        return token;
    }

    /**
     * @param token
     */
    public void setToken(Token token)
    {
        this.token = token;
        propertyHelper.clear();
    }

    /**
     */
    public ExtendedTokenSequence asTokenSequence()
    {
        ExtendedTokenSequence extendedTokenSequence = asShallowTokenSequence(this);

        // Convert original tokens as well
        extendedTokenSequence
            .setProperty(
                ExtendedTokenSequence.PROPERTY_MOST_FREQUENT_ORIGINAL_TOKEN_SEQUENCE,
                asShallowTokenSequence((Token) getProperty(PROPERTY_MOST_FREQUENT_ORIGINAL_TOKEN)));

        return extendedTokenSequence;
    }

    /**
     * Does not copy original tokens onto original token sequences
     * 
     * @param extendedToken
     */
    private ExtendedTokenSequence asShallowTokenSequence(Token extendedToken)
    {
        ExtendedTokenSequence extendedTokenSequence = new ExtendedTokenSequence(
            new MutableTokenSequence(extendedToken));
        extendedTokenSequence.setProperty(ExtendedTokenSequence.PROPERTY_TF,
            getProperty(PROPERTY_WEIGHTED_TF));

        return extendedTokenSequence;
    }

    /**
     * Gets a value of a named property associated with this ExtendedToken.
     * 
     * @param propertyName
     */
    public Object getProperty(String propertyName)
    {
        return propertyHelper.getProperty(propertyName);
    }

    /**
     * Sets a named property for this ExtendedToken.
     */
    public Object setProperty(String propertyName, Object property)
    {
        return propertyHelper.setProperty(propertyName, property);
    }

    /**
     * Returns a named <code>double</code> value associated with this
     * ExtendedToken. It is the responsibility of the programmer to assure that
     * the appropriate object casting succeeds.
     */
    public double getDoubleProperty(String propertyName, double defaultValue)
    {
        return propertyHelper.getDoubleProperty(propertyName, defaultValue);
    }

    /**
     * Sets a named <code>double</code> value for this ExtendedToken.
     * 
     * @param propertyName
     * @param value
     */
    public Object setDoubleProperty(String propertyName, double value)
    {
        return propertyHelper.setDoubleProperty(propertyName, value);
    }

    /**
     * Returns a named <code>int</code> value associated with this
     * ExtendedToken. It is the responsibility of the programmer to assure that
     * the appropriate object casting succeeds.
     * 
     * @param propertyName
     */
    public int getIntProperty(String propertyName, int defaultValue)
    {
        return propertyHelper.getIntProperty(propertyName, defaultValue);
    }

    /**
     * Sets a named <code>int</code> value for this ExtendedToken.
     * 
     * @param propertyName
     * @param value
     */
    public Object setIntProperty(String propertyName, int value)
    {
        return propertyHelper.setIntProperty(propertyName, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.linguistic.tokens.Token#appendTo(java.lang.StringBuffer)
     */
    public void appendTo(StringBuffer buffer)
    {
        token.appendTo(buffer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        if (propertyHelper.getProperty(PROPERTY_MOST_FREQUENT_ORIGINAL_TOKEN) != null)
        {
            return propertyHelper.getProperty(
                PROPERTY_MOST_FREQUENT_ORIGINAL_TOKEN).toString();
        }
        else
        {
            return token.toString();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object arg)
    {
        if (arg == this)
        {
            return true;
        }

        if (arg == null)
        {
            return false;
        }

        if (arg.getClass() != getClass())
        {
            return false;
        }
        else
        {
            boolean c1 = token.equals(((ExtendedToken) arg).token);
            boolean c2 = true;
            if (propertyHelper != null)
            {
                c2 = propertyHelper
                    .equals(((ExtendedToken) arg).propertyHelper);
            }
            return c1 && c2;

        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return token.hashCode()
            + (propertyHelper != null ? propertyHelper.hashCode() : 0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.linguistic.tokens.Token#getImage()
     */
    public String getImage()
    {
        return token.getImage();
    }
}