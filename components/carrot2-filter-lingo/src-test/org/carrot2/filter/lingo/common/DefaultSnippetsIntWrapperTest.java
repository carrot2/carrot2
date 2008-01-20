
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.lingo.common;


import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import java.util.HashSet;


/**
 *
 */
public class DefaultSnippetsIntWrapperTest
    extends TestCase
{
    /** */
    private static final DefaultSnippetsIntWrapper emptyInput = new DefaultSnippetsIntWrapper(
            new String[0]
        );
    private static final String emptyOutputPhrase = "";

    /** */
    private static final DefaultSnippetsIntWrapper oneSentenceInput = new DefaultSnippetsIntWrapper(
            new String [] { "Ala ma kota" }
        );
    private static final int [] oneSentenceOutputWordArray = new int [] { 0, 1, 2, -1 };
    private static final String [] oneSentenceOutputPhrases = new String []
        {
            "Ala", "Ala ma", "Ala ma kota", "ma", "ma kota", "kota"
        };

    /** */
    private static final DefaultSnippetsIntWrapper twoSentencesInput = new DefaultSnippetsIntWrapper(
            new String [] { "Ala . kota ." }
        );
    private static final int [] twoSentencesOutputWordArray = new int []
        {
            0, 0x7fffffff, 1, 0x7ffffffe, -1
        };
    private static final String [] twoSentencesOutputPhrases = new String []
        {
            "Ala", "Ala .", "Ala . kota", "Ala . kota .", ".", ". kota", ". kota .", "kota",
            "kota .", "."
        };

    /** */
    private static final DefaultSnippetsIntWrapper twoDocumentsInput = new DefaultSnippetsIntWrapper(
            new String [] { "Ala ma kota", "", "Kot ma Ale" }
        );
    private static final int [] twoDocumentsOutputWordArray = new int []
        {
            0, 1, 2, 0x7fffffff, 3, 1, 4, -1
        };
    private static final int [] twoDocumentsOutputDocumentArray = new int [] { 0, 0, 0, 1, 1, 1, 1 };

    /**
     *
     */
    public void testEmptyData()
    {
        assertEquals("intData array length = 0", 1, emptyInput.asIntArray().length);
        assertEquals(
            "empty substring", emptyOutputPhrase, emptyInput.getStringRepresentation(0, 0)
        );
    }


    /**
     *
     */
    public void testOneSentence()
    {
        assertEquals("code array", oneSentenceOutputWordArray, oneSentenceInput.asIntArray());

        int k = 0;

        for (int i = 0; i < (oneSentenceOutputWordArray.length - 1); i++)
        {
            for (int j = i + 1; j < oneSentenceOutputWordArray.length; j++)
            {
                assertEquals(
                    "word retrieval", oneSentenceOutputPhrases[k++],
                    oneSentenceInput.getStringRepresentation(i, j)
                );
            }
        }
    }


    /**
     *
     */
    public void testTwoSentences()
    {
        assertEquals("code array", twoSentencesOutputWordArray, twoSentencesInput.asIntArray());

        int k = 0;

        for (int i = 0; i < (twoSentencesOutputWordArray.length - 1); i++)
        {
            for (int j = i + 1; j < twoSentencesOutputWordArray.length; j++)
            {
                assertEquals(
                    "word retrieval", twoSentencesOutputPhrases[k++],
                    twoSentencesInput.getStringRepresentation(i, j)
                );
            }
        }
    }


    /**
     *
     */
    public void testTwoDocuments()
    {
        assertEquals("code array", twoDocumentsOutputWordArray, twoDocumentsInput.asIntArray());
        assertEquals(
            "code array", twoDocumentsOutputDocumentArray, twoDocumentsInput.getDocumentIndices()
        );
        assertEquals("kota", "Kot", twoDocumentsInput.getStringRepresentation(4, 5));
    }


    /**
     *
     */
    public void testStopWords()
    {
        HashSet stopWords = new HashSet();
        stopWords.add("ma");
        stopWords.add("I");
        stopWords.add("tez");

        DefaultSnippetsIntWrapper stopWordsInput = new DefaultSnippetsIntWrapper(
                new String [] { "Ala ma kota . I ma tez psa" }, stopWords
            );
        int [] stopWordsStopWordCodes = new int [] { 1, 3, 4 };

        assertEquals("stop word codes", stopWordsStopWordCodes, stopWordsInput.getStopWordCodes());
    }


    /**
     *
     */
    protected void assertEquals(String comment, int [] arrayA, int [] arrayB)
    {
        assertEquals(comment + " - arrayLengths:", arrayA.length, arrayB.length);

        try
        {
            for (int i = 0; i < arrayA.length; i++)
            {
                assertEquals(comment + " - array element [" + i + "]:", arrayA[i], arrayB[i]);
            }
        }
        catch (AssertionFailedError e)
        {
            for (int i = 0; i < arrayA.length; i++)
            {
                System.out.print("(" + arrayA[i] + " " + arrayB[i] + ") \n");
            }

            throw e;
        }
    }
}
