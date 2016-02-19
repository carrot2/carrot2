
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
public class CompleteLabelFilterTest extends LabelFilterTestBase
{
    @Override
    protected void initializeFilters(LabelFilterProcessor filterProcessor)
    {
        filterProcessor.stopWordLabelFilter.enabled = true;
        filterProcessor.completeLabelFilter.enabled = true;
        filterProcessor.completeLabelFilter.labelOverrideThreshold = 0.5;
    }

    @Test
    public void testEmpty()
    {
        final int [] expectedLabelsFeatureIndex = new int [] {};

        check(expectedLabelsFeatureIndex);
    }

    @Test
    public void testOnePhrase()
    {
        createDocuments("aa bb cc . aa bb cc", "aa bb cc . aa bb cc");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            5
        };

        check(expectedLabelsFeatureIndex, 0);
    }

    @Test
    public void testSubphrases()
    {
        createDocuments("aa bb cc . aa bb cc", "bb cc . bb cc");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            5
        };

        check(expectedLabelsFeatureIndex, 0);
    }

    @Test
    public void testNestedPhrases()
    {
        createDocuments("aa bb cc dd . aa bb cc dd", "aa bb dd . aa bb dd");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            9, 11
        };

        check(expectedLabelsFeatureIndex, 0);
    }

    @Test
    public void testFuzzyOverrideApplied()
    {
        createDocuments("aa bb cc . aa bb cc . aa bb cc . aa bb cc . aa bb cc dd . aa bb cc dd");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            9
        };

        double previousThreshold = labelFilterProcessor.completeLabelFilter.labelOverrideThreshold;
        labelFilterProcessor.completeLabelFilter.labelOverrideThreshold = 0.3;
        check(expectedLabelsFeatureIndex, 0);
        labelFilterProcessor.completeLabelFilter.labelOverrideThreshold = previousThreshold;
    }

    @Test
    public void testFuzzyOverrideNotApplied()
    {
        createDocuments("aa bb cc . aa bb cc . aa bb cc . aa bb cc . aa bb cc dd . aa bb cc dd");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            7, 9
        };

        check(expectedLabelsFeatureIndex, 0);
    }

    @Test
    public void testOverridingByFilteredOutPhrase()
    {
        createDocuments("stop aa bb stop . stop aa bb stop");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            3
        };

        check(expectedLabelsFeatureIndex, 0);
    }
}
