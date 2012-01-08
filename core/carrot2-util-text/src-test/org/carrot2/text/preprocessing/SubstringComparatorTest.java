
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2012, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.preprocessing;

import java.util.*;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * Test cases for {@link SubstringComparator}.
 */
public class SubstringComparatorTest
{
    /**
     * @see "http://issues.carrot2.org/browse/CARROT-778"
     */
    @Test
    public void testCarrot778()
    {
        Random rnd = new Random(0xdeadbeef);  

        int [] tokensWordIndex = new int [1000];
        int [] wordsStemIndex = new int [2];
        
        for (int i = 0; i < tokensWordIndex.length; i++)
            tokensWordIndex[i] = rnd.nextInt(wordsStemIndex.length);

        for (int i = 0; i < wordsStemIndex.length; i++)
            wordsStemIndex[i] = i;

        final int substrLength = 3;
        final int maxFrom = tokensWordIndex.length - substrLength;
        List<Substring> substrings = Lists.newArrayList();
        for (int i = 0; i < 1000; i++)
            substrings.add(new Substring(i, 
                i % maxFrom, (i + substrLength) % maxFrom, 1));

        Collections.sort(substrings, 
            new SubstringComparator(tokensWordIndex, wordsStemIndex));
    }

    @Test
    public void testComparatorContract()
    {
        int [] tokensWordIndex = new int [2];
        int [] wordsStemIndex = new int [1];
        Substring a = new Substring(0, 0, 1, 0);
        
        Assert.assertEquals(0, 
            new SubstringComparator(tokensWordIndex, wordsStemIndex).compare(a, a));
    }
}
