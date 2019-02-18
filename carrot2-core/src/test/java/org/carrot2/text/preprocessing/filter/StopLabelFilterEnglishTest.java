
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.preprocessing.filter;

import org.assertj.core.api.Assertions;
import org.carrot2.clustering.TestDocument;
import org.carrot2.language.EnglishLanguageComponentsFactory;
import org.carrot2.language.LanguageComponents;
import org.carrot2.language.TestsLanguageComponentsFactoryVariant2;
import org.carrot2.text.preprocessing.LabelFilterProcessor;
import org.carrot2.text.preprocessing.LabelFilterTestBase;
import org.carrot2.text.preprocessing.PreprocessingContextAssert;
import org.junit.Test;

/**
 * Test cases for {@link StopWordLabelFilter}.
 */
public class StopLabelFilterEnglishTest extends LabelFilterTestBase
{
    @Override
    protected void initializeFilters(LabelFilterProcessor filterProcessor)
    {
        filterProcessor.stopWordLabelFilter.get().enabled.set(true);
        filterProcessor.stopLabelFilter.get().enabled.set(true);
    }

    @Test
    public void testEmpty()
    {
        PreprocessingContextAssert a = preprocess();
        Assertions.assertThat(a.labelImages())
            .isEmpty();
    }

    @Test
    public void testNonStopLabels()
    {
        PreprocessingContextAssert a = preprocess(new TestDocument("coal . mining", "coal . mining"));
        Assertions.assertThat(a.labelImages())
            .containsOnly("coal", "mining");
    }

    @Test
    public void testSingleWordStopLabels()
    {
        PreprocessingContextAssert a = preprocess(new TestDocument("new . new . new", "coal news . coal news"));
        Assertions.assertThat(a.labelImages())
            .containsOnly("coal", "news", "coal news");
    }

    @Test
    public void testPhraseStopLabels()
    {
        PreprocessingContextAssert a = preprocess(new TestDocument("information on coal", "information on coal"));
        Assertions.assertThat(a.labelImages())
            .containsOnly("coal");
    }

    @Override
    protected PreprocessingContextAssert preprocess(TestDocument... docs) {
        return super.preprocess(null, LanguageComponents.get(EnglishLanguageComponentsFactory.NAME), docs);
    }
}
