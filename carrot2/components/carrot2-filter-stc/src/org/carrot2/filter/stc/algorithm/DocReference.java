
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.stc.algorithm;


import java.util.*;

import org.carrot2.core.clustering.TokenizedDocument;
import org.carrot2.core.linguistic.tokens.*;


/**
 * Class representing a single document reference.
 */
public final class DocReference
{
    /** Document url */
    private String url;

    /** Stemmed snippet */
    private final ArrayStemmedSnippet stemmedSnippet;

    public DocReference(TokenizedDocument document)
    {
        this.url = document.getUrl();
        final ArrayList snippet = new ArrayList();

        if (document.getTitle() != null) {
            snippet.addAll(splitIntoSentences(document.getTitle()));
        }
        if (document.getSnippet() != null) {
            snippet.addAll(splitIntoSentences(document.getSnippet()));
        }

        this.stemmedSnippet = process(snippet);
    }

    /**
     * Splits a token sequence into a {@link List} of {@link List}s (sentences) of
     * {@link String} objects (words). 
     */
    private static List splitIntoSentences(final TokenSequence tokens) {
        final ArrayList sentences = new ArrayList(10);
        final ArrayList currentSentence = new ArrayList();
        currentSentence.ensureCapacity(10);

        final int maxTokenIndex = tokens.getLength();
        for (int i = 0; i < maxTokenIndex; i++) {
            final TypedToken token = (TypedToken) tokens.getTokenAt(i);
            final short tokenType = token.getType();
            if ((tokenType & TypedToken.TOKEN_FLAG_SENTENCE_DELIM) != 0) {
                if (currentSentence.size() > 0) {
                    sentences.add(new ArrayList(currentSentence));
                    currentSentence.clear();
                }
            } else {
                // Skip punctuation?
                // if ((tokenType & TypedToken.TOKEN_TYPE_PUNCTUATION) == 0)
                currentSentence.add(token);
            }
        }
        return sentences;
    }    

    /**
     * Returns StemmedSnippet object, if any, or null. This DocReference object must be processed
     * with some other class in order to contain StemmedSnippet object.
     */
    public ArrayStemmedSnippet getStemmedSnippet()
    {
        return this.stemmedSnippet;
    }

    public String toString()
    {
        return "(" + url + "\")";
    }
    
    /**
     * Processes a single {@link DocReference} into an array of
     * {@link StemmedTerm}s.
     */
    private ArrayStemmedSnippet process(final ArrayList snippet)
    {
        int snippetWordsNumber = 0;

        // count words and sentences, so that we know how big array we need to store
        // stemmed data.
        for (Iterator sentenceIterator = snippet.iterator(); sentenceIterator.hasNext();)
        {
            final List sentence = (List) sentenceIterator.next();
            snippetWordsNumber += sentence.size();
            // make room for special class denoting the end of the sentence.
            snippetWordsNumber++;
        }

        final StemmedTerm [] stemmed = new StemmedTerm[snippetWordsNumber];

        // look up words in existing stemming classes or create a new class and assign
        // its code to that word.
        int stemmedIndex = 0;
        for (Iterator sentenceIterator = snippet.iterator(); sentenceIterator.hasNext();)
        {
            final List sentence = (List) sentenceIterator.next();

            for (Iterator wordIterator = sentence.iterator(); wordIterator.hasNext();)
            {
                final Token token = (Token) wordIterator.next();
                final StemmedTerm t = new StemmedTerm(token);
                stemmed[stemmedIndex++] = t;
            }

            // mark end-of-sequence
            stemmed[stemmedIndex++] = null;
        }

        // add fully stemmed snippet to DocReference
        return new ArrayStemmedSnippet(stemmed);
    }
}
