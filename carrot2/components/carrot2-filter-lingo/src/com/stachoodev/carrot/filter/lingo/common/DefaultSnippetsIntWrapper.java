
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

package com.stachoodev.carrot.filter.lingo.common;

import com.stachoodev.carrot.filter.lingo.util.arrays.ArrayUtils;

import java.util.*;


/**
 *
 */
public class DefaultSnippetsIntWrapper extends AbstractSnippetsIntWrapper {
    /** */

    /** DOCUMENT ME! */
    private HashSet stopWords;

    /**
     * Method SnippetsIntWrapper.
     *
     * @param documents
     */
    public DefaultSnippetsIntWrapper(Snippet[] documents) {
        this(documents, new HashSet());
    }

    /**
     * Method SnippetsIntWrapper.
     *
     * @param documents
     */
    public DefaultSnippetsIntWrapper(Snippet[] documents, HashSet stopWords) {
        this.stopWords = stopWords;
        
        String[] strings = new String[documents.length];
        for (int i = 0; i < documents.length; i++) {
            strings[i] = documents[i].getText();
        }

        setDocuments(strings);
    }

    /**
     * @param document document data. Sentences MUST be period-delimited, with
     *        a space character preceding and succeeding the period.
     *        Subsequent words MUST be separated by a single space character.
     */
    public DefaultSnippetsIntWrapper(String[] documents) {
        this(documents, new HashSet());
    }

    /**
     * @param document document data. Sentences MUST be period-delimited, with
     *        a space character preceding and succeeding the period.
     *        Subsequent words MUST be separated by a single space character.
     */
    public DefaultSnippetsIntWrapper(String[] documents, HashSet stopWords) {
        this.stopWords = stopWords;
        setDocuments(documents);
    }

    /**
     *
     */
    protected DefaultSnippetsIntWrapper() {
    }

    /**
     *
     */
    protected void createIntData() {
        StringTokenizer stringTokenizer = new StringTokenizer(documentsData);
        ArrayList wordWrappers = new ArrayList();
        ArrayList stopWordCodesArray = new ArrayList();
        Hashtable words = new Hashtable();
        int position = 0;

        //
        // All words contained in the input documents are assigned increasing 
        // integer codes 0, 1, etc. Every sentence delimiter ('.') is 
        // assigned an integer code in a decreasing order starting from 0x7fffffff.
        // Document delimiters are treated as sentence delimiters.
        //
        int code = 0;
        int wordCode = 0;
        int periodCode = 0x7fffffff;
        int documentIndex = 0;

        // Convert strings to integers
        while (stringTokenizer.hasMoreTokens()) {
            String word = stringTokenizer.nextToken();

            if (word.equals(".")) {
                code = periodCode--;
            } else if (word.equals(DOCUMENT_DELIMITER)) {
                code = periodCode--;
                documentIndex++;

                // Document index value for a document delimiter is void
            } else {
                if (words.containsKey(word)) {
                    code = ((Integer) words.get(word)).intValue();
                } else {
                    code = wordCode++;
                    words.put(word, new Integer(code));

                    // Check if stop-word
                    if ((stopWords != null) && stopWords.contains(word)) {
                        stopWordCodesArray.add(new Integer(code));
                    }
                }
            }

            wordWrappers.add(new WordWrapper(position, code, documentIndex));
            position += (word.length() + 1); // +1 on account of the ' ' character
        }

        distinctWordCount = wordCode;

        // Write into int arrays
        intData = new int[wordWrappers.size() + 1];
        wordPositions = new int[wordWrappers.size() + 1];
        documentIndices = new int[wordWrappers.size()];

        for (int i = 0; i < wordWrappers.size(); i++) {
            intData[i] = ((WordWrapper) wordWrappers.get(i)).code;
            wordPositions[i] = ((WordWrapper) wordWrappers.get(i)).position;
            documentIndices[i] = ((WordWrapper) wordWrappers.get(i)).documentIndex;
        }

        intData[wordWrappers.size()] = -1;
        wordPositions[wordWrappers.size()] = position;

        // Stop words
        stopWordCodes = new int[stopWordCodesArray.size()];

        for (int i = 0; i < stopWordCodes.length; i++) {
            stopWordCodes[i] = ((Integer) stopWordCodesArray.get(i)).intValue();
        }
    }

    /**
     * @see com.stachoodev.carrot.filter.lingo.common.AbstractSnippetsIntWrapper#clone()
     */
    public Object clone() {
        DefaultSnippetsIntWrapper clone = new DefaultSnippetsIntWrapper();

        clone.distinctWordCount = this.distinctWordCount;
        clone.documentCount = this.documentCount;
        clone.documentIndices = ArrayUtils.clone(this.documentIndices);
        clone.intData = ArrayUtils.clone(this.intData);
        clone.documentsData = new String(this.documentsData);

        return clone;
    }
}
