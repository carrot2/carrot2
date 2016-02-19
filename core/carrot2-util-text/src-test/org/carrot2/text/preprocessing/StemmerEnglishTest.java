
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

import org.carrot2.util.tests.CarrotTestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Language-independent test cases for {@link LanguageModelStemmer}.
 */
public class StemmerEnglishTest extends CarrotTestCase
{
    PreprocessingContextBuilder contextBuilder;

    // @formatter:off

    @Before
    public void prepareContextBuilder()
    {
        contextBuilder = new PreprocessingContextBuilder();
    }

    @Test
    public void testLowerCaseWords()
    {
        PreprocessingContextAssert a = contextBuilder
            .newDoc("data mining", "data mining")
            .buildContextAssert();

        a.constainsStem("data").withTf(2).withDocumentTf(0, 2).withFieldIndices(0, 1);
        a.constainsStem("mine").withTf(2).withDocumentTf(0, 2).withFieldIndices(0, 1);
        assertThat(a.context.allStems.image.length).isEqualTo(2);

        assertThat(a.tokens()).onProperty("stemImage")
            .containsExactly("data", "mine", null, 
                             "data", "mine", null);
    }

    @Test
    public void testUpperCaseWords()
    {
        PreprocessingContextAssert a = contextBuilder
            .newDoc("DATA MINING", "DATA MINING")
            .buildContextAssert();

        a.constainsStem("data").withTf(2).withDocumentTf(0, 2).withFieldIndices(0, 1);
        a.constainsStem("mine").withTf(2).withDocumentTf(0, 2).withFieldIndices(0, 1);
        assertThat(a.context.allStems.image.length).isEqualTo(2);

        assertThat(a.tokens()).onProperty("stemImage")
            .containsExactly("data", "mine", null, 
                             "data", "mine", null);
    }

    @Test
    public void testMixedCaseWords()
    {
        PreprocessingContextAssert a = contextBuilder
            .newDoc("DATA MINING Data Mining", "Data Mining Data Mining")
            .buildContextAssert();

        a.constainsStem("data").withTf(4).withDocumentTf(0, 4).withFieldIndices(0, 1);
        a.constainsStem("mine").withTf(4).withDocumentTf(0, 4).withFieldIndices(0, 1);
        assertThat(a.context.allStems.image.length).isEqualTo(2);

        assertThat(a.tokens()).onProperty("stemImage")
            .containsExactly("data", "mine", "data", "mine", null, 
                             "data", "mine", "data", "mine", null);
    }

    // @formatter:on
}
