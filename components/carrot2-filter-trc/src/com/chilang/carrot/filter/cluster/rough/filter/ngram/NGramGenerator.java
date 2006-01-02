package com.chilang.carrot.filter.cluster.rough.filter.ngram;

import com.chilang.carrot.tokenizer.ITokenizer;

import java.util.LinkedList;


/**
 * It takes as an input a txt string and
 * generate a collection of n-gram (with word as an unit)
 */
public class NGramGenerator implements TextProcessor {
    int nGramMaxLength;
    NGramProcessor nGramProcessor;

    public NGramGenerator(int maxLength, NGramProcessor processor) {
        this.nGramMaxLength = maxLength;
        this.nGramProcessor = processor;
        this.factory = new NGramFactory();
    }

    NGramFactory factory;

    public NGramGenerator(int nGramMaxLength, NGramProcessor nGramProcessor, NGramFactory factory) {
        this(nGramMaxLength, nGramProcessor);
        this.factory = factory;
    }



    public void process(ITokenizer tokenizer) {
//        String[] words = tokenizer.tokenize();
        /**
         * Number of n-grams with length from 1..k of n-words long text is :
         *    n + (n-1) + (n-2) + .. + (n-k+1) = k * n - (1+2+..+k-1) = k*n - (k-1)*k/2
         *  = k * (2*n - (k-1)) / 2
         */
//        int ngramLen = (words.length * 2 - nGramMaxLength +1) * nGramMaxLength / 2;

//        System.out.println("num of ngrams="+ngramLen);
        /**
         * Array of all n-grams
         */


        /**
         * Last longest phrase (list of words) in the processing token stream
         */
        LinkedList phraseBuffer = new LinkedList();

        while(tokenizer.hasToken()) {

            String token = tokenizer.nextToken();

            if (token.equals(ITokenizer.SENTENCE_DELIMITER)
             || token.equals(ITokenizer.PHRASE_DELIMITER)
             || token.length() <= 2
//             || StringUtils.isAllDigits(token)
            ) {

                phraseBuffer.clear();

            } else {

                // add new word to phrase buffer
                phraseBuffer.addLast(token);

                /**
                 * Generate ngram of length from 1 .. min(maxLength, phrase buffer size)
                 * starting from last word, extending backward
                 */
                for (int k=1; k<=nGramMaxLength && k<=phraseBuffer.size(); k++) {
                    String[] words = (String[])phraseBuffer.subList(phraseBuffer.size()-k, phraseBuffer.size()).toArray(new String[0]);
                    //create ngram from array of words after
                    //removing all stopwords from head & tail
                    NGram nGram = factory.create(words);

                    //process ngram only if it doesn't contain any stop words

                    if ((nGram != null) && (nGram.length() == words.length))
                        nGramProcessor.process(nGram);
                }
            }
        }
    }

    public NGram[] getNGrams() {
        return nGramProcessor.getNGrams();
    }

}
