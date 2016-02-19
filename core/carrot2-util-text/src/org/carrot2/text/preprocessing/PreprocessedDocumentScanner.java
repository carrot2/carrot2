
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

import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.util.IntArrayPredicateIterator;

import com.carrotsearch.hppc.predicates.ShortPredicate;

/**
 * Iterates over tokenized documents in {@link PreprocessingContext}.
 */
public class PreprocessedDocumentScanner
{
    /** Predicate for splitting on document separator. */
    public static final ShortPredicate ON_DOCUMENT_SEPARATOR = 
        equalTo(ITokenizer.TF_SEPARATOR_DOCUMENT);

    /** Predicate for splitting on field separator. */
    public static final ShortPredicate ON_FIELD_SEPARATOR = 
        equalTo(ITokenizer.TF_SEPARATOR_FIELD);

    /** Predicate for splitting on sentence separator. */
    public static final ShortPredicate ON_SENTENCE_SEPARATOR = new ShortPredicate()
    {
        public boolean apply(short tokenType)
        {
            return (tokenType & ITokenizer.TF_SEPARATOR_SENTENCE) != 0;
        }
    };

    /** 
     * Return a new {@link ShortPredicate} returning <code>true</code>
     * if the argument equals a given value. 
     */
    public static final ShortPredicate equalTo(final short t)
    {
        return new ShortPredicate() {
            public boolean apply(short value)
            {
                return value == t; 
            }
        };
    }

    /**
     * Iterate over all documents, fields and sentences in {@link PreprocessingContext#allTokens}.
     */
    public final void iterate(PreprocessingContext context)
    {
        /*
         * Recursively iterate through documents, fields and sentences. This can be
         * implemented a bit faster (without iterators), but I guess the overhead here is
         * minimal anyway.
         */
        final IntArrayPredicateIterator docIterator = new IntArrayPredicateIterator(
            context.allTokens.type, 0, context.allTokens.type.length - 1,
            ON_DOCUMENT_SEPARATOR);

        while (docIterator.hasNext())
        {
            final int docStart = docIterator.next();
            final int docLength = docIterator.getLength();

            document(context, docStart, docLength);
        }
    }

    /**
     * Invoked for each document. Splits further into fields.
     */
    protected void document(PreprocessingContext context, int start, int length)
    {
        final IntArrayPredicateIterator fieldIterator = new IntArrayPredicateIterator(
            context.allTokens.type, start, length,
            ON_FIELD_SEPARATOR);

        while (fieldIterator.hasNext())
        {
            final int fieldStart = fieldIterator.next();
            final int fieldLength = fieldIterator.getLength();

            field(context, fieldStart, fieldLength);
        }
    }

    /**
     * Invoked for each document's field. Splits further into sentences.
     */
    protected void field(PreprocessingContext context, int start, int length)
    {
        final IntArrayPredicateIterator sentenceIterator = new IntArrayPredicateIterator(
            context.allTokens.type, start, length,
            ON_SENTENCE_SEPARATOR);

        while (sentenceIterator.hasNext())
        {
            final int sentenceStart = sentenceIterator.next();
            final int sentenceLength = sentenceIterator.getLength();

            sentence(context, sentenceStart, sentenceLength);
        }
    }

    /**
     * Invoked for each document's sentence.
     */
    protected void sentence(PreprocessingContext context, int start, int length)
    {

    }
}
