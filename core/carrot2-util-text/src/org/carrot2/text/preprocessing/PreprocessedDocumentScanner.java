
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

import org.carrot2.util.IntArrayPredicateIterator;

/**
 * Iterates over tokenized documents in {@link PreprocessingContext}.
 */
public class PreprocessedDocumentScanner
{
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
            PreprocessingContext.ON_DOCUMENT_SEPARATOR);

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
            PreprocessingContext.ON_FIELD_SEPARATOR);

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
            PreprocessingContext.ON_SENTENCE_SEPARATOR);

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
