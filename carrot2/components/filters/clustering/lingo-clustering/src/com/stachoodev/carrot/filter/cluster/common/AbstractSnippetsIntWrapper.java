

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


import com.stachoodev.util.suffixarrays.wrapper.AbstractIntWrapper;
import com.stachoodev.util.suffixarrays.wrapper.Substring;


/**
 *
 */
public abstract class AbstractSnippetsIntWrapper
    extends AbstractIntWrapper
{
    /** Document delimiter char */
    protected static final String DOCUMENT_DELIMITER = "|";

    /** All documents concatenated */
    protected String documentsData;

    /** Input documents */
    protected int documentCount;

    /** Distint word count */
    protected int distinctWordCount;

    /** Int codes of stop words */
    protected int [] stopWordCodes;

    /**
     * Starting positions of all words comprising the input documents are stored here to facilitate
     * wordIndexRange -> realString mapping.
     */
    protected int [] wordPositions;

    /**
     * Document indices corresponding to word indices are stored here to support wordIndex ->
     * documentIndex mapping.
     */
    protected int [] documentIndices;

    /**
     *
     */
    public AbstractSnippetsIntWrapper()
    {
        this(new Snippet[0]);
    }


    /**
     * Method SnippetsIntWrapper.
     *
     * @param documents
     */
    public AbstractSnippetsIntWrapper(Snippet [] documents)
    {
        super();
        this.documentCount = documents.length;

        String [] strings = new String[documents.length];

        for (int i = 0; i < documents.length; i++)
        {
            strings[i] = documents[i].getText();
        }

        setDocuments(strings);
    }

    /**
     * Method setDocuments.
     *
     * @param documents
     */
    protected void setDocuments(String [] documents)
    {
        if (documents.length > 0)
        {
            StringBuffer stringBuffer = new StringBuffer(documents[0]);

            for (int i = 1; i < documents.length; i++)
            {
                if (documents[i].length() > 0)
                {
                    stringBuffer.append(' ');
                    stringBuffer.append(DOCUMENT_DELIMITER);
                    stringBuffer.append(' ');
                    stringBuffer.append(documents[i]);
                }
            }

            documentsData = stringBuffer.toString();
        }
        else
        {
            documentsData = "";
        }

        createIntData();
    }


    /**
     *
     */
    protected abstract void createIntData();


    /**
     * @see java.lang.Object#clone()
     */
    public abstract Object clone();


    /**
     *
     */
    public int [] getDocumentIndices()
    {
        return documentIndices;
    }


    /**
     *
     */
    public String getStringRepresentation(Substring substring)
    {
        return getStringRepresentation(substring.getFrom(), substring.getTo());
    }


    /**
     *
     */
    public String getStringRepresentation(int from, int to)
    {
        if (from == to)
        {
            return "";
        }

        String result = documentsData.substring(wordPositions[from], wordPositions[to] - 1);

        return result;
    }


    /**
     * @return
     */
    public int [] getStopWordCodes()
    {
        return stopWordCodes;
    }


    /**
     *
     */
    public int getDocumentCount()
    {
        return documentCount;
    }


    /**
     * @return
     */
    public int getDistinctWordCount()
    {
        return distinctWordCount;
    }

    /**
     *
     */
    protected class WordWrapper
    {
        /** */
        public int position;

        /** */
        public int code;

        /** */
        public int documentIndex;

        /**
         *
         */
        public WordWrapper(int position, int code, int documentIndex)
        {
            this.position = position;
            this.code = code;
            this.documentIndex = documentIndex;
        }
    }
}
