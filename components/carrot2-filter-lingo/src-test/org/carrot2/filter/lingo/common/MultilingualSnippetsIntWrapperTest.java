
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
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
import java.util.HashMap;
import java.util.HashSet;


/**
 * @author stachoo
 */
public class MultilingualSnippetsIntWrapperTest
    extends TestCase
{
    /** */
    private HashMap stopWordSets;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp()
    {
        HashSet en = new HashSet();
        en.add("to");
        en.add("a");
        en.add("the");

        HashSet pl = new HashSet();
        pl.add("i");
        pl.add("dla");

        HashSet de = new HashSet();
        de.add("bei");
        de.add("ein");
        de.add("das");

        stopWordSets = new HashMap();
        stopWordSets.put("en", en);
        stopWordSets.put("pl", pl);
        stopWordSets.put("de", de);
    }


    /**
     * Empty data testcase.
     */
    public void testEmptyData()
    {
        Snippet [] inputSnippets = new Snippet[0];

        MultilingualSnippetsIntWrapper wrapper = new MultilingualSnippetsIntWrapper(inputSnippets);

        assertEquals("documentCount", 0, wrapper.getDocumentCount());
        assertEquals("distinctWordCount", 0, wrapper.getDistinctWordCount());
        assertEquals("intData.length", 1, wrapper.asIntArray().length);
        assertEquals("documentIndices.length", 0, wrapper.getDocumentIndices().length);
        assertEquals("stopWordCodes.length", 0, wrapper.getStopWordCodes().length);
    }


    /**
     * Single language test case
     */
    public void testSingleLanguage()
    {
        Snippet [] inputSnippets = new Snippet []
            {
                new Snippet("1", "title one", "body one", "en"),
                new Snippet("2", "title two", "body two", "en")
            };

        int [] intData = new int [] { 0, 1, 0x7fffffff, 2, 1, 0x7ffffffe, 0, 3, 0x7ffffffd, 2, 3, -1 };

        int [] documentIndices = new int [] { 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1 };

        int [] stopWordCodes = new int[0];

        MultilingualSnippetsIntWrapper wrapper = new MultilingualSnippetsIntWrapper(inputSnippets);

        assertEquals("documentCount", 2, wrapper.getDocumentCount());
        assertEquals("distinctWordCount", 4, wrapper.getDistinctWordCount());
        assertEquals("intData", intData, wrapper.asIntArray());
        assertEquals("documentIndices", documentIndices, wrapper.getDocumentIndices());
        assertEquals("stopWordCodes", stopWordCodes, wrapper.getStopWordCodes());
    }


    /**
     * Single language test case
     */
    public void testMoreLanguages()
    {
        Snippet [] inputSnippets = new Snippet []
            {
                new Snippet("1", "title one", "the body one", "en"),
                new Snippet("2", "title two", "a body two", "en"),
                new Snippet("3", "tytul trzy", "dla tresc trzy", "pl"),
                new Snippet("4", "titel vier", "etwas ein vier", "de")
            };

        int [] intData = new int []
            {
                0, 1, 0x7fffffff, 2, 3, 1, 0x7ffffffe, 0, 4, 0x7ffffffd, 5, 3, 4, 0x7ffffffc, 6, 7,
                0x7ffffffb, 8, 9, 7, 0x7ffffffa, 10, 11, 0x7ffffff9, 12, 13, 11, -1
            };

        int [] documentIndices = new int []
            {
                0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3
            };

        int [] stopWordCodes = new int [] { 2, 5, 8, 13 };

        MultilingualSnippetsIntWrapper wrapper = new MultilingualSnippetsIntWrapper(
                inputSnippets, stopWordSets
            );

        // Global
        assertEquals("documentCount", 4, wrapper.getDocumentCount());
        assertEquals("distinctWordCount", 14, wrapper.getDistinctWordCount());
        assertEquals("intData", intData, wrapper.asIntArray());
        assertEquals("documentIndices", documentIndices, wrapper.getDocumentIndices());
        assertEquals("stopWordCodes", stopWordCodes, wrapper.getStopWordCodes());

        // Language-split
        AbstractSnippetsIntWrapper enWrapper = wrapper.getWrapperForLanguage("en");

        int [] enIntData = new int []
            {
                0, 1, 0x7fffffff, 2, 3, 1, 0x7ffffffe, 0, 4, 0x7ffffffd, 5, 3, 4, -1
            };

        int [] enDocumentIndices = new int [] { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1 };

        int [] enStopWordCodes = new int [] { 2, 5 };

        assertEquals("en: documentCount", 2, enWrapper.getDocumentCount());
        assertEquals("en: intData", enIntData, enWrapper.asIntArray());
        assertEquals("en: documentIndices", enDocumentIndices, enWrapper.getDocumentIndices());
        assertEquals("en: stopWordCodes", enStopWordCodes, enWrapper.getStopWordCodes());
        assertEquals("en: phrase", "body two", enWrapper.getStringRepresentation(11, 13));

        AbstractSnippetsIntWrapper plWrapper = wrapper.getWrapperForLanguage("pl");

        int [] plIntData = new int [] { 0x7ffffffc, 6, 7, 0x7ffffffb, 8, 9, 7, -1 };

        int [] plDocumentIndices = new int [] { 2, 2, 2, 2, 2, 2, 2 };

        int [] plStopWordCodes = new int [] { 8 };

        assertEquals("pl: documentCount", 1, plWrapper.getDocumentCount());
        assertEquals("pl: intData", plIntData, plWrapper.asIntArray());
        assertEquals("pl: documentIndices", plDocumentIndices, plWrapper.getDocumentIndices());
        assertEquals("pl: stopWordCodes", plStopWordCodes, plWrapper.getStopWordCodes());
        assertEquals("pl: phrase", "dla tresc trzy", plWrapper.getStringRepresentation(4, 7));

        AbstractSnippetsIntWrapper deWrapper = wrapper.getWrapperForLanguage("de");

        int [] deIntData = new int [] { 0x7ffffffa, 10, 11, 0x7ffffff9, 12, 13, 11, -1 };

        int [] deDocumentIndices = new int [] { 3, 3, 3, 3, 3, 3, 3 };

        int [] deStopWordCodes = new int [] { 13 };

        assertEquals("de: documentCount", 1, deWrapper.getDocumentCount());
        assertEquals("de: intData", deIntData, deWrapper.asIntArray());
        assertEquals("de: documentIndices", deDocumentIndices, deWrapper.getDocumentIndices());
        assertEquals("de: stopWordCodes", deStopWordCodes, deWrapper.getStopWordCodes());
        assertEquals("de: phrase", "etwas ein", deWrapper.getStringRepresentation(4, 6));
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
