

/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.dawidweiss.carrot.filter.stc.algorithm;


import java.util.*;


/**
 * Class which applies ImmediateStemmer to DocReferences. E.g. it can process snippets on the fly.
 */
public class ImmediateReferenceStemmer
{
    /**
     * This hashmap holds the words which have already been stemmed and have been assigned their
     * stemming-class number.
     */
    protected HashMap words;

    /** ImmediateStemmer object which is used for stemming. */
    protected ImmediateStemmer stemmer;

    /**
     * Hide empty constructor
     */
    protected ImmediateReferenceStemmer()
    {
    }


    /**
     * Public constructor requires ImmediateStemmer interface object to be given.
     */
    public ImmediateReferenceStemmer(ImmediateStemmer stemmer)
    {
        words = new HashMap(499);

        if (stemmer == null)
        {
            throw new java.lang.NullPointerException("Stemmer cannot be null");
        }

        this.stemmer = stemmer;
    }

    /**
     * Processes a single snippet.
     */
    public void process(DocReference reference)
    {
        if (stemmer == null)
        {
            throw new java.lang.NullPointerException("Stemmer cannot be null");
        }

        int snippetWordsNumber = 0;

        // count words and sentences, so that we know how big array we need to store
        // stemmed data.
        List snippetSentences = reference.getSnippet();

        for (Iterator sentenceIterator = snippetSentences.iterator(); sentenceIterator.hasNext();)
        {
            List sentence = (List) sentenceIterator.next();

            snippetWordsNumber += sentence.size();

            // make room for special class denoting the end of the sentence.
            snippetWordsNumber++;
        }

        StemmedTerm [] stemmed = new StemmedTerm[snippetWordsNumber];
        int stemmedIndex = 0;

        // look up words in existing stemming classes or create a new class and assign
        // its code to that word.
        for (Iterator sentenceIterator = snippetSentences.iterator(); sentenceIterator.hasNext();)
        {
            List sentence = (List) sentenceIterator.next();

            for (Iterator wordIterator = sentence.iterator(); wordIterator.hasNext();)
            {
                String word = (String) wordIterator.next();
                String original = word;

                word = word.trim().toLowerCase();

                // if word contains any non-letter characters, don't stem it.
                for (int i = 0; i <= word.length(); i++)
                {
                    if (i == word.length())
                    {
                        // STEM WORD
                        word = stemmer.stemWord(word);
                    }
                    else if (Character.isLetter(word.charAt(i)) == false)
                    {
                        break;
                    }
                }

                // find StemmedTerm class.
                StemmedTerm t = (StemmedTerm) words.get(original);

                // not found? Add it to all classes
                if (t == null)
                {
                    t = new SingleFormStemmedTerm(original, word);

                    words.put(original, t);
                }

                stemmed[stemmedIndex++] = t;
            }

            // mark end-of-sequence
            stemmed[stemmedIndex++] = null;
        }

        // add fully stemmed snippet to DocReference
        reference.setStemmedSnippet(new ArrayStemmedSnippet(stemmed));
    }

    static class SingleFormStemmedTerm
        implements StemmedTerm, Comparable
    {
        /** original word */
        String word;

        /** Stemmed form */
        String stemmed;

        /** A flag indicating whether this term is on stop-list */
        boolean stopword = false;

        /**
         * Indicates whether this term exists in the stop-list
         */
        public boolean isStopWord()
        {
            if (word.length() < 2)
            {
                return true;
            }

            return stopword;
        }


        /**
         * Sets the stop-word flag to true or false. The result obtained from isStopWord must be
         * consistent with this method.
         */
        public void setStopWord(boolean value)
        {
            stopword = value;
        }

        /**
         * Public constructor.
         */
        public SingleFormStemmedTerm(String word, String stemmed)
        {
            this.word = word;
            this.stemmed = stemmed;
        }

        /**
         * Overrides hashCode() method of Object class
         */
        public int hashCode()
        {
            return stemmed.hashCode();
        }


        /**
         * Overrides equals() method of Object class
         */
        public boolean equals(Object parm1)
        {
            if (parm1 instanceof SingleFormStemmedTerm)
            {
                // possibility of optimization.
                return stemmed.equals(((SingleFormStemmedTerm) parm1).stemmed);
            }
            else
            {
                return false;
            }
        }


        /**
         * Implementation of Comparable interface
         */
        public int compareTo(Object p)
        {
            return stemmed.compareTo(((SingleFormStemmedTerm) p).stemmed);
        }


        /**
         * Returns string representation of this object
         */
        public String toString()
        {
            return "{" + (this.stopword ? "!"
                                        : "") + this.word + "->" + stemmed + "}";
        }


        /**
         * Implementation of StemmedTerm interface
         */
        public String getTerm()
        {
            return this.word;
        }


        /**
         * Implementation of StemmedTerm interface.
         */
        public String getStemmed()
        {
            return this.stemmed;
        }
    }
}
