/*
 * SnippetTokenizerTest.java Created on 2004-06-15
 */
package com.dawidweiss.carrot.util.tokenizer;

import com.dawidweiss.carrot.core.local.clustering.*;

import junit.framework.*;

/**
 * A simple SnippetTokenizer test.
 * 
 * @author stachoo
 */
public class SnippetTokenizerTest extends TestCase
{
    /** A helper tokenizer */
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
    public void testEmpty()
    {
        TokenizedDocument document = snippetTokenizer
            .tokenize(new RawDocumentSnippet("", ""));
        assertEquals("Zero-length title", 0, document.getTitle().getLength());
        assertEquals("Zero-length snippet", 0, document.getSnippet()
            .getLength());
    }

    /**
     * 
     */
    public void testNonEmpty()
    {
        TokenizedDocument document = snippetTokenizer
            .tokenize(new RawDocumentSnippet("a simple test",
                "nothing fancy. really"));

        // Note we're not testing the parser, just the factory
        assertEquals("Non-zero-length title", 3, document.getTitle()
            .getLength());
        assertEquals("Non-zero-length snippet", 4, document.getSnippet()
            .getLength());
    }

    /**
     * 
     */
    public void testPropertyCopying()
    {
        TokenizedDocument document = snippetTokenizer
            .tokenize(new RawDocumentSnippet("a simple test",
                "nothing fancy. really", "en"));

        assertEquals("Copied property", "en", document
            .getProperty(RawDocument.PROPERTY_LANGUAGE));
    }
}