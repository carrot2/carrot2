
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

package org.carrot2.text.linguistic;

import org.carrot2.core.LanguageCode;
import org.carrot2.util.tests.CarrotTestCase;
import org.junit.Test;

/**
 * 
 */
public class LanguageModelTest extends CarrotTestCase
{
    /**
     * {@link LanguageModel} should return {@link LanguageModel}s that return the
     * same stemmer and tokenizer instance for reuse within the same thread (we assume no
     * two streams are tokenized at the same time using the same factory).
     */
    @Test
    public void testLanguageModelReturnsCachedStemmer()
    {
        final DefaultStemmerFactory stemmerFactory = new DefaultStemmerFactory();
        final DefaultTokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
        final DefaultLexicalDataFactory lexicalDataFactory = new DefaultLexicalDataFactory();

        final LanguageModel model1 = LanguageModel.create(LanguageCode.ENGLISH,
            stemmerFactory, tokenizerFactory, lexicalDataFactory);
        assertSame(model1.getStemmer(), model1.getStemmer());

        final LanguageModel model2 = LanguageModel.create(LanguageCode.ENGLISH,
            stemmerFactory, tokenizerFactory, lexicalDataFactory);
        assertSame(model2.getStemmer(), model2.getStemmer());

        assertNotSame(model1.getStemmer(), model2.getStemmer());
    }

    /**
     * {@link LanguageModel} should return {@link LanguageModel}s that return the
     * same stemmer and tokenizer instance for reuse within the same thread (we assume no
     * two streams are tokenized at the same time using the same factory).
     */
    @Test
    public void testLanguageModelReturnsCachedTokenizer()
    {
        final DefaultStemmerFactory stemmerFactory = new DefaultStemmerFactory();
        final DefaultTokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
        final DefaultLexicalDataFactory lexicalDataFactory = new DefaultLexicalDataFactory();

        final LanguageModel model1 = LanguageModel.create(LanguageCode.ENGLISH,
            stemmerFactory, tokenizerFactory, lexicalDataFactory);
        assertSame(model1.getTokenizer(), model1.getTokenizer());

        final LanguageModel model2 = LanguageModel.create(LanguageCode.ENGLISH,
            stemmerFactory, tokenizerFactory, lexicalDataFactory);
        assertSame(model2.getStemmer(), model2.getStemmer());

        assertNotSame(model1.getTokenizer(), model2.getTokenizer());
    }
}
