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
package com.dawidweiss.carrot.util.tokenizer;

import com.dawidweiss.carrot.core.local.clustering.*;

import junit.framework.*;

/**
 * A simple SnippetTokenizer test.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class SnippetTokenizerTest extends TestCase
{
    /** A helper tokenizer */
    private SnippetTokenizer snippetTokenizer = new SnippetTokenizer();

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
        snippetTokenizer.clear();
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