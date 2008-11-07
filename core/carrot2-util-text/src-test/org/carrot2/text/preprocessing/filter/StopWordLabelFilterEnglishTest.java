
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.preprocessing.filter;

import org.carrot2.text.linguistic.LanguageModelFactory;
import org.carrot2.text.linguistic.SnowballLanguageModelFactory;
import org.carrot2.text.preprocessing.LabelFilterProcessor;
import org.carrot2.text.preprocessing.LabelFilterTestBase;
import org.junit.Test;

/**
 * Test cases for {@link StopWordLabelFilter}.
 */
public class StopWordLabelFilterEnglishTest extends LabelFilterTestBase
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
        createDocuments("I . HAVE . data", "I . HAVE . data");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            wordIndices.get("data")
        };

        check(expectedLabelsFeatureIndex);
    }

    @Test
    public void testStopWordsInPhrases()
    {
        createDocuments("of data mining for", "of data mining for");

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
        snowballLanguageModelFactory.mergeStopwords = false;
        return snowballLanguageModelFactory;
    }
}
