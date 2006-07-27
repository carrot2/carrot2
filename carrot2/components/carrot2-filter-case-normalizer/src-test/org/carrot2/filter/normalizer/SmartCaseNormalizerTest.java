
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

package org.carrot2.filter.normalizer;

import org.carrot2.filter.normalizer.SmartCaseNormalizer;

import junit.framework.*;

import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.linguistic.*;
import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
import com.dawidweiss.carrot.util.tokenizer.*;
import com.dawidweiss.carrot.util.tokenizer.languages.*;

/**
 * Unit tests for the
 * {@link org.carrot2.filter.normalizer.SmartCaseNormalizer}class.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class SmartCaseNormalizerTest extends TestCase
{
    /** A helper tokenized document factory */
    private SnippetTokenizer snippetTokenizer;

    /** The case normalizer under tests */
    private SmartCaseNormalizer caseNormalizer;

    /** Polish LanguageTokenizer */
    private LanguageTokenizer polishTokenizer;

    /**
     * 
     */
    public SmartCaseNormalizerTest()
    {
        snippetTokenizer = new SnippetTokenizer();
        caseNormalizer = new SmartCaseNormalizer();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        polishTokenizer = AllKnownLanguages.getLanguageForIsoCode("pl").borrowTokenizer();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
        AllKnownLanguages.getLanguageForIsoCode("pl").returnTokenizer(polishTokenizer);
    }
    
    /**
     *  
     */
    public void testCapitalizationEn()
    {
        TokenizedDocument document = snippetTokenizer
            .tokenize(new RawDocumentSnippet("A simple title",
                "a Simple snippet Of THE document", "en"));
        TokenizedDocument normalizedDocument = snippetTokenizer
            .tokenize(new RawDocumentSnippet("a Simple Title",
                "a Simple Snippet of the Document", "en"));

        // Clear raw document references, which would break equality
        document.setProperty(TokenizedDocument.PROPERTY_RAW_DOCUMENT, null);
        normalizedDocument.setProperty(TokenizedDocument.PROPERTY_RAW_DOCUMENT,
            null);

        caseNormalizer.addDocument(document);
        caseNormalizer.getNormalizedDocuments();
        assertEquals("Correct capitalization", normalizedDocument, document);
    }
    
    /**
     *  
     */
    public void testStemPreserving()
    {
        TokenizedDocument document = snippetTokenizer
            .tokenize(new RawDocumentSnippet("Dancing Dance DANCES",
                "", "en"));

        // Clear raw document references, which would break equality
        document.setProperty(TokenizedDocument.PROPERTY_RAW_DOCUMENT, null);

        caseNormalizer.addDocument(document);
        caseNormalizer.getNormalizedDocuments();
        
        assertNotNull("Stem preserved", ((StemmedToken) document.getTitle()
            .getTokenAt(0)).getStem());
        assertEquals("Stem preserved", ((StemmedToken) document.getTitle()
            .getTokenAt(0)).getStem().toString(), "danc");
        assertNotNull("Stem preserved", ((StemmedToken) document.getTitle()
            .getTokenAt(1)).getStem());
        assertEquals("Stem preserved", ((StemmedToken) document.getTitle()
            .getTokenAt(1)).getStem().toString(), "danc");
        assertNotNull("Stem preserved", ((StemmedToken) document.getTitle()
            .getTokenAt(2)).getStem());
        assertEquals("Stem preserved", ((StemmedToken) document.getTitle()
            .getTokenAt(2)).getStem().toString(), "danc");
    }
    
    /**
     *  
     */
    public void testUnknownLanguage()
    {
        TokenizedDocument document01 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("A simple title",
                "a Simple snippet Of THE document"));
        TokenizedDocument document02 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("A simple title",
                "a Simple snippet Of THE document"));
        TokenizedDocument normalizedDocument01 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("A Simple Title",
                "A Simple Snippet Of THE Document"));
        TokenizedDocument normalizedDocument02 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("A Simple Title",
                "A Simple Snippet Of THE Document"));

        // Clear raw document references, which would break equality
        document01.setProperty(TokenizedDocument.PROPERTY_RAW_DOCUMENT, null);
        document01.setProperty(TokenizedDocument.PROPERTY_LANGUAGE, null);
        normalizedDocument01.setProperty(
            TokenizedDocument.PROPERTY_RAW_DOCUMENT, null);
        document02.setProperty(TokenizedDocument.PROPERTY_RAW_DOCUMENT, null);
        document02.setProperty(TokenizedDocument.PROPERTY_LANGUAGE, null);
        normalizedDocument02.setProperty(
            TokenizedDocument.PROPERTY_RAW_DOCUMENT, null);

        caseNormalizer.addDocument(document01);
        caseNormalizer.addDocument(document02);
        caseNormalizer.getNormalizedDocuments();
        assertEquals("Correct capitalization", normalizedDocument01, document01);
        assertEquals("Correct capitalization", normalizedDocument02, document02);
    }

    /**
     *  
     */
    public void testCapitalizationPl()
    {
        TokenizedDocument document = snippetTokenizer
            .tokenize(new RawDocumentSnippet("Prosty tytuł",
                "Ale mały wycineczek Z dokumentu"), polishTokenizer);
        TokenizedDocument normalizedDocument = snippetTokenizer
            .tokenize(new RawDocumentSnippet("Prosty Tytuł",
                "ale Mały Wycineczek z Dokumentu"), polishTokenizer);

        // Clear raw document references, which would break equality
        document.setProperty(TokenizedDocument.PROPERTY_RAW_DOCUMENT, null);
        normalizedDocument.setProperty(TokenizedDocument.PROPERTY_RAW_DOCUMENT,
            null);

        caseNormalizer.addDocument(document);
        caseNormalizer.getNormalizedDocuments();
        assertEquals("Correct capitalization", normalizedDocument, document);
    }

    /**
     *  
     */
    public void testCapitalizationEnPl()
    {
        TokenizedDocument document01 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("Prosty on tytuł",
                "Ale mały wycineczek Z dokumentu for"), polishTokenizer);
        TokenizedDocument document02 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("Title",
                "John likes ale on rocks for", "en"));
        TokenizedDocument normalizedDocument01 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("Prosty on Tytuł",
                "ale Mały Wycineczek z Dokumentu For"), polishTokenizer);
        TokenizedDocument normalizedDocument02 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("Title",
                "John Likes Ale on Rocks for", "en"));

        // Clear raw document references, which would break equality
        document01.setProperty(TokenizedDocument.PROPERTY_RAW_DOCUMENT, null);
        normalizedDocument01.setProperty(
            TokenizedDocument.PROPERTY_RAW_DOCUMENT, null);
        document02.setProperty(TokenizedDocument.PROPERTY_RAW_DOCUMENT, null);
        normalizedDocument02.setProperty(
            TokenizedDocument.PROPERTY_RAW_DOCUMENT, null);

        caseNormalizer.addDocument(document01);
        caseNormalizer.addDocument(document02);
        caseNormalizer.getNormalizedDocuments();
        assertEquals("Correct capitalization", normalizedDocument01, document01);
        assertEquals("Correct capitalization", normalizedDocument02, document02);
    }

    /**
     *  
     */
    public void testAcronyms()
    {
        TokenizedDocument document = snippetTokenizer
            .tokenize(new RawDocumentSnippet("MySQL database",
                "MySQL query . on mysql website", "en"));
        TokenizedDocument normalizedDocument = snippetTokenizer
            .tokenize(new RawDocumentSnippet("MySQL Database",
                "MySQL Query . on MySQL Website", "en"));

        // Clear raw document references, which would break equality
        document.setProperty(TokenizedDocument.PROPERTY_RAW_DOCUMENT, null);
        normalizedDocument.setProperty(TokenizedDocument.PROPERTY_RAW_DOCUMENT,
            null);

        caseNormalizer.addDocument(document);
        caseNormalizer.getNormalizedDocuments();
        assertEquals("Correct capitalization", normalizedDocument, document);
    }

    /**
     *  
     */
    public void testRepeatedCalls()
    {
        TokenizedDocument document01 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("Prosty on tytuł",
                "Ale mały wycineczek Z dokumentu"), polishTokenizer);
        TokenizedDocument normalizedDocument01 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("Prosty on Tytuł",
                "ale Mały Wycineczek z Dokumentu"), polishTokenizer);

        // Clear raw document references, which would break equality
        document01.setProperty(TokenizedDocument.PROPERTY_RAW_DOCUMENT, null);
        normalizedDocument01.setProperty(
            TokenizedDocument.PROPERTY_RAW_DOCUMENT, null);

        caseNormalizer.addDocument(document01);
        caseNormalizer.getNormalizedDocuments();
        assertEquals("Correct capitalization", normalizedDocument01, document01);
        caseNormalizer.clear();

        TokenizedDocument document02 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("Title",
                "John likes ale on rocks", "en"));
        TokenizedDocument normalizedDocument02 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("Title",
                "John Likes Ale on Rocks", "en"));

        // Clear raw document references, which would break equality
        document02.setProperty(TokenizedDocument.PROPERTY_RAW_DOCUMENT, null);
        normalizedDocument02.setProperty(
            TokenizedDocument.PROPERTY_RAW_DOCUMENT, null);

        caseNormalizer.addDocument(document02);
        caseNormalizer.getNormalizedDocuments();
        assertEquals("Correct capitalization", normalizedDocument02, document02);
    }
}