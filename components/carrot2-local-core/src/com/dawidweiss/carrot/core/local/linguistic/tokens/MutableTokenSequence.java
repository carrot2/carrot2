/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.core.local.linguistic.tokens;

import java.util.*;

/**
 * A dynamically constructed token sequence.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class MutableTokenSequence implements TokenSequence
{
    /** Tokens */
    private List tokens;
    
    /** String image of this MutableTokenSequence */
    private String image;

    /**
     * Creates an empty MutableTokenSequence.
     */
    public MutableTokenSequence()
    {
        tokens = new ArrayList();
    }

    /**
     * Creates a MutableTokenSequence with one initial token
     * 
     * @param token
     */
    public MutableTokenSequence(Token token)
    {
        this();
        addToken(token);
    }

    /**
     * Creates a MutableTokenSequence with some initial tokens
     * 
     * @param tokens
     */
    public MutableTokenSequence(Token [] tokens)
    {
        this();
        addTokens(tokens);
    }

    /**
     * @param token
     */
    public void addToken(Token token)
    {
        tokens.add(token);
    }

    /**
     * @param tokenArray
     */
    public void addTokens(Token [] tokenArray)
    {
        tokens.addAll(Arrays.asList(tokenArray));
    }

    /**
     * @param index
     * @param token
     */
    public void setTokenAt(int index, Token token)
    {
        tokens.set(index, token);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.linguistic.tokens.TokenSequence#getLength()
     */
    public int getLength()
    {
        return tokens.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.linguistic.tokens.TokenSequence#getTokenAt(int)
     */
    public Token getTokenAt(int index)
    {
        return (Token) tokens.get(index);
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
        throw new RuntimeException("Not implemented yet");
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

        if (!(obj instanceof TokenSequence))
        {
            return false;
        }
        else
        {
            TokenSequence tokenSequence = (TokenSequence) obj;
            if (tokenSequence.getLength() != tokens.size())
            {
                return false;
            }
            else
            {
                for (int t = 0; t < tokens.size(); t++)
                {
                    if (!tokens.get(t).equals(tokenSequence.getTokenAt(t)))
                    {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        int hash = 0;

        // As hash code is supposed to be fast - we calculate it for at most 8
        // first tokens
        for (int t = 0; t < tokens.size() && t < 8; t++)
        {
            if (tokens.get(t) != null)
            {
                hash += tokens.get(t).hashCode();
            }
        }

        return hash;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        if (image == null)
        {
            StringBuffer stringBuffer = new StringBuffer();
            for (Iterator tokensIter = tokens.iterator(); tokensIter.hasNext();)
            {
                Token token = (Token) tokensIter.next();
                token.appendTo(stringBuffer);
                stringBuffer.append(" ");
            }
            
            image = stringBuffer.toString();
        }

        return image;
    }
}