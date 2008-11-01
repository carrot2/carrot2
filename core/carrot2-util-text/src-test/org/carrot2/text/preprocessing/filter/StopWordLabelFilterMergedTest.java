package org.carrot2.text.preprocessing.filter;

import org.carrot2.text.linguistic.LanguageModelFactory;
import org.carrot2.text.linguistic.SnowballLanguageModelFactory;
import org.carrot2.text.preprocessing.*;
import org.junit.Test;

/**
 * Test cases for {@link StopWordLabelFilter} with stop word list merging.
 */
public class StopWordLabelFilterMergedTest extends LabelFilterTestBase
{
    @Override
    protected void initializeFilters(LabelFilterProcessor filterProcessor)
    {
        filterProcessor.completeLabelFilter.enabled = true;
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
        createDocuments("data . mining", "data . mining");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            0, 1
        };

        check(expectedLabelsFeatureIndex);
    }

    @Test
    public void testStopWords()
    {
        createDocuments("I . dog . data", "I . dog . data . y . y . el . el . der . der");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            wordIndices.get("data")
        };

        check(expectedLabelsFeatureIndex);
    }

    @Test
    public void testStopWordsInPhrases()
    {
        createDocuments("y data mining der", "y data mining der");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            4
        };

        check(expectedLabelsFeatureIndex);
    }

    @Override
    protected LanguageModelFactory createLanguageModelFactory()
    {
        final SnowballLanguageModelFactory snowballLanguageModelFactory = new SnowballLanguageModelFactory();
        snowballLanguageModelFactory.mergeStopwords = true;
        snowballLanguageModelFactory.reloadStopwords = true;
        return snowballLanguageModelFactory;
    }
}
