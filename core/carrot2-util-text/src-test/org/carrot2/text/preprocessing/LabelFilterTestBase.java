
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

package org.carrot2.text.preprocessing;

import static org.fest.assertions.Assertions.assertThat;

import org.carrot2.text.linguistic.LanguageModelFactory;
import org.carrot2.text.preprocessing.filter.CompleteLabelFilter;
import org.junit.Before;

/**
 * Test cases for {@link CompleteLabelFilter}.
 */
public class LabelFilterTestBase extends PreprocessingComponentTestBase
{
    /** Filter processor under tests */
    protected LabelFilterProcessor labelFilterProcessor;

    /** Other preprocessing components required for the test */
    private Tokenizer tokenizer;
    private CaseNormalizer caseNormalizer;
    private LanguageModelStemmer languageModelStemmer;
    private PhraseExtractor phraseExtractor;
    private StopListMarker stopListMarker;

    @Before
    public void setUpPreprocessingComponents()
    {
        tokenizer = new Tokenizer();
        caseNormalizer = new CaseNormalizer();
        languageModelStemmer = new LanguageModelStemmer();
        phraseExtractor = new PhraseExtractor();
        stopListMarker = new StopListMarker();
        labelFilterProcessor = new LabelFilterProcessor();

        // Disable all filters by default. Tests will enable the filters they need.
        labelFilterProcessor.minLengthLabelFilter.enabled = false;
        labelFilterProcessor.queryLabelFilter.enabled = false;
        labelFilterProcessor.numericLabelFilter.enabled = false;
        labelFilterProcessor.stopWordLabelFilter.enabled = false;
        labelFilterProcessor.completeLabelFilter.enabled = false;

        initializeFilters(labelFilterProcessor);
    }

    protected void initializeFilters(LabelFilterProcessor filterProcessor)
    {
    }

    protected void check(int [] expectedLabelsFeatureIndex)
    {
        runPreprocessing();

        assertThat(context.allLabels.featureIndex).as("allLabels.featureIndex")
            .isEqualTo(expectedLabelsFeatureIndex);
    }

    protected void runPreprocessing()
    {
        tokenizer.tokenize(context);
        caseNormalizer.normalize(context);
        languageModelStemmer.stem(context);
        phraseExtractor.extractPhrases(context);
        stopListMarker.mark(context);
        labelFilterProcessor.process(context);
    }

    @Override
    protected LanguageModelFactory createLanguageModelFactory()
    {
        return new TestLanguageModelFactory();
    }
}
