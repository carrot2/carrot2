package org.carrot2.text;

import java.util.*;

import com.google.common.collect.Maps;

/**
 * Maps an integer to each added unique token ({@link CharSequence}).
 */
public final class CharSequenceIntMap
{
    /**
     * Mutable character sequence (for token ID lookups).
     */
    private final MutableCharArray buffer = new MutableCharArray("");

    /**
     * A map of index codes and previously seen character sequences.
     */
    private HashMap<MutableCharArray, Integer> tokenImages = Maps.newHashMap();

    /**
     * Fetch an index for an existing or new {@link CharSequence}.
     */
    public int getIndex(CharSequence charSequence)
    {
        buffer.reset(charSequence);
        return getIndex(buffer);
    }

    /**
     * Fetch an index for an existing or new {@link MutableCharArray}. This method does
     * not create intermediate object if not necessary (as does {@link #get(CharSequence)}).
     */
    public int getIndex(MutableCharArray charSequence)
    {
        Integer code = tokenImages.get(charSequence);
        if (code == null)
        {
            code = tokenImages.size();
            tokenImages.put(new MutableCharArray(charSequence), code);
        }
        return code;
    }

    /**
     * Returns unique images of tokens at the moment of making the call.
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
     * @return Returns the current size of the unique images set.
     */
    public int getSize()
    {
        return tokenImages.size();
    }
}
