
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

import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link SuffixSorter}.
 */
public class SuffixSorterTest extends PreprocessingComponentTestBase
{
    /** Suffix sorter under tests */
    private SuffixSorter suffixSorter;

    /** Other preprocessing components required for the test */
    private Tokenizer tokenizer;
    private CaseNormalizer caseNormalizer;

    @Before
    public void setUpPreprocessingComponents()
    {
        tokenizer = new Tokenizer();
        caseNormalizer = new CaseNormalizer();
        suffixSorter = new SuffixSorter();
    }

    @Test
    public void testEmpty()
    {
        // Do not add any documents to the rawDocuments list
        int [] expectedSuffixOrder = new int []
        {
            0
        };

        int [] expectedLcpArray = new int []
        {
            0
        };

        checkAsserts(expectedSuffixOrder, expectedLcpArray);
    }

    @Test
    public void testEmptySnippet()
    {
        createDocuments((String) null);

        int [] expectedSuffixOrder = new int []
        {
            0
        };

        int [] expectedLcpArray = new int []
        {
            0
        };

        checkAsserts(expectedSuffixOrder, expectedLcpArray);
    }

    @Test
    public void testEmptyBody()
    {
        createDocuments("a");

        int [] expectedSuffixOrder = new int []
        {
            0, 1
        };

        int [] expectedLcpArray = new int []
        {
            0, 0
        };

        checkAsserts(expectedSuffixOrder, expectedLcpArray);
    }

    @Test
    public void testEmptyTitle()
    {
        createDocuments((String) null, "a");

        int [] expectedSuffixOrder = new int []
        {
            0, 1
        };

        int [] expectedLcpArray = new int []
        {
            0, 0
        };

        checkAsserts(expectedSuffixOrder, expectedLcpArray);
    }

    @Test
    public void testOnePhrase()
    {
        createDocuments("a b", "a b");

        int [] expectedSuffixOrder = new int []
        {
            1, 4, 0, 3, 2, 5
        };

        int [] expectedLcpArray = new int []
        {
            0, 1, 0, 2, 0, 0
        };

        checkAsserts(expectedSuffixOrder, expectedLcpArray);
    }

    @Test
    public void testPunctuation()
    {
        createDocuments("a . b", "a . b");

        int [] expectedSuffixOrder = new int []
        {
            2, 6, 0, 4, 1, 3, 5, 7
        };

        int [] expectedLcpArray = new int []
        {
            0, 1, 0, 1, 0, 0, 0, 0
        };

        checkAsserts(expectedSuffixOrder, expectedLcpArray);
    }

    @Test
    public void testMoreTokens()
    {
        createDocuments("a b c d e  f g h i j  k l m n o  p q r", null);

        int [] expectedSuffixOrder = new int []
        {
            17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0, 18
        };

        int [] expectedLcpArray = new int []
        {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
        };

        checkAsserts(expectedSuffixOrder, expectedLcpArray);
    }

    private void checkAsserts(int [] expectedSuffixOrder, int [] expectedLcpArray)
    {
        tokenizer.tokenize(context);
        caseNormalizer.normalize(context);
        suffixSorter.suffixSort(context);

        assertThat(context.allTokens.suffixOrder).as("allTokens.suffixOrder").isEqualTo(
            expectedSuffixOrder);
        assertThat(context.allTokens.lcp).as("allTokens.lcp").isEqualTo(expectedLcpArray);
    }
}
