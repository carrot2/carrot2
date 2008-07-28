package org.carrot2.text.preprocessing;

import static org.fest.assertions.Assertions.assertThat;

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

    @Test
    public void testSingleWordNotCapitalized()
    {
        createDocuments("test", "test");
        final String expectedLabel = "Test";

        checkFullPreprocessing(expectedLabel);
        checkWithoutPreprocessing(new char [] []
        {
            "test".toCharArray()
        }, new boolean []
        {
            false
        }, expectedLabel);
    }

    @Test
    public void testSingleWordCapitalized()
    {
        createDocuments("kMN", "kMN");
        final String expectedLabel = "kMN";

        checkFullPreprocessing(expectedLabel);
        checkWithoutPreprocessing(new char [] []
        {
            "kMN".toCharArray()
        }, new boolean []
        {
            false
        }, expectedLabel);
    }

    @Test
    public void testSingleStopWord()
    {
        createDocuments("for", "for");
        labelFilterProcessor.stopWordLabelFilter.enabled = false;
        final String expectedLabel = "For";

        checkFullPreprocessing(expectedLabel);
        checkWithoutPreprocessing(new char [] []
        {
            "for".toCharArray()
        }, new boolean []
        {
            true
        }, expectedLabel);
    }

    @Test
    public void testPhraseWithLowerCaseWords()
    {
        createDocuments("test phrase", "test phrase");
        final String expectedLabel = "Test Phrase";

        checkFullPreprocessing(expectedLabel);
        checkWithoutPreprocessing(new char [] []
        {
            "test".toCharArray(), "phrase".toCharArray()
        }, new boolean []
        {
            false, false
        }, expectedLabel);
    }

    @Test
    public void testPhraseWithStopWords()
    {
        createDocuments("food for dog", "food for dog");
        final String expectedLabel = "Food for Dog";

        checkFullPreprocessing(expectedLabel);
        checkWithoutPreprocessing(new char [] []
        {
            "food".toCharArray(), "for".toCharArray(), "dog".toCharArray()
        }, new boolean []
        {
            false, true, false
        }, expectedLabel);
    }

    @Test
    public void testPhraseWithCapitalizedWords()
    {
        createDocuments("iMac stuff", "iMac stuff");
        final String expectedLabel = "iMac Stuff";

        checkFullPreprocessing(expectedLabel);
        checkWithoutPreprocessing(new char [] []
        {
            "iMac".toCharArray(), "stuff".toCharArray()
        }, new boolean []
        {
            false, false
        }, expectedLabel);
    }

    private void checkWithoutPreprocessing(char [][] words, boolean [] stopWords,
        String expectedFormattedLabel)
    {
        assertThat(LabelFormatter.format(words, stopWords)).isEqualTo(
            expectedFormattedLabel);
    }

    private void checkFullPreprocessing(String... expectedFormattedLabels)
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
