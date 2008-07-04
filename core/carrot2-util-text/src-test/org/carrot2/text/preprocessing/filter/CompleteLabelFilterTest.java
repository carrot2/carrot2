package org.carrot2.text.preprocessing.filter;

import org.carrot2.text.preprocessing.*;
import org.junit.Test;

/**
 * Test cases for {@link StopWordLabelFilter}.
 */
public class CompleteLabelFilterTest extends LabelFilterTestBase
{
    @Override
    protected void initializeFilters(LabelFilterProcessor filterProcessor)
    {
        filterProcessor.completeLabelFilter.enabled = true;
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

        check(expectedLabelsFeatureIndex);
    }

    @Test
    public void testSubphrases()
    {
        createDocuments("aa bb cc . aa bb cc", "bb cc . bb cc");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            5
        };

        check(expectedLabelsFeatureIndex);
    }

    @Test
    public void testNestedPhrases()
    {
        createDocuments("aa bb cc dd . aa bb cc dd", "aa bb dd . aa bb dd");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            9, 11
        };

        check(expectedLabelsFeatureIndex);
    }

    @Test
    public void testFuzzyOverrideApplied()
    {
        createDocuments("aa bb cc . aa bb cc . aa bb cc . aa bb cc . aa bb cc dd . aa bb cc dd");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            9
        };

        double previousCutoff = labelFilterProcessor.completeLabelFilter.labelOverrideCutoff;
        labelFilterProcessor.completeLabelFilter.labelOverrideCutoff = 0.3;
        check(expectedLabelsFeatureIndex);
        labelFilterProcessor.completeLabelFilter.labelOverrideCutoff = previousCutoff;
    }

    @Test
    public void testFuzzyOverrideNotApplied()
    {
        createDocuments("aa bb cc . aa bb cc . aa bb cc . aa bb cc . aa bb cc dd . aa bb cc dd");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            7, 9
        };

        check(expectedLabelsFeatureIndex);
    }
}
