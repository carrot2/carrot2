/*
 * TokenizedDocumentsIntWrapperTest.java Created on 2004-06-15
 */
package com.stachoodev.carrot.filter.lingo.model;

import java.util.*;

import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.util.tokenizer.*;
import com.stachoodev.suffixarrays.wrapper.*;

import junit.framework.*;
import junitx.framework.*;

/**
 * @author stachoo
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
                Arrays.asList(new TokenizedDocument[]
                { document }));

        ArrayAssert.assertEquals("Empty int array", new int[]
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
                Arrays.asList(new TokenizedDocument[]
                { document }));

        ArrayAssert
                .assertEquals(
                        "Single snippet with empty body int array",
                        new int[]
                        {
                         0,
                         1 * MaskableIntWrapper.SECONDARY_OFFSET,
                         2 * MaskableIntWrapper.SECONDARY_OFFSET,
                         -1 - MaskableIntWrapper.SECONDARY_OFFSET,
                         -1 }, wrapper.asIntArray());
    }

    /**
     * 
     */
    public void testSingleSnippetWithEmptyTitle()
    {
        TokenizedDocument document = snippetTokenizer
                .tokenize(new RawDocumentSnippet("", "This time only a snippet"));
        TokenizedDocumentsIntWrapper wrapper = new TokenizedDocumentsIntWrapper(
                Arrays.asList(new TokenizedDocument[]
                { document }));

        ArrayAssert
                .assertEquals(
                        "Single snippet with empty title int array",
                        new int[]
                        {
                         0,
                         1 * MaskableIntWrapper.SECONDARY_OFFSET,
                         2 * MaskableIntWrapper.SECONDARY_OFFSET,
                         3 * MaskableIntWrapper.SECONDARY_OFFSET,
                         4 * MaskableIntWrapper.SECONDARY_OFFSET,
                         -1 - MaskableIntWrapper.SECONDARY_OFFSET,
                         -1 }, wrapper.asIntArray());
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
                Arrays.asList(new TokenizedDocument[]
                { document }));

        ArrayAssert
                .assertEquals(
                        "Single snippet with repeated tokens",
                        new int[]
                        {
                         0 * MaskableIntWrapper.SECONDARY_OFFSET,
                         1 * MaskableIntWrapper.SECONDARY_OFFSET,
                         2 * MaskableIntWrapper.SECONDARY_OFFSET,
                         3 * MaskableIntWrapper.SECONDARY_OFFSET,
                         -1 - MaskableIntWrapper.SECONDARY_OFFSET,
                         4 * MaskableIntWrapper.SECONDARY_OFFSET,
                         1 * MaskableIntWrapper.SECONDARY_OFFSET,
                         2 * MaskableIntWrapper.SECONDARY_OFFSET,
                         3 * MaskableIntWrapper.SECONDARY_OFFSET,
                         -1 - 2 * MaskableIntWrapper.SECONDARY_OFFSET,
                         -1 }, wrapper.asIntArray());
    }

    /**
     * 
     */
    public void testRepeatedSentenceDelimiters()
    {
        TokenizedDocument document = snippetTokenizer
                .tokenize(new RawDocumentSnippet("title. simply title",
                        "body, body, body, body"));
        TokenizedDocumentsIntWrapper wrapper = new TokenizedDocumentsIntWrapper(
                Arrays.asList(new TokenizedDocument[]
                { document }));

        ArrayAssert
                .assertEquals(
                        "Single snippet with repeated sentence delimiters",
                        new int[]
                        {
                         0 * MaskableIntWrapper.SECONDARY_OFFSET,
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
                         -1 - 6 * MaskableIntWrapper.SECONDARY_OFFSET,
                         -1 }, wrapper.asIntArray());
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
                Arrays.asList(new TokenizedDocument[]
                { document1, document2 }));

        ArrayAssert.assertEquals("Two snippets", new int[]
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
         -1 - 4 * MaskableIntWrapper.SECONDARY_OFFSET, -1 },
                wrapper.asIntArray());
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
                Arrays.asList(new TokenizedDocument[]
                { document1, document2 }));

        ArrayAssert.assertEquals("Stemmed snippets", new int[]
        { 0, 1,
         -1 - MaskableIntWrapper.SECONDARY_OFFSET, 2, 1,
         1 * MaskableIntWrapper.SECONDARY_OFFSET,
         -1 - 2 * MaskableIntWrapper.SECONDARY_OFFSET, 
         1 * MaskableIntWrapper.SECONDARY_OFFSET + 1,
         0,
         -1 - 3 * MaskableIntWrapper.SECONDARY_OFFSET, 
         1 * MaskableIntWrapper.SECONDARY_OFFSET + 2,
         2 * MaskableIntWrapper.SECONDARY_OFFSET,
         3 * MaskableIntWrapper.SECONDARY_OFFSET,
         -1 - 4 * MaskableIntWrapper.SECONDARY_OFFSET, 
         -1 },
                wrapper.asIntArray());
    }
}