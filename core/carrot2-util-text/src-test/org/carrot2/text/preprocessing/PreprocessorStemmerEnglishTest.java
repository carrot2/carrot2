package org.carrot2.text.preprocessing;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

/**
 * Test cases for stemming in {@link Preprocessor}.
 */
public class PreprocessorStemmerEnglishTest extends PreprocessorTestBase
{
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
        final PreprocessingContext context = new PreprocessingContext();
        preprocessor.preprocess(context, PreprocessingTasks.TOKENIZE,
            PreprocessingTasks.CASE_NORMALIZE, PreprocessingTasks.STEMMING,
            PreprocessingTasks.MARK_TOKENS_STOPLIST);

        assertThat(context.allWords.commonTermFlag).isEqualTo(expectedCommonTermFlag);
    }
}
