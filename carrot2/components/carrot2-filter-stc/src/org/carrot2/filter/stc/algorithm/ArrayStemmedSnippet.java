
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


import org.carrot2.filter.stc.suffixtree.SuffixableElement;


/**
 * An array representation of stemmed snippet's terms.
 */
public class ArrayStemmedSnippet
{
    /** Snippet terms as an array. Sentences are separated with null values */
    private final StemmedTerm [] snippet;

    /** Precalculated sentence boundaries */
    private final StemmedSentence [] sentences;

    /**
     * Nested class representing a sentence in this snippet.
     */
    public class StemmedSentence
        implements SuffixableElement
    {
        /** The index of the first word of a sentence */
        int start;

        /** The last word of this sentence */
        int end;

        /**
         * public constructor of a sentence
         */
        public StemmedSentence(int start, int end)
        {
            this.start = start;
            this.end = end;
        }

        /**
         * implementation of SuffixableElement interface
         */
        public Object get(int index)
        {
            if (index == (size() - 1))
            {
                return SuffixableElement.END_OF_SUFFIX;
            }

            return snippet[start + index];
        }


        /**
         * implementation of SuffixableElement interface
         */
        public int size()
        {
            return end - start + 2;
        }


        /**
         * Retuns the Snippet this sentence belongs to.
         */
        public ArrayStemmedSnippet getSnippet()
        {
            return ArrayStemmedSnippet.this;
        }


        /**
         * Returns string representation of this object
         */
        public String toString()
        {
            String p = "";

            for (int i = start; i <= end; i++)
            {
                p = p + snippet[i].toString();
            }

            return p;
        }


        /**
         * Chops off leading and ending stop-words. Be sure to check whether sentence length is
         * still more than 1 (otherwise the sentence doesn't exist and consists solely of EOS
         * marker).
         *
         * @return true if the sentence consisted solely of stop-words.
         */
        public boolean trimStopWords()
        {
            int newStart = start;
            int newEnd = end;

            while ((newStart <= newEnd) && snippet[newStart].isStopWord())
            {
                newStart++;
            }

            while ((newStart <= newEnd) && snippet[newEnd].isStopWord())
            {
                newEnd--;
            }

            start = newStart;
            end = newEnd;

            return start > end;
        }
    }

    /**
     * Public constructor.
     */
    public ArrayStemmedSnippet(StemmedTerm [] snippetTerms)
    {
        this.snippet = snippetTerms;

        int sentCount = 0;

        // calculate number of sentences
        for (int i = 0; i < snippetTerms.length; i++)
        {
            if (snippetTerms[i] == null)
            {
                sentCount++;
            }
        }

        // create sentence objects.
        sentences = new StemmedSentence[sentCount];

        int index = 0;
        int sent = 0;

        for (int i = 0; i < snippetTerms.length; i++)
        {
            if (snippetTerms[i] == null)
            {
                sentences[sent++] = new StemmedSentence(index, i - 1);
                index = i + 1;
            }
        }
    }

    /**
     * Returns the number of sentences in this snippet
     */
    public int size()
    {
        return sentences.length;
    }


    /**
     * Returns n-th sentence of this snippet
     */
    public SuffixableElement getSentence(int n)
    {
        return sentences[n];
    }


    /**
     * Trims stop words from the edges of sentences.
     */
    public void trimEdgeStopWords()
    {
        int j = 0;

        for (int i = 0; i < sentences.length; i++)
        {
            sentences[j] = sentences[i];

            if (!sentences[i].trimStopWords())
            {
                j++;
            }
        }

        if (j != sentences.length)
        {
            // resize array
            StemmedSentence [] newSentences = new StemmedSentence[j];

            System.arraycopy(sentences, 0, newSentences, 0, j);
        }
    }


    /**
     * String representation of this object
     */
    public String toString()
    {
        StringBuffer p = new StringBuffer();

        p.append("[");

        for (int i = 0; i < snippet.length; i++)
        {
            p.append((snippet[i] == null) ? ";"
                                          : snippet[i].toString()
            );
        }

        return p.append("]").toString();
    }
}
