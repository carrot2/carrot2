
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core.linguistic.tokens;

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
    private final ArrayList tokens;

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

    public void addToken(Token token)
    {
        tokens.add(token);
    }

    public void addTokens(Token [] tokenArray)
    {
        tokens.addAll(Arrays.asList(tokenArray));
    }

    public void setTokenAt(int index, Token token)
    {
        tokens.set(index, token);
    }

    public int getLength()
    {
        return tokens.size();
    }

    public Token getTokenAt(int index)
    {
        return (Token) tokens.get(index);
    }

    public int copyTo(Token [] destination, int startAt, int destinationStartAt, int maxLength)
    {
        if (destinationStartAt < 0 || destinationStartAt >= destination.length) {
            throw new IllegalArgumentException("Destination index out of bounds: "
                    + destinationStartAt);
        }
        if (startAt < 0 || startAt >= this.tokens.size()) {
            throw new IllegalArgumentException("Start at index out of bounds: " 
                    + startAt);
        }

        final int howmuch = Math.min(maxLength, Math.min(this.tokens.size() - startAt, destination.length - destinationStartAt));
        int indexFrom = startAt;
        int indexTo = destinationStartAt;
        for (int i = howmuch; i > 0; i--) {
            destination[indexTo] = (Token) this.tokens.get(indexFrom);
            indexTo++;
            indexFrom++;
        }

        return howmuch; 
    }

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