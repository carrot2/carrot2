
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

package org.carrot2.text.preprocessing;

import org.carrot2.text.linguistic.ILexicalDataFactory;
import org.carrot2.text.linguistic.IStemmerFactory;
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
        check(expectedLabelsFeatureIndex, -1);
    }

    protected void check(int [] expectedLabelsFeatureIndex, int expectedFirstPhraseIndex)
    {
        runPreprocessing();

        assertThat(context.allLabels.featureIndex).as("allLabels.featureIndex")
            .isEqualTo(expectedLabelsFeatureIndex);
        assertThat(context.allLabels.firstPhraseIndex).as("allLabels.firstPhraseIndex")
            .isEqualTo(expectedFirstPhraseIndex);
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
    protected ILexicalDataFactory createLexicalDataFactory()
    {
        return new TestLexicalDataFactory();
    }

    @Override
    protected IStemmerFactory createStemmerFactory()
    {
        return new TestStemmerFactory();
    }
}
