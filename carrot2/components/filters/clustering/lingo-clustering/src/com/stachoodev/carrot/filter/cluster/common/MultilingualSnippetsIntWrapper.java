

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.stachoodev.carrot.filter.cluster.common;


import java.util.*;

import com.stachoodev.util.arrays.ArrayUtils;


/**
 *
 */
public class MultilingualSnippetsIntWrapper
    extends AbstractSnippetsIntWrapper
{
    /** Sets of stopwords for different languages */
    private HashMap stopWordSets;

    /** Indices of documents starting a new language */
    private int [] languageStartIndices;

    /** Names of languages corresponding to the languageStartIndices array */
    private String [] languageNames;

    /** AbstractSnippetIntWrapper object for each language */
    private HashMap snippetIntWrappers;

    /**
     * Method SnippetsIntWrapper. IMPORTANT: the input snippets must be sorted by their language
     *
     * @param documents
     */
    public MultilingualSnippetsIntWrapper(Snippet [] documents)
    {
        this(documents, new HashMap());
    }


    /**
     * Method SnippetsIntWrapper. IMPORTANT: the input snippets must be sorted by their language
     *
     * @param documents
     */
    public MultilingualSnippetsIntWrapper(Snippet [] documents, HashMap stopWordSets)
    {
        super();
        this.documentCount = documents.length;
        this.snippetIntWrappers = new HashMap();
        this.stopWordSets = stopWordSets;

        // Count the number of distinct languages
        int languageCount = 1;

        for (int i = 1; i < documents.length; i++)
        {
            if (!documents[i].getLanguage().equals(documents[i - 1].getLanguage()))
            {
                languageCount++;
            }
        }

        this.languageStartIndices = new int[languageCount];
        this.languageNames = new String[languageCount];

        String [] strings = new String[documents.length];

        if (documents.length > 0)
        {
            languageStartIndices[0] = 0;
            languageNames[0] = documents[0].getLanguage();
            strings[0] = documents[0].getText();
        }

        int languageIndex = 1;

        for (int i = 1; i < documents.length; i++)
        {
            strings[i] = documents[i].getText();

            if (!documents[i].getLanguage().equals(documents[i - 1].getLanguage()))
            {
                languageNames[languageIndex] = documents[i].getLanguage();
                languageStartIndices[languageIndex] = i;
                languageIndex++;
            }
        }

        setDocuments(strings);
    }


    /**
     *
     */
    protected MultilingualSnippetsIntWrapper()
    {
    }

    /**
     *
     */
    protected void createIntData()
    {
        if (documentCount == 0)
        {
            stopWordCodes = new int[0];
            documentIndices = new int[0];

            return;
        }

        StringTokenizer stringTokenizer = new StringTokenizer(documentsData);
        ArrayList wordWrappers = new ArrayList();
        ArrayList stopWordCodesArray = new ArrayList();
        HashMap words = new HashMap();

        //
        // All words contained in the input documents are assigned increasing 
        // integer codes 0, 1, etc. Every sentence delimiter ('.') is 
        // assigned an integer code in a decreasing order starting from 0x7fffffff.
        // Document delimiters are treated as sentence delimiters.
        //
        // Separate stopword code arrays are generated for each language
        //
        int code = 0;
        int wordCode = 0;
        int periodCode = 0x7fffffff;
        int documentIndex = 0;
        int position = 0;

        // Initial stopword set
        int currentLanguage = 0;
        HashSet stopWords = (HashSet) stopWordSets.get(languageNames[currentLanguage]);

        // Needed to split stop word codes array
        int [] stopWordsStartIndices = new int[languageNames.length + 1];
        stopWordsStartIndices[0] = 0;

        // Needed to split intArray, documentIndices and wordPositions arrays
        int [] documentStartIndices = new int[languageNames.length + 1];
        documentStartIndices[0] = 0;

        // Needed when adding stopwords
        HashSet addedStopWords = new HashSet();

        int [] documentCounts = new int[languageNames.length];

        //        
        // Create the global int wrapper
        //
        while (stringTokenizer.hasMoreTokens())
        {
            String word = stringTokenizer.nextToken().toLowerCase();

            if (word.equals("."))
            {
                code = periodCode--;
            }
            else if (word.equals(DOCUMENT_DELIMITER))
            {
                code = periodCode--;

                // Document index value for a document delimiter is void
                documentIndex++;
                documentCounts[currentLanguage]++;

                // Check whether to switch to the next language
                if (
                    (languageStartIndices.length > (currentLanguage + 1))
                        && (languageStartIndices[currentLanguage + 1] == documentIndex)
                )
                {
                    currentLanguage++;
                    stopWords = (HashSet) stopWordSets.get(languageNames[currentLanguage]);
                    stopWordsStartIndices[currentLanguage] = stopWordCodesArray.size();
                    documentStartIndices[currentLanguage] = wordWrappers.size();
                    addedStopWords = new HashSet();
                }
            }
            else
            {
                if (words.containsKey(word))
                {
                    code = ((Integer) words.get(word)).intValue();
                }
                else
                {
                    code = wordCode++;
                    words.put(word, new Integer(code));
                }

                // Check if stop-word
                if (
                    (stopWords != null) && stopWords.contains(word)
                        && !addedStopWords.contains(word)
                )
                {
                    stopWordCodesArray.add(new Integer(code));
                    addedStopWords.add(word);
                }
            }

            wordWrappers.add(new WordWrapper(position, code, documentIndex));
            position += (word.length() + 1); // +1 on account of the ' ' character
        }

        documentCounts[currentLanguage]++;
        stopWordsStartIndices[currentLanguage + 1] = stopWordCodesArray.size();
        documentStartIndices[currentLanguage + 1] = wordWrappers.size();

        distinctWordCount = wordCode;

        // Write into int arrays
        intData = new int[wordWrappers.size() + 1];
        wordPositions = new int[wordWrappers.size() + 1];
        documentIndices = new int[wordWrappers.size()];

        for (int i = 0; i < wordWrappers.size(); i++)
        {
            intData[i] = ((WordWrapper) wordWrappers.get(i)).code;
            wordPositions[i] = ((WordWrapper) wordWrappers.get(i)).position;
            documentIndices[i] = ((WordWrapper) wordWrappers.get(i)).documentIndex;
        }

        intData[wordWrappers.size()] = -1;
        wordPositions[wordWrappers.size()] = position;

        // Stop words
        if (stopWordCodesArray.size() > 0)
        {
            stopWordCodes = new int[stopWordCodesArray.size()];

            for (int i = 0; i < stopWordCodes.length; i++)
            {
                stopWordCodes[i] = ((Integer) stopWordCodesArray.get(i)).intValue();
            }
        }

        //
        // Create split snippet int wrappers
        //
        // Split documentsData
        String [] splitDocumentsData = new String[languageNames.length];

        for (int j = 0; j < splitDocumentsData.length; j++)
        {
            splitDocumentsData[j] = documentsData.substring(
                    wordPositions[documentStartIndices[j]],
                    wordPositions[documentStartIndices[j + 1]] - 1
                );
        }

        // Split intData and add finishing '-1'
        int [][] splitIntData = ArrayUtils.split(intData, documentStartIndices);

        for (int j = 0; j < splitIntData.length; j++)
        {
            splitIntData[j] = ArrayUtils.extend(splitIntData[j], -1);
        }

        // Split documentIndices
        int [][] splitDocumentIndices = ArrayUtils.split(documentIndices, documentStartIndices);

        // Split stop word codes
        int [][] splitStopWordCodes = ArrayUtils.split(stopWordCodes, stopWordsStartIndices);

        // Split wordPositions and correct offsets
        int [][] splitWordPositions = ArrayUtils.split(wordPositions, documentStartIndices);

        for (int j = 0; j < splitWordPositions.length; j++)
        {
            splitWordPositions[j] = ArrayUtils.extend(
                    splitWordPositions[j], wordPositions[documentStartIndices[j + 1]]
                );

            for (int k = splitWordPositions[j].length - 1; k > -1; k--)
            {
                splitWordPositions[j][k] -= splitWordPositions[j][0];
            }
        }

        for (int i = 0; i < languageNames.length; i++)
        {
            Arrays.sort(splitStopWordCodes[i]);

            AbstractSnippetsIntWrapper wrapper = new SplitSnippetsIntWrapper(
                    splitDocumentsData[i], documentCounts[i], 0, // not necessary for the time being
                    splitIntData[i], splitDocumentIndices[i], splitStopWordCodes[i],
                    splitWordPositions[i]
                );

            snippetIntWrappers.put(languageNames[i], wrapper);
        }
    }


    /**
     * @param language
     *
     * @return
     */
    public AbstractSnippetsIntWrapper getWrapperForLanguage(String language)
    {
        return (AbstractSnippetsIntWrapper) snippetIntWrappers.get(language);
    }


    /**
     * @return
     */
    public String [] getLanguageNames()
    {
        return (String []) snippetIntWrappers.keySet().toArray(
            new String[snippetIntWrappers.keySet().size()]
        );
    }


    /**
     * @see com.stachoodev.util.suffixarrays.wrapper.IntWrapper#reverse()
     */
    public void reverse()
    {
        super.reverse();

        Iterator keys = snippetIntWrappers.keySet().iterator();

        while (keys.hasNext())
        {
            String key = (String) keys.next();
            ((AbstractSnippetsIntWrapper) snippetIntWrappers.get(key)).reverse();
        }
    }


    /**
     * @see java.lang.Object#clone()
     */
    public Object clone()
    {
        MultilingualSnippetsIntWrapper clone = new MultilingualSnippetsIntWrapper();

        clone.distinctWordCount = this.distinctWordCount;
        clone.documentCount = this.documentCount;
        clone.documentIndices = ArrayUtils.clone(this.documentIndices);
        clone.intData = ArrayUtils.clone(this.intData);
        clone.languageNames = ArrayUtils.clone(this.languageNames);
        clone.languageStartIndices = ArrayUtils.clone(this.languageStartIndices);
        clone.documentsData = new String(this.documentsData);
        clone.snippetIntWrappers = new HashMap();

        Iterator keys = snippetIntWrappers.keySet().iterator();

        while (keys.hasNext())
        {
            String key = (String) keys.next();
            AbstractSnippetsIntWrapper wrapper = (AbstractSnippetsIntWrapper) snippetIntWrappers
                .get(key);
            clone.snippetIntWrappers.put(key, wrapper.clone());
        }

        return clone;
    }

    /**
     *
     */
    protected class SplitSnippetsIntWrapper
        extends AbstractSnippetsIntWrapper
    {
        /**
         * @param documentsData
         * @param documentCount
         * @param distinctWordsCount
         * @param intData
         * @param documentIndices
         * @param stopWordCodes
         * @param wordPositions
         */
        public SplitSnippetsIntWrapper(
            String documentsData, int documentCount, int distinctWordsCount, int [] intData,
            int [] documentIndices, int [] stopWordCodes, int [] wordPositions
        )
        {
            this.documentsData = documentsData;
            this.documentCount = documentCount;
            this.distinctWordCount = distinctWordsCount;
            this.intData = intData;
            this.documentIndices = documentIndices;
            this.stopWordCodes = stopWordCodes;
            this.wordPositions = wordPositions;
        }

        /**
         * @see com.stachoodev.carrot.filter.cluster.common.AbstractSnippetsIntWrapper#createIntData()
         */
        protected void createIntData()
        {
            // Does nothing - all necessary data is given in the constructor
        }


        /**
         * @see java.lang.Object#clone()
         */
        public Object clone()
        {
            SplitSnippetsIntWrapper wrapper = new SplitSnippetsIntWrapper(
                    documentsData, documentCount, distinctWordCount, ArrayUtils.clone(intData),
                    ArrayUtils.clone(documentIndices), ArrayUtils.clone(stopWordCodes),
                    ArrayUtils.clone(wordPositions)
                );

            return wrapper;
        }
    }
}
