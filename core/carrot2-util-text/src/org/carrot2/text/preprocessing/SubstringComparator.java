
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.preprocessing;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Compares {@link Substring}s in such a way as to put them into different buckets in an
 * inflection-insensitive way. The order of different variants of substrings within
 * buckets is arbitrary.
 */
class SubstringComparator implements Comparator<Substring>, Serializable
{
    private static final long serialVersionUID = 1L;
    
    private final int [] tokensWordIndex;
    private final int [] wordsStemIndex;

    /**
     * Creates a Comparator for substrings relating to the given word and stem indices.
     */
    public SubstringComparator(int [] tokensWordIndex, int [] wordsStemIndex)
    {
        this.tokensWordIndex = tokensWordIndex;
        this.wordsStemIndex = wordsStemIndex;
    }

    public int compare(Substring s1, Substring s2)
    {
        final int s1From = s1.from;
        final int s1To = s1.to;
        final int s2From = s2.from;
        final int s2To = s2.to;

        final int s2Length = s2To - s2From;
        final int s1Length = s1To - s1From;
        if (s1Length != s2Length)
        {
            return s1Length - s2Length;
        }

        assert s1Length == s2Length;
        for (int i = 0; i < s1Length; i++)
        {
            final int stemIndex1 = wordsStemIndex[tokensWordIndex[s1From + i]];
            final int stemIndex2 = wordsStemIndex[tokensWordIndex[s2From + i]];
            if (stemIndex1 != stemIndex2)
            {
                return stemIndex1 - stemIndex2;
            }
        }

        // If the substrings are equal in the inflection-insensitive way, return 0 (equivalence class).
        return 0;
    }
}
