package org.carrot2.text.analysis;

import java.util.*;

import org.apache.lucene.analysis.Token;
import org.carrot2.text.MutableCharArray;

import com.google.common.collect.*;

/**
 * Builds an array of {@link Token} images, where each unique token is integer-coded and
 * its image is stored only once.
 */
public final class TokenArrayBuilder
{
    /**
     * Current token's image (for token ID lookups).
     */
    private final MutableCharArray currentToken = new MutableCharArray("");

    /**
     * A map of hash codes and previously seen token images.
     */
    private HashMap<MutableCharArray, Integer> tokenImages = Maps.newHashMap();

    /**
     * An array of unique token images, first token is at position <code>1</code> to
     * allow special treatment of 0-index token.
     */
    private int [] tokens;

    /**
     * Last saved index in {@link #tokens}.
     */
    private int currentIndex = 0;

    /**
     * 
     */
    public void add(Token token)
    {
        final int tokenLength = token.termLength();
        final char [] tokenBuffer = token.termBuffer();

        // Check for identical tokens, remove redundant tokens.
        currentToken.reset(tokenBuffer, 0, tokenLength);

        // Token code.
        Integer tokenCode = tokenImages.get(currentToken);
        if (tokenCode == null)
        {
            tokenCode = tokenImages.size();

            final char [] buffer = new char [tokenLength];
            System.arraycopy(tokenBuffer, 0, buffer, 0, tokenLength);

            final MutableCharArray newToken = new MutableCharArray(buffer, 0, tokenLength);
            tokenImages.put(newToken, tokenCode);
        }

        ensureCapacity(currentIndex + 1);
        tokens[currentIndex] = tokenCode;
        currentIndex++;
    }

    /**
     * Returns the array of token indices.
     */
    public int [] getTokens()
    {
        return tokens;
    }

    /**
     * Returns unique images of tokens.
     */
    public MutableCharArray [] getTokenImages()
    {
        final MutableCharArray [] result = new MutableCharArray [tokenImages.size()];

        for (final Map.Entry<MutableCharArray, Integer> entry : tokenImages.entrySet())
        {
            result[entry.getValue()] = entry.getKey();
        }

        return result;
    }

    /**
     * Ensure {@link #tokens} can hold index <code>minCapacity</code>.
     */
    private void ensureCapacity(int minCapacity)
    {
        if (tokens == null)
        {
            tokens = new int [minCapacity + 1];
            return;
        }

        final int oldCapacity = tokens.length;
        if (minCapacity > oldCapacity)
        {
            final int oldData[] = tokens;
            final int newCapacity = Math.max(minCapacity, (oldCapacity * 3) / 2 + 1);

            tokens = new int [newCapacity];
            System.arraycopy(oldData, 0, tokens, 0, Math.min(oldData.length, tokens.length));
        }
    }
}
