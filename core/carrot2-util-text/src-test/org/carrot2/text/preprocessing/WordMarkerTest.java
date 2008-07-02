package org.carrot2.text.preprocessing;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for stemming in {@link Preprocessor}.
 */
public class WordMarkerTest extends PreprocessingComponentTestBase
{
    /** Marker under tests */
    private StopListMarker stopListMarker;
    
    /** Other preprocessing components required for the test */
    private Tokenizer tokenizer;
    private CaseNormalizer caseNormalizer;
    private LanguageModelStemmer languageModelStemmer;

    @Before
    public void setUpPreprocessingComponents()
    {
        tokenizer = new Tokenizer();
        caseNormalizer = new CaseNormalizer();
        languageModelStemmer = new LanguageModelStemmer();
        stopListMarker = new StopListMarker();
    }
    
    @Test
    public void testNonStopWords()
    {
        createDocuments("data mining", "data mining");

        final boolean [] expectedCommonTermFlag = new boolean []
        {
            false, false
        };

        checkAsserts(expectedCommonTermFlag);
    }

    @Test
    public void testStopWords()
    {
        createDocuments("this you", "have are");

        final boolean [] expectedCommonTermFlag = new boolean []
        {
            true, true, true, true
        };

        checkAsserts(expectedCommonTermFlag);
    }

    private void checkAsserts(boolean [] expectedCommonTermFlag)
    {
        tokenizer.tokenize(context);
        caseNormalizer.normalize(context);
        languageModelStemmer.stem(context);
        stopListMarker.mark(context);
        
        assertThat(context.allWords.commonTermFlag).isEqualTo(expectedCommonTermFlag);
    }
}
