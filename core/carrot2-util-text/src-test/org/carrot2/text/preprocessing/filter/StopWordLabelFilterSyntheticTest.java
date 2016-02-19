
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

package org.carrot2.text.preprocessing.filter;

import org.carrot2.text.preprocessing.LabelFilterProcessor;
import org.carrot2.text.preprocessing.LabelFilterTestBase;
import org.junit.Test;

/**
 * Test cases for {@link StopWordLabelFilter}.
 */
public class StopWordLabelFilterSyntheticTest extends LabelFilterTestBase
{
    @Override
    protected void initializeFilters(LabelFilterProcessor filterProcessor)
    {
        filterProcessor.stopWordLabelFilter.enabled = true;
    }

    @Test
    public void testEmpty()
    {
        final int [] expectedLabelsFeatureIndex = new int [] {};

        check(expectedLabelsFeatureIndex);
    }

    @Test
    public void testNonStopWords()
    {
        createDocuments("aa . aa", "bb . bb");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            0, 1
        };

        check(expectedLabelsFeatureIndex);
    }

    @Test
    public void testStopWords()
    {
        createDocuments("stop . stop", "bb . bb");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            wordIndices.get("bb")
        };

        check(expectedLabelsFeatureIndex);
    }

    @Test
    public void testNonStopPhrases()
    {
        createDocuments("aa aa . aa aa", "bb bb . bb bb");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            0, 1, 2, 3
        };

        check(expectedLabelsFeatureIndex, 2);
    }

    @Test
    public void testStopPhrases()
    {
        createDocuments("aa stop aa . aa stop aa",
            "stop bb . stop bb . bb stop . bb stop");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            0, 1, 7
        };

        check(expectedLabelsFeatureIndex, 2);
    }

    @Test
    public void testStopPhrasesWithStemmedWords()
    {
        createDocuments("aa1 . aa2 . aa1 . aa2",
            "stop aa1 aa2. stop aa1 aa2 . stop aa1 aa2");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            0, 3
        };

        check(expectedLabelsFeatureIndex, 1);
    }

    @Test
    public void testStemmedWords()
    {
        createDocuments("abc . abc . abc", "abd . abd . abe . abe");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            0
        };

        check(expectedLabelsFeatureIndex);
    }
}
