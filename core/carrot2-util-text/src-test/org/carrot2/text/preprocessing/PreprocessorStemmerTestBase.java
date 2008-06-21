package org.carrot2.text.preprocessing;

import static org.carrot2.util.test.Assertions.assertThat;
import static org.fest.assertions.Assertions.assertThat;

/**
 * Base class for {@link StemmingTask} tests.
 */
public class PreprocessorStemmerTestBase extends PreprocessorTestBase
{
    protected void checkAsserts(char [][] expectedStemImages, int [] expectedStemTf,
        int [] expectedStemIndices, int [][] expectedStemTfByDocument)
    {
        final PreprocessingContext context = new PreprocessingContext();
        preprocessor.preprocess(context, PreprocessingTasks.TOKENIZE,
            PreprocessingTasks.CASE_NORMALIZE, PreprocessingTasks.STEMMING);

        assertThat(context.allWords.stemIndex).as("allWords.stemIndices").isEqualTo(
            expectedStemIndices);
        assertThat(context.allStems.images).as("allStems.images").isEqualTo(
            expectedStemImages);
        assertThat(context.allStems.tf).as("allStems.tf").isEqualTo(expectedStemTf);
        assertThat(context.allStems.tfByDocument).as("allStems.tfByDocument").isEqualTo(
            expectedStemTfByDocument);
    }
}
