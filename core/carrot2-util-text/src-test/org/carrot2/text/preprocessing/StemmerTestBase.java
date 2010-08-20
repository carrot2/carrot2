
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.preprocessing;

import static org.fest.assertions.Assertions.assertThat;

import org.carrot2.text.analysis.ITokenizer;
import org.junit.Before;

/**
 * Base class for {@link LanguageModelStemmer} tests.
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
        int [] expectedStemIndices, int [][] expectedStemTfByDocument,
        byte [][] expectedFieldIndices)
    {
        performProcessing();

        assertThat(context.allWords.stemIndex).as("allWords.stemIndices").isEqualTo(
            expectedStemIndices);
        assertThat(context.allStems.image).as("allStems.images").isEqualTo(
            expectedStemImages);
        assertThat(context.allStems.tf).as("allStems.tf").isEqualTo(expectedStemTf);
        assertThat(context.allStems.tfByDocument).as("allStems.tfByDocument").isEqualTo(
            expectedStemTfByDocument);
        assertThat(context.allStems.fieldIndices).as("allStems.fieldIndices").isEqualTo(
            CaseNormalizerTest.flattenToBits(expectedFieldIndices));
    }

    protected void check(String query, short [] expectedWordsFlag)
    {
        createPreprocessingContext(query);
        performProcessing();

        short [] cloned = new short [context.allWords.type.length];
        System.arraycopy(context.allWords.type, 0, cloned, 0, context.allWords.type.length);

        for (int i = 0; i < cloned.length; i++)
            cloned[i] &= ITokenizer.TF_QUERY_WORD;

        assertThat(cloned).as("allWords.flag")
            .isEqualTo(expectedWordsFlag);
    }

    private void performProcessing()
    {
        tokenizer.tokenize(context);
        caseNormalizer.normalize(context);
        languageModelStemmer.stem(context);
    }
}
