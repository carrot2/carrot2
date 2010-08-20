
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
import org.junit.Test;

/**
 * Test cases for {@link StopListMarker}.
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

        check(expectedCommonTermFlag);
    }

    @Test
    public void testStopWords()
    {
        createDocuments("this you", "have are");

        final boolean [] expectedCommonTermFlag = new boolean []
        {
            true, true, true, true
        };

        check(expectedCommonTermFlag);
    }

    private void check(boolean [] expectedCommonTermFlag)
    {
        tokenizer.tokenize(context);
        caseNormalizer.normalize(context);
        languageModelStemmer.stem(context);
        stopListMarker.mark(context);

        boolean [] actual = new boolean [context.allWords.type.length];
        for (int i = 0; i < actual.length; i++)
        {
            actual[i] = ((context.allWords.type[i] & ITokenizer.TF_COMMON_WORD) != 0);
        }

        assertThat(actual).isEqualTo(expectedCommonTermFlag);
    }
}
