package org.carrot2.text.preprocessing;

import static org.carrot2.util.test.Assertions.assertThat;
import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;

/**
 * Base class for {@link StemmingTask} tests.
 */
public class StemmerTestBase extends PreprocessingComponentTestBase
{
    /** Stemmer under tests */
    private LanguageModelStemmer languageModelStemmer;

    /** Other preprocessing components required for the test */
    private Tokenizer tokenizer;
    private CaseNormalizer caseNormalizer;

    @Before
    public void setUpPreprocessingComponents()
    {
        tokenizer = new Tokenizer();
        caseNormalizer = new CaseNormalizer();
        languageModelStemmer = new LanguageModelStemmer();
    }
    
    protected void check(char [][] expectedStemImages, int [] expectedStemTf,
        int [] expectedStemIndices, int [][] expectedStemTfByDocument)
    {
        tokenizer.tokenize(context);
        caseNormalizer.normalize(context);
        languageModelStemmer.stem(context);
        
        assertThat(context.allWords.stemIndex).as("allWords.stemIndices").isEqualTo(
            expectedStemIndices);
        assertThat(context.allStems.images).as("allStems.images").isEqualTo(
            expectedStemImages);
        assertThat(context.allStems.tf).as("allStems.tf").isEqualTo(expectedStemTf);
        assertThat(context.allStems.tfByDocument).as("allStems.tfByDocument").isEqualTo(
            expectedStemTfByDocument);
    }
}
