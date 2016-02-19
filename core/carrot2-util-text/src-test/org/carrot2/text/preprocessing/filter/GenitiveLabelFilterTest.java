
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
 * Test cases for {@link GenitiveLabelFilter}.
 */
public class GenitiveLabelFilterTest extends LabelFilterTestBase
{
    @Override
    protected void initializeFilters(LabelFilterProcessor filterProcessor)
    {
        filterProcessor.genitiveLabelFilter.enabled = true;
    }

    @Test
    public void testEmpty()
    {
        final int [] expectedLabelsFeatureIndex = new int [] {};

        check(expectedLabelsFeatureIndex);
    }

    @Test
    public void testNoGenitiveWords()
    {
        createDocuments("abc . abc", "abcd . abcd");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            0, 1
        };

        check(expectedLabelsFeatureIndex);
    }

    @Test
    public void testGenitiveWords()
    {
        createDocuments("abcs' . abcs'", "abcd`s . abcd`s");

        final int [] expectedLabelsFeatureIndex = new int [] {};

        check(expectedLabelsFeatureIndex);
    }

    @Test
    public void testNoGenitiveEndingPhrases()
    {
        createDocuments("country's minister'll . country's minister'll");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            wordIndices.get("minister'll"), 2
        };

        check(expectedLabelsFeatureIndex, 1);
    }

    @Test
    public void testGenitiveEndingPhrases()
    {
        createDocuments("country minister`s . country's minister`s");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            wordIndices.get("country")
        };

        check(expectedLabelsFeatureIndex);
    }
}
