/*
 * TokenStem.java Created on 2004-06-18
 */
package com.stachoodev.carrot.filter.lingo.model;

import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
import com.dawidweiss.carrot.util.tokenizer.parser.*;

/**
 * A token whose image is some <b>stem </b> of another token. The original word
 * is <b>not </b> stored.
 * 
 * @author stachoo
 */
public class TokenStem extends StringTypedToken implements TypedToken
{
    /**
     * Creates a <code>TokenStem</code> based on a {@link TypedToken}. If the
     * input <code>typedToken</code> is an instance of {@link StemmedToken},
     * the image of this token will be set to the input token's stem. Otherwise,
     * the image of this token will be set to the image of the
     * {@link TypedToken}. In both cases the type of this token will be copied
     * from the input token.
     * 
     * @param typedToken
     */
    public TokenStem(TypedToken typedToken)
    {
        String stem;
        if (typedToken instanceof StemmedToken)
        {
            if ((stem = ((StemmedToken) typedToken).getStem()) == null)
            {
                stem = typedToken.toString();
            }
        }
        else
        {
            stem = typedToken.toString();
        }
        assign(stem, typedToken.getType());
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
            return toString().equals(arg.toString());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return toString().hashCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return getImage();
    }
}