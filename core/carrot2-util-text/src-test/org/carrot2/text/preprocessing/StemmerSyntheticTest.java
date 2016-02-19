
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

import static org.carrot2.text.analysis.ITokenizer.*;

import org.carrot2.util.tests.CarrotTestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Language-independent test cases for {@link LanguageModelStemmer}.
 */
public class StemmerSyntheticTest extends CarrotTestCase
{
    PreprocessingContextBuilder contextBuilder;

    // @formatter:off

    @Before
    public void prepareContextBuilder()
    {
        contextBuilder = new PreprocessingContextBuilder()
            .withStemmerFactory(new TestStemmerFactory())
            .withLexicalDataFactory(new TestLexicalDataFactory());
    }

    @Test
    public void testEmpty()
    {
        assertThat(contextBuilder.buildContext().allStems.image.length).isEqualTo(0);
    }

    @Test
    public void testSingleStems()
    {
        PreprocessingContextAssert a = contextBuilder
            .newDoc("abc", "bcd")
            .buildContextAssert();
           
        a.constainsStem("a").withTf(1).withDocumentTf(0, 1).withFieldIndices(0);
        a.constainsStem("b").withTf(1).withDocumentTf(0, 1).withFieldIndices(1);
        assertThat(a.context.allStems.image.length).isEqualTo(2);

        // Account for field-separator markers (nulls) below.
        assertThat(a.tokens()).onProperty("stemImage")
            .containsExactly("a", null, "b", null);
    }

    @Test
    public void testFrequentSingleStems()
    {
        PreprocessingContextAssert a = contextBuilder
            .newDoc("abc abc", "bcd bcd bcd")
            .buildContextAssert();

        a.constainsStem("a").withTf(2).withDocumentTf(0, 2).withFieldIndices(0);
        a.constainsStem("b").withTf(3).withDocumentTf(0, 3).withFieldIndices(1);
        assertThat(a.context.allStems.image.length).isEqualTo(2);

        // Account for field-separator markers (nulls) below.
        assertThat(a.tokens()).onProperty("stemImage")
            .containsExactly("a", "a", null, "b", "b", "b", null);
    }

    @Test
    public void testOriginalFrequencyAggregation()
    {
        PreprocessingContextAssert a = contextBuilder
            .newDoc("abc acd bcd", "ade bof")
            .buildContextAssert();

        a.constainsStem("a").withTf(3).withDocumentTf(0, 3).withFieldIndices(0, 1);
        a.constainsStem("b").withTf(2).withDocumentTf(0, 2).withFieldIndices(0, 1);
        assertThat(a.context.allStems.image.length).isEqualTo(2);
        
        // Account for field-separator markers (nulls) below.
        assertThat(a.tokens()).onProperty("stemImage")
            .containsExactly("a", "a", "b", null, "a", "b", null);
    }

    @Test
    public void testNullStems()
    {
        PreprocessingContextAssert a = contextBuilder
            .newDoc("aa ab", "aa bc")
            .buildContextAssert();

        a.constainsStem("aa").withTf(2).withDocumentTf(0, 2).withFieldIndices(0, 1);
        a.constainsStem("ab").withTf(1).withDocumentTf(0, 1).withFieldIndices(0);
        a.constainsStem("bc").withTf(1).withDocumentTf(0, 1).withFieldIndices(1);
        assertThat(a.context.allStems.image.length).isEqualTo(3);

        // Account for field-separator markers (nulls) below.
        assertThat(a.tokens()).onProperty("stemImage")
            .containsExactly("aa", "ab", null, "aa", "bc", null);
    }

    @Test
    public void testWordTfByDocumentAggregation()
    {
        PreprocessingContextAssert a = contextBuilder
            .newDoc("abc acd ade")
            .newDoc("ade", "bcd bof")
            .newDoc(null, "bcd")
            .newDoc("ade", "bof")
            .buildContextAssert();

        a.constainsStem("a").withTf(5)
            .withExactDocumentTfs(new int [][] {{0, 3}, {1, 1}, {3, 1}})
            .withFieldIndices(0);
        a.constainsStem("b").withTf(4)
            .withExactDocumentTfs(new int [][] {{1, 2}, {2, 1}, {3, 1}})
            .withFieldIndices(1);
        assertThat(a.context.allStems.image.length).isEqualTo(2);

        // Account for field-separator markers (nulls) below.
        assertThat(a.tokens()).onProperty("stemImage")
            .containsExactly("a", "a", "a", null, 
                             "a", null, "b", "b", null,
                             "b", null,
                             "a", null, "b", null);
    }

    @Test
    public void testAllQueryWords()
    {
        PreprocessingContextAssert a = contextBuilder
            .newDoc("q1 q2", "q3")
            .withQuery("q1 q2 q3")
            .buildContextAssert();

        assertThat(a.tokens()).onProperty("wordType")
            .containsExactly(TF_QUERY_WORD | TT_TERM, TF_QUERY_WORD | TT_TERM, null,
                             TF_QUERY_WORD | TT_TERM, null);
    }

    @Test
    public void testSomeQueryWords()
    {
        PreprocessingContextAssert a = contextBuilder
            .newDoc("test q2", "aa q1")
            .withQuery("q1 q2 q3")
            .buildContextAssert();

        assertThat(a.tokens()).onProperty("wordType")
            .containsExactly(TT_TERM, TF_QUERY_WORD | TT_TERM, null, 
                             TT_TERM, TF_QUERY_WORD | TT_TERM, null);
    }
    
    @Test
    public void testNoQueryWords()
    {
        PreprocessingContextAssert a = contextBuilder
            .newDoc("q2", "aa q1")
            .withQuery("q3")
            .buildContextAssert();

        assertThat(a.tokens()).onProperty("wordType")
            .containsExactly(TT_TERM, null, 
                             TT_TERM, TT_TERM, null);
    }

    @Test
    public void testBlankQuery()
    {
        PreprocessingContextAssert a = contextBuilder
            .newDoc("q2", "aa q1")
            .withQuery("")
            .buildContextAssert();

        assertThat(a.tokens()).onProperty("wordType")
            .containsExactly(TT_TERM, null, 
                             TT_TERM, TT_TERM, null);
    }

    @Test
    public void testNullQuery()
    {
        PreprocessingContextAssert a = contextBuilder
            .newDoc("q2", "aa q1")
            .withQuery(null)
            .buildContextAssert();

        assertThat(a.tokens()).onProperty("wordType")
            .containsExactly(TT_TERM, null, 
                             TT_TERM, TT_TERM, null);
    }

    @Test
    public void testDifferentStemsInQuery()
    {
        PreprocessingContextAssert a = contextBuilder
            .newDoc("que01 que02", "test word")
            .withQuery("que04")
            .buildContextAssert();

        assertThat(a.tokens()).onProperty("wordType")
            .containsExactly(TT_TERM | TF_QUERY_WORD, TT_TERM | TF_QUERY_WORD, null, 
                             TT_TERM, TT_TERM, null);
    }

    // @formatter:on
}
