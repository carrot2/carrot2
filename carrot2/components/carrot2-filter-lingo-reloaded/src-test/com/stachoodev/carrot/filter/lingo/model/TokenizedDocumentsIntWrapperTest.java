/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.filter.lingo.model;

import java.util.*;

import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.util.tokenizer.*;
import com.stachoodev.suffixarrays.wrapper.*;

import junit.framework.*;
import junitx.framework.*;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class TokenizedDocumentsIntWrapperTest extends TestCase
{
    /** A helper tokenizer factory */
    private SnippetTokenizer snippetTokenizer;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        snippetTokenizer = new SnippetTokenizer();
    }

    /**
     *  
     */
    public void testEmptyData()
    {
        TokenizedDocument document = snippetTokenizer
            .tokenize(new RawDocumentSnippet("", ""));
        TokenizedDocumentsIntWrapper wrapper = new TokenizedDocumentsIntWrapper(
            Arrays.asList(new TokenizedDocument []
            { document }));

        ArrayAssert.assertEquals("Empty int array", new int []
        { -1 }, wrapper.asIntArray());
    }

    /**
     *  
     */
    public void testSingleSnippetWithEmptyBody()
    {
        TokenizedDocument document = snippetTokenizer
            .tokenize(new RawDocumentSnippet("Just a title", ""));
        TokenizedDocumentsIntWrapper wrapper = new TokenizedDocumentsIntWrapper(
            Arrays.asList(new TokenizedDocument []
            { document }));

        ArrayAssert.assertEquals("Single snippet with empty body int array",
            new int []
            { 0, 1 * MaskableIntWrapper.SECONDARY_OFFSET,
             2 * MaskableIntWrapper.SECONDARY_OFFSET,
             -1 - MaskableIntWrapper.SECONDARY_OFFSET, -1 }, wrapper
                .asIntArray());
    }

    /**
     *  
     */
    public void testSingleSnippetWithEmptyTitle()
    {
        TokenizedDocument document = snippetTokenizer
            .tokenize(new RawDocumentSnippet("", "This time only a snippet"));
        TokenizedDocumentsIntWrapper wrapper = new TokenizedDocumentsIntWrapper(
            Arrays.asList(new TokenizedDocument []
            { document }));

        ArrayAssert.assertEquals("Single snippet with empty title int array",
            new int []
            { 0, 1 * MaskableIntWrapper.SECONDARY_OFFSET,
             2 * MaskableIntWrapper.SECONDARY_OFFSET,
             3 * MaskableIntWrapper.SECONDARY_OFFSET,
             4 * MaskableIntWrapper.SECONDARY_OFFSET,
             -1 - MaskableIntWrapper.SECONDARY_OFFSET, -1 }, wrapper
                .asIntArray());
    }

    /**
     *  
     */
    public void testRepeatedTokens()
    {
        TokenizedDocument document = snippetTokenizer
            .tokenize(new RawDocumentSnippet("title of the snippet",
                "body of the snippet"));
        TokenizedDocumentsIntWrapper wrapper = new TokenizedDocumentsIntWrapper(
            Arrays.asList(new TokenizedDocument []
            { document }));

        ArrayAssert.assertEquals("Single snippet with repeated tokens",
            new int []
            { 0 * MaskableIntWrapper.SECONDARY_OFFSET,
             1 * MaskableIntWrapper.SECONDARY_OFFSET,
             2 * MaskableIntWrapper.SECONDARY_OFFSET,
             3 * MaskableIntWrapper.SECONDARY_OFFSET,
             -1 - MaskableIntWrapper.SECONDARY_OFFSET,
             4 * MaskableIntWrapper.SECONDARY_OFFSET,
             1 * MaskableIntWrapper.SECONDARY_OFFSET,
             2 * MaskableIntWrapper.SECONDARY_OFFSET,
             3 * MaskableIntWrapper.SECONDARY_OFFSET,
             -1 - 2 * MaskableIntWrapper.SECONDARY_OFFSET, -1 }, wrapper
                .asIntArray());
    }

    /**
     *  
     */
    public void testRepeatedSentenceDelimiters()
    {
        TokenizedDocument document = snippetTokenizer
            .tokenize(new RawDocumentSnippet("title. simply, title",
                "body. body. body. body"));
        TokenizedDocumentsIntWrapper wrapper = new TokenizedDocumentsIntWrapper(
            Arrays.asList(new TokenizedDocument []
            { document }));

        ArrayAssert.assertEquals(
            "Single snippet with repeated sentence delimiters", new int []
            { 0 * MaskableIntWrapper.SECONDARY_OFFSET,
             -1 - MaskableIntWrapper.SECONDARY_OFFSET,
             1 * MaskableIntWrapper.SECONDARY_OFFSET,
             0 * MaskableIntWrapper.SECONDARY_OFFSET,
             -1 - 2 * MaskableIntWrapper.SECONDARY_OFFSET,
             2 * MaskableIntWrapper.SECONDARY_OFFSET,
             -1 - 3 * MaskableIntWrapper.SECONDARY_OFFSET,
             2 * MaskableIntWrapper.SECONDARY_OFFSET,
             -1 - 4 * MaskableIntWrapper.SECONDARY_OFFSET,
             2 * MaskableIntWrapper.SECONDARY_OFFSET,
             -1 - 5 * MaskableIntWrapper.SECONDARY_OFFSET,
             2 * MaskableIntWrapper.SECONDARY_OFFSET,
             -1 - 6 * MaskableIntWrapper.SECONDARY_OFFSET, -1 }, wrapper
                .asIntArray());
    }

    /**
     *  
     */
    public void testMoreSnippets()
    {
        TokenizedDocument document1 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("snippet 01 title",
                "snippet 01 body"));
        TokenizedDocument document2 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("snippet 02 title",
                "snippet 02 body"));
        TokenizedDocumentsIntWrapper wrapper = new TokenizedDocumentsIntWrapper(
            Arrays.asList(new TokenizedDocument []
            { document1, document2 }));

        ArrayAssert.assertEquals("Two snippets", new int []
        { 0, 1 * MaskableIntWrapper.SECONDARY_OFFSET,
         2 * MaskableIntWrapper.SECONDARY_OFFSET,
         -1 - MaskableIntWrapper.SECONDARY_OFFSET, 0,
         1 * MaskableIntWrapper.SECONDARY_OFFSET,
         3 * MaskableIntWrapper.SECONDARY_OFFSET,
         -1 - 2 * MaskableIntWrapper.SECONDARY_OFFSET, 0,
         4 * MaskableIntWrapper.SECONDARY_OFFSET,
         2 * MaskableIntWrapper.SECONDARY_OFFSET,
         -1 - 3 * MaskableIntWrapper.SECONDARY_OFFSET, 0,
         4 * MaskableIntWrapper.SECONDARY_OFFSET,
         3 * MaskableIntWrapper.SECONDARY_OFFSET,
         -1 - 4 * MaskableIntWrapper.SECONDARY_OFFSET, -1 }, wrapper
            .asIntArray());
    }

    /**
     *  
     */
    public void testStemmedSnippets()
    {
        TokenizedDocument document1 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("title titles",
                "titled titles document", "en"));
        TokenizedDocument document2 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("documents title",
                "documented snippet body", "en"));
        TokenizedDocumentsIntWrapper wrapper = new TokenizedDocumentsIntWrapper(
            Arrays.asList(new TokenizedDocument []
            { document1, document2 }));

        ArrayAssert.assertEquals("Stemmed snippets", new int []
        { 0, 1, -1 - MaskableIntWrapper.SECONDARY_OFFSET, 2, 1,
         1 * MaskableIntWrapper.SECONDARY_OFFSET,
         -1 - 2 * MaskableIntWrapper.SECONDARY_OFFSET,
         1 * MaskableIntWrapper.SECONDARY_OFFSET + 1, 0,
         -1 - 3 * MaskableIntWrapper.SECONDARY_OFFSET,
         1 * MaskableIntWrapper.SECONDARY_OFFSET + 2,
         2 * MaskableIntWrapper.SECONDARY_OFFSET,
         3 * MaskableIntWrapper.SECONDARY_OFFSET,
         -1 - 4 * MaskableIntWrapper.SECONDARY_OFFSET, -1 }, wrapper
            .asIntArray());
    }

    /**
     *  
     */
    public void testQueryWords()
    {
        TokenizedDocument document1 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("title titles",
                "titled titles document", "en"));
        TokenizedDocument document2 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("documents title",
                "documented snippet body", "en"));
        TokenizedDocumentsIntWrapper wrapper = new TokenizedDocumentsIntWrapper(
            Arrays.asList(new TokenizedDocument []
            { document1, document2 }), new String []
            { "document", "body" });

        assertTrue("Query word identification", wrapper.isQueryWord(32));
        assertTrue("Query word identification", wrapper.isQueryWord(33));
        assertTrue("Query word identification", wrapper.isQueryWord(96));
        assertFalse("Query word identification", wrapper.isQueryWord(0));
        assertFalse("Query word identification", wrapper.isQueryWord(64));
    }

    /**
     *  
     */
    public void testSegments()
    {
        TokenizedDocument document1 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("title titles",
                "titled titles document", "en"));
        TokenizedDocument document2 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("documents title",
                "documented snippet body", "en"));
        TokenizedDocumentsIntWrapper wrapper = new TokenizedDocumentsIntWrapper(
            Arrays.asList(new TokenizedDocument []
            { document1, document2 }), new String []
            { "document", "body" });

        short [] segments = new short []
        { TokenizedDocumentsIntWrapper.SEGMENT_TITLE,
         TokenizedDocumentsIntWrapper.SEGMENT_TITLE,
         TokenizedDocumentsIntWrapper.SEGMENT_DOCUMENT_DELIMITER,
         TokenizedDocumentsIntWrapper.SEGMENT_SNIPPET,
         TokenizedDocumentsIntWrapper.SEGMENT_SNIPPET,
         TokenizedDocumentsIntWrapper.SEGMENT_SNIPPET,
         TokenizedDocumentsIntWrapper.SEGMENT_DOCUMENT_DELIMITER,
         TokenizedDocumentsIntWrapper.SEGMENT_TITLE,
         TokenizedDocumentsIntWrapper.SEGMENT_TITLE,
         TokenizedDocumentsIntWrapper.SEGMENT_DOCUMENT_DELIMITER,
         TokenizedDocumentsIntWrapper.SEGMENT_SNIPPET,
         TokenizedDocumentsIntWrapper.SEGMENT_SNIPPET,
         TokenizedDocumentsIntWrapper.SEGMENT_SNIPPET,
         TokenizedDocumentsIntWrapper.SEGMENT_DOCUMENT_DELIMITER,
         TokenizedDocumentsIntWrapper.SEGMENT_TERMINATOR };

        for (int i = 0; i < segments.length; i++)
        {
            assertEquals("Segment identification", segments[i], wrapper
                .getSegmentForPosition(i));
        }
    }
}