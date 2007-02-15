/*
 * Copyright (c) 2004 Poznan Supercomputing and Networking Center
 * 10 Noskowskiego Street, Poznan, Wielkopolska 61-704, Poland
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Poznan Supercomputing and Networking Center ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into
 * with PSNC.
 */

package org.carrot2.webapp.serializers;

import java.io.*;
import java.util.*;

import org.carrot2.core.linguistic.*;
import org.carrot2.core.linguistic.tokens.*;


/**
 * Generates identifiers for words. Such identifiers are required for
 * highlighting of cluster label words in snippets. We need a single point of
 * word id generation because it has to be performed twice (once in the
 * documents request and one more time in the clusters request), and the results
 * must be consistent.
 * 
 * @author Stanislaw Osinski
 */
public class TextMarker
{
    /** For keeping track of emitted word ids */
    private int currentWordId;
    private Map wordStems;

    /** For tokenization */
    LanguageTokenizer tokenizer;
    private Token[] tokens;
    private int[] startPositions;

    /** For stemming */
    Stemmer stemmer;
    Set stopwords;

    static class StemInfo {
        public String id;
        public int frequency;
        
        public StemInfo(String id, int frequency)
        {
            super();
            this.id = id;
            this.frequency = frequency;
        }
    }
    

    TextMarker(LanguageTokenizer tokenizer, Stemmer stemmer, Set stopwords)
    {
        this.tokenizer = tokenizer;
        this.stemmer = stemmer;
        this.stopwords = stopwords;

        this.wordStems = new HashMap();
        this.tokens = new Token[64];
        this.startPositions = new int[64];
    }


    /**
     * Tokenizes the input and assigns ids to words.
     * 
     * @param input
     */
    public void tokenize(char[] text)
    {
        tokenize(text, null);
    }


    /**
     * Tokenizes the input, assigns ids to words and notifies the listener of
     * the related events.
     * 
     * @param input
     * @param listener
     */
    public void tokenize(char[] text, TextMarkerListener listener)
    {
        tokenizer.restartTokenizationOn(new CharArrayReader(text));
        int tokenCount;
        int positionToOutput = 0;
        while ( (tokenCount = tokenizer
                .getNextTokens(tokens, startPositions, 0)) > 0) {

            for (int i = 0; i < tokenCount; i++) {
                if (startPositions[i] > positionToOutput) {
                    if (listener != null) {
                        listener.unmarkedTextIdentified(text, positionToOutput,
                                startPositions[i] - positionToOutput);
                    }
                    positionToOutput += startPositions[i] - positionToOutput;
                }

                boolean newId = false;
                String wordId;
                String tokenImageLowerCase = tokens[i].getImage().toLowerCase();

                if (stopwords.contains(tokenImageLowerCase)) {
                    wordId = null;
                }
                else {
                    String stem = stemmer.getStem(tokenImageLowerCase
                            .toCharArray(), 0, tokenImageLowerCase.length());
                    if (stem == null) {
                        stem = tokenImageLowerCase;
                    }
                    StemInfo stemInfo = (StemInfo)wordStems.get(stem);
                    if (stemInfo == null) {
                        wordId = Integer.toString(currentWordId);
                        currentWordId++;
                        wordStems.put(stem, new StemInfo(wordId, 1));
                        newId = true;
                    }
                    else 
                    {
                        wordId = stemInfo.id;
                        stemInfo.frequency++;
                    }
                }

                if (listener != null) {
                    listener.markedTextIdentified(text, positionToOutput,
                            tokenImageLowerCase.length(), wordId, newId);
                }

                positionToOutput += tokens[i].getImage().length();
            }
        }

        if (positionToOutput < text.length && listener != null) {
            listener.unmarkedTextIdentified(text, positionToOutput, text.length
                    - positionToOutput);
        }
    }


    /**
     * Returns an Id for a word or null if the word has not been marked.
     * 
     * @param word
     * @return
     */
    public String getWordId(String word)
    {
        String wordLowerCase = word.toLowerCase();
        String stem = stemmer.getStem(wordLowerCase.toCharArray(), 0,
                wordLowerCase.length());
        if (stem == null) {
            stem = wordLowerCase;
        }

        return ((StemInfo)wordStems.get(stem)).id;
    }

    public Iterator getWordInfos()
    {
        return wordStems.values().iterator();
    }
}
