
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
 * {@link com.dawidweiss.carrot.core.local.linguistic.tokens.TokenSequence}and
 * adds to it support for storing/retrieving properties.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class ExtendedTokenSequence implements TokenSequence, PropertyProvider
{

    /** TokenSeqence delegate */
    private TokenSequence tokenSequence;

    /** This TokenSequence's properties */
    private PropertyHelper propertyHelper;

    /** The sequence's frequency across the whole collection */
    public static final String PROPERTY_TF = "tf";

    /**
     * For a generalized (i.e. stemmed) token sequence returns original phrases
     * as a list of {@link ExtendedTokenSequence}s.
     */
    public static final String PROPERTY_ORIGINAL_TOKEN_SEQUENCES = "originalTS";

    /**
     * For a generalized (i.e. stemmed) token sequence returns the original
     * phrase that is most frequent. When there is no one phrase that is most
     * frequent an arbitrary original phrase will be returned
     */
    public static final String PROPERTY_MOST_FREQUENT_ORIGINAL_TOKEN_SEQUENCE = "MForiginalTS";

    /** */
    public static final String PROPERTY_PSEUDO_TF = "ptf";

    /**
     * Creates an ExtendedTokenSequence wrapped around the provided
     * {@link TokenSequence}.
     * 
     * @param token
     */
    public ExtendedTokenSequence(TokenSequence tokenSequence)
    {
        this.tokenSequence = tokenSequence;

        this.propertyHelper = new PropertyHelper();
    }

    /**
     * Gets a value of a named property associated with this ExtendedToken.
     * 
     * @param propertyName
     * @return
     */
    public Object getProperty(String propertyName)
    {
        return propertyHelper.getProperty(propertyName);
    }

    /**
     * Sets a named property for this ExtendedToken.
     * 
     * @param propertyName
     * @param value
     * @return
     */
    public Object setProperty(String propertyName, Object value)
    {
        return propertyHelper.setProperty(propertyName, value);
    }

    /**
     * Returns a named <code>double</code> value associated with this Extended
     * Token. It is the responsibility of the programmer to assure that the
     * appropriate object casting succeeds.
     * 
     * @param propertName
     * @return
     */
    public double getDoubleProperty(String propertName, double defaultValue)
    {
        return propertyHelper.getDoubleProperty(propertName, defaultValue);
    }

    /**
     * Sets a named <code>double</code> value for this Extended token.
     * 
     * @param propertyName
     * @param value
     * @return
     */
    public Object setDoubleProperty(String propertyName, double value)
    {
        return propertyHelper.setDoubleProperty(propertyName, value);
    }

    /**
     * Returns a named <code>int</code> value associated with this Extended
     * Token. It is the responsibility of the programmer to assure that the
     * appropriate object casting succeeds.
     * 
     * @param propertName
     * @return
     */
    public int getIntProperty(String propertyName, int defaultValue)
    {
        return propertyHelper.getIntProperty(propertyName, defaultValue);
    }

    /**
     * Sets a named <code>double</code> value for this Extended token.
     * 
     * @param propertyName
     * @param value
     * @return
     */
    public Object setIntProperty(String propertyName, int value)
    {
        return propertyHelper.setIntProperty(propertyName, value);
    }

    /**
     * @return
     */
    public String getFullInfo()
    {
        return tokenSequence.toString() + " [" + propertyHelper.toString()
            + "]";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.linguistic.tokens.TokenSequence#getLength()
     */
    public int getLength()
    {
        return tokenSequence.getLength();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.linguistic.tokens.TokenSequence#getTokenAt(int)
     */
    public Token getTokenAt(int index)
    {
        return tokenSequence.getTokenAt(index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.linguistic.tokens.TokenSequence#copyTo(com.dawidweiss.carrot.core.local.linguistic.tokens.Token[],
     *      int, int, int)
     */
    public int copyTo(Token [] destination, int startAt,
        int destinationStartAt, int maxLength)
    {
        return tokenSequence.copyTo(destination, startAt, destinationStartAt,
            maxLength);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        if (propertyHelper
            .getProperty(PROPERTY_MOST_FREQUENT_ORIGINAL_TOKEN_SEQUENCE) != null)
        {
            return propertyHelper.getProperty(
                PROPERTY_MOST_FREQUENT_ORIGINAL_TOKEN_SEQUENCE).toString();
        }
        else
        {
            return tokenSequence.toString();
        }
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
        else
        {
            return tokenSequence
                .equals(((ExtendedTokenSequence) obj).tokenSequence) && propertyHelper
                .equals(((ExtendedTokenSequence) obj).propertyHelper);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return tokenSequence.hashCode() + propertyHelper.hashCode();
    }
}