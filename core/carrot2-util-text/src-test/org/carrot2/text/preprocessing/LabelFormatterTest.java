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
        check("Test");
    }

    @Test
    public void testSingleWordCapitalized()
    {
        createDocuments("kMN", "kMN");
        check("kMN");
    }

    @Test
    public void testSingleStopWord()
    {
        createDocuments("for", "for");
        labelFilterProcessor.stopWordLabelFilter.enabled = false;
        check("For");
    }

    @Test
    public void testPhraseWithLowerCaseWords()
    {
        createDocuments("test phrase", "test phrase");
        check("Test Phrase");
    }

    @Test
    public void testPhraseWithStopWords()
    {
        createDocuments("food for dog", "food for dog");
        check("Food for Dog");
    }

    @Test
    public void testPhraseWithCapitalizedWords()
    {
        createDocuments("iMac stuff", "iMac stuff");
        check("iMac Stuff");
    }

    private void check(String... expectedFormattedLabels)
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
