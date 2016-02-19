
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

import org.carrot2.core.LanguageCode;
import org.carrot2.text.linguistic.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link LabelFormatter}.
 */
public class LabelFormatterTest extends PreprocessingComponentTestBase
{
    /** Label formatter under tests */
    private LabelFormatter labelFormatter;

    /** Other preprocessing components required for the test */
    private Tokenizer tokenizer;
    private CaseNormalizer caseNormalizer;
    private LanguageModelStemmer languageModelStemmer;
    private PhraseExtractor phraseExtractor;
    private StopListMarker stopListMarker;
    private LabelFilterProcessor labelFilterProcessor;

    @Before
    public void setUpPreprocessingComponents()
    {
        tokenizer = new Tokenizer();
        caseNormalizer = new CaseNormalizer();
        languageModelStemmer = new LanguageModelStemmer();
        phraseExtractor = new PhraseExtractor();
        stopListMarker = new StopListMarker();
        labelFilterProcessor = new LabelFilterProcessor();
        labelFormatter = new LabelFormatter();
    }

    @Override
    protected ILexicalDataFactory createLexicalDataFactory()
    {
        final ILexicalDataFactory factory = super.createLexicalDataFactory();
        ((DefaultLexicalDataFactory) factory).mergeResources = false;
        return factory;
    }

    @Test
    public void testSingleWordNotCapitalized()
    {
        createDocuments("test", "test");
        final String expectedLabel = "Test";

        checkFullPreprocessing(LanguageCode.ENGLISH, expectedLabel);
        checkWithoutPreprocessing(new char [] []
        {
            "test".toCharArray()
        }, new boolean []
        {
            false
        }, expectedLabel, true);
    }

    @Test
    public void testSingleWordCapitalized()
    {
        createDocuments("kMN", "kMN");
        final String expectedLabel = "kMN";

        checkFullPreprocessing(LanguageCode.ENGLISH, expectedLabel);
        checkWithoutPreprocessing(new char [] []
        {
            "kMN".toCharArray()
        }, new boolean []
        {
            false
        }, expectedLabel, true);
    }

    @Test
    public void testSingleStopWord()
    {
        createDocuments("for", "for");
        labelFilterProcessor.stopWordLabelFilter.enabled = false;
        final String expectedLabel = "For";

        checkFullPreprocessing(LanguageCode.ENGLISH, expectedLabel);
        checkWithoutPreprocessing(new char [] []
        {
            "for".toCharArray()
        }, new boolean []
        {
            true
        }, expectedLabel, true);
    }

    @Test
    public void testPhraseWithLowerCaseWords()
    {
        createDocuments("test phrase", "test phrase");
        final String expectedLabel = "Test Phrase";

        checkFullPreprocessing(LanguageCode.ENGLISH, expectedLabel);
        checkWithoutPreprocessing(new char [] []
        {
            "test".toCharArray(), "phrase".toCharArray()
        }, new boolean []
        {
            false, false
        }, expectedLabel, true);
    }

    @Test
    public void testPhraseWithStopWords()
    {
        createDocuments("food for fish", "food for fish");
        final String expectedLabel = "Food for Fish";

        checkFullPreprocessing(LanguageCode.ENGLISH, expectedLabel);
        checkWithoutPreprocessing(new char [] []
        {
            "food".toCharArray(), "for".toCharArray(), "fish".toCharArray()
        }, new boolean []
        {
            false, true, false
        }, expectedLabel, true);
    }

    @Test
    public void testPhraseWithoutStopWords()
    {
        createDocuments("Jaguar car", "Jaguar car");
        final String expectedLabel = "Jaguar Car";

        checkFullPreprocessing(LanguageCode.ENGLISH, expectedLabel);
        checkWithoutPreprocessing(new char [] []
        {
            "Jaguar".toCharArray(), "Car".toCharArray()
        }, new boolean []
        {
            false, true, false
        }, expectedLabel, true);
    }

    @Test
    public void testPhraseWithCapitalizedWords()
    {
        createDocuments("iMac stuff", "iMac stuff");
        final String expectedLabel = "iMac Stuff";

        checkFullPreprocessing(LanguageCode.ENGLISH, expectedLabel);
        checkWithoutPreprocessing(new char [] []
        {
            "iMac".toCharArray(), "stuff".toCharArray()
        }, new boolean []
        {
            false, false
        }, expectedLabel, true);
    }

    @Test
    public void testChinesePhrases()
    {
        createDocuments("东亚货币贬值", "东亚货币贬值");
        final String expectedLabel = "东亚货币贬值";

        checkFullPreprocessing(LanguageCode.ENGLISH, expectedLabel);
        checkWithoutPreprocessing(new char [] []
        {
            "东亚货币贬值".toCharArray()
        }, new boolean []
        {
            false, false
        }, expectedLabel, false);
    }

    private void checkWithoutPreprocessing(char [][] words, boolean [] stopWords,
        String expectedFormattedLabel, boolean joinWithSpace)
    {
        assertThat(LabelFormatter.format(words, stopWords, joinWithSpace)).isEqualTo(
            expectedFormattedLabel);
    }

    private void checkFullPreprocessing(LanguageCode language,
        String... expectedFormattedLabels)
    {
        tokenizer.tokenize(context);
        caseNormalizer.normalize(context);
        languageModelStemmer.stem(context);
        phraseExtractor.extractPhrases(context);
        stopListMarker.mark(context);
        labelFilterProcessor.process(context);

        final int [] labelsFeatureIndex = context.allLabels.featureIndex;
        assertThat(labelsFeatureIndex.length).as("featureIndex.length").isEqualTo(
            expectedFormattedLabels.length);
        for (int i = 0; i < labelsFeatureIndex.length; i++)
        {
            assertThat(labelFormatter.format(context, labelsFeatureIndex[i])).as(
                "featureIndex[" + i + "]").isEqualTo(expectedFormattedLabels[i]);
        }
    }
}
