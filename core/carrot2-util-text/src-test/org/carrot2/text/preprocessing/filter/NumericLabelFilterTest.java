
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
public class NumericLabelFilterTest extends LabelFilterTestBase
{
    @Override
    protected void initializeFilters(LabelFilterProcessor filterProcessor)
    {
        filterProcessor.numericLabelFilter.enabled = true;
    }

    @Test
    public void testEmpty()
    {
        final int [] expectedLabelsFeatureIndex = new int [] {};

        check(expectedLabelsFeatureIndex);
    }

    @Test
    public void testNonNumericWords()
    {
        createDocuments("aa . aa", "bb.com.pl . bb.com.pl");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            0, 1
        };

        check(expectedLabelsFeatureIndex);
    }

    @Test
    public void testNumericWords()
    {
        createDocuments("10,12 . 10,12", "bb . bb");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            wordIndices.get("bb")
        };

        check(expectedLabelsFeatureIndex);
    }

    @Test
    public void testPhraseStartingWithNumbers()
    {
        createDocuments("5 xx", "5 xx");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            wordIndices.get("xx")
        };

        check(expectedLabelsFeatureIndex);
    }

    @Test
    public void testPhraseStartingWithNonNumbers()
    {
        createDocuments("xx 5", "xx 5");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            1, 2
        };

        check(expectedLabelsFeatureIndex, 1);
    }
}
