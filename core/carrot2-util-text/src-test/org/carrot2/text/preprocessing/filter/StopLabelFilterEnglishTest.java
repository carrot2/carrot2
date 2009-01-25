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

import org.carrot2.text.linguistic.ILanguageModelFactory;
import org.carrot2.text.linguistic.DefaultLanguageModelFactory;
import org.carrot2.text.preprocessing.LabelFilterProcessor;
import org.carrot2.text.preprocessing.LabelFilterTestBase;
import org.junit.Test;

/**
 * Test cases for {@link StopWordLabelFilter}.
 */
public class StopLabelFilterEnglishTest extends LabelFilterTestBase
{
    @Override
    protected void initializeFilters(LabelFilterProcessor filterProcessor)
    {
        filterProcessor.stopWordLabelFilter.enabled = true;
        filterProcessor.stopLabelFilter.enabled = true;
    }

    @Test
    public void testEmpty()
    {
        final int [] expectedLabelsFeatureIndex = new int [] {};

        check(expectedLabelsFeatureIndex);
    }

    @Test
    public void testNonStopLabels()
    {
        createDocuments("data . mining", "data . mining");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            0, 1
        };

        check(expectedLabelsFeatureIndex);
    }

    @Test
    public void testSingleWordStopLabels()
    {
        createDocuments("new . new . new", "data news . data news");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            wordIndices.get("data"), wordIndices.get("news"), 3
        };

        check(expectedLabelsFeatureIndex, 2);
    }

    @Test
    public void testPhraseStopLabels()
    {
        createDocuments("information on data", "information on data");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            wordIndices.get("data")
        };

        check(expectedLabelsFeatureIndex);
    }

    @Override
    protected ILanguageModelFactory createLanguageModelFactory()
    {
        final DefaultLanguageModelFactory snowballLanguageModelFactory = new DefaultLanguageModelFactory();
        snowballLanguageModelFactory.mergeResources = false;
        return snowballLanguageModelFactory;
    }
}
