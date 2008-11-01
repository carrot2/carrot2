package org.carrot2.text.preprocessing.filter;

import org.carrot2.text.preprocessing.*;
import org.junit.Test;

/**
 * Test cases for {@link StopWordLabelFilter}.
 */
public class MinLengthLabelFilterTest extends LabelFilterTestBase
{
    @Override
    protected void initializeFilters(LabelFilterProcessor filterProcessor)
    {
        filterProcessor.minLengthLabelFilter.enabled = true;
    }

    @Test
    public void testEmpty()
    {
        final int [] expectedLabelsFeatureIndex = new int [] {};

        check(expectedLabelsFeatureIndex);
    }

    @Test
    public void testTooShortWords()
    {
        createDocuments("aa . aa", "b . b");

        final int [] expectedLabelsFeatureIndex = new int [] {};

        check(expectedLabelsFeatureIndex);
    }

    @Test
    public void testLongerWords()
    {
        createDocuments("abc . abc", "abcd . abcd");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            0, 1
        };

        check(expectedLabelsFeatureIndex);
    }

    @Test
    public void testShortPhrases()
    {
        createDocuments("a a . a a", "b b . b b");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            2, 3
        };

        check(expectedLabelsFeatureIndex);
    }
}
