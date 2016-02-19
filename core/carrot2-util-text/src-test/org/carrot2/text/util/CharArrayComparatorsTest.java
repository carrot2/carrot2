
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

package org.carrot2.text.util;

import java.util.Arrays;

import org.carrot2.util.tests.CarrotTestCase;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link CharArrayComparators}. 
 */
public class CharArrayComparatorsTest extends CarrotTestCase
{
    @Test
    public void testNormalizingComparatorPL()
    {
        char [][] testWords = new char [] []
        {
            "\u0142an".toCharArray(),
            "demo".toCharArray(),
            "demos".toCharArray(),
            "DEMO".toCharArray(),
            "\u0141AN".toCharArray(),
            "Demos".toCharArray(),
            "demo".toCharArray(),
            "\u0141an".toCharArray(),
            "DEMOS".toCharArray()
        };

        char [][] expectedOrderedWords = new char [] []
        {
            "\u0142an".toCharArray(),
            "\u0141an".toCharArray(),
            "\u0141AN".toCharArray(),
            "demo".toCharArray(),
            "demo".toCharArray(),
            "DEMO".toCharArray(),
            "demos".toCharArray(),
            "Demos".toCharArray(),
            "DEMOS".toCharArray()
        };

        check(testWords, expectedOrderedWords);
    }

    @Test
    public void testNormalizingComparator()
    {
        char [][] testWords = new char [] []
        {
            "use".toCharArray(),
            "UAE".toCharArray(),
            "Use".toCharArray()
        };

        char [][] expectedOrderedWords = new char [] []
        {
            "UAE".toCharArray(),
            "use".toCharArray(),
            "Use".toCharArray()
        };

        check(testWords, expectedOrderedWords);
    }
    
    @Test
    public void testNullsAreEqual()
    {
        Assert.assertTrue(0 == CharArrayComparators.CASE_INSENSITIVE_CHAR_ARRAY_COMPARATOR.compare(null, null));
        Assert.assertTrue(0 == CharArrayComparators.FAST_CHAR_ARRAY_COMPARATOR.compare(null, null));
        Assert.assertTrue(0 == CharArrayComparators.NORMALIZING_CHAR_ARRAY_COMPARATOR.compare(null, null));
    }    

    private void check(char [][] testWords, char [][] expectedOrderedWords)
    {
        Arrays.sort(testWords,
            CharArrayComparators.NORMALIZING_CHAR_ARRAY_COMPARATOR);
        assertThat(testWords).isEqualTo(expectedOrderedWords);
    }
}
