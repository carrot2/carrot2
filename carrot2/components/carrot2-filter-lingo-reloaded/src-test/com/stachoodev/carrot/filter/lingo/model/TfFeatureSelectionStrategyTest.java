/*
 * DfFeatureSelectionStrategyTest.java Created on 2004-05-16
 */
package com.stachoodev.carrot.filter.lingo.model;

import java.util.*;

import junit.framework.*;

import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.util.tokenizer.*;

/**
 * @author stachoo
 */
public class TfFeatureSelectionStrategyTest extends TestCase
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
    public void testDocumentEmptyList()
    {
        FeatureSelectionStrategy featureSelectionStrategy = new TfFeatureSelectionStrategy();
        assertEquals("Empty selected terms list", 0, featureSelectionStrategy
            .getSelectedFeatures(new ArrayList()).size());
    }

    /**
     *  
     */
    public void testEmptyTitleEmptyBody()
    {
        TokenizedDocument document1 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("", ""));
        List documentList = Arrays.asList(new TokenizedDocument []
        { document1 });

        FeatureSelectionStrategy featureSelectionStrategy = new TfFeatureSelectionStrategy();

        assertEquals("Empty selected terms list", 0, featureSelectionStrategy
            .getSelectedFeatures(documentList).size());
    }

    /**
     *  
     */
    public void testNoTokens()
    {
        TokenizedDocument document1 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("a b", "a d e"));
        List documentList = Arrays.asList(new TokenizedDocument []
        { document1 });

        FeatureSelectionStrategy featureSelectionStrategy = new TfFeatureSelectionStrategy(
            4);
        assertEquals("Empty selected terms list", 0, featureSelectionStrategy
            .getSelectedFeatures(documentList).size());
    }

    /**
     *  
     */
    public void testOneToken()
    {
        TokenizedDocument document1 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("a a", "a c"));
        TokenizedDocument document2 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("e", "a a"));
        List documentList = Arrays.asList(new TokenizedDocument []
        { document1, document2 });

        double titleDfMultiplier = 2.5;
        FeatureSelectionStrategy featureSelectionStrategy = new TfFeatureSelectionStrategy(
            4, titleDfMultiplier);

        // Expected output
        ExtendedToken a = ModelTestUtils.createTokenStem(0,
            ExtendedToken.PROPERTY_TF, "a", 2 * titleDfMultiplier + 3);

        List expectedTokenList = Arrays.asList(new ExtendedToken []
        { a });

        List selectedTokenList = featureSelectionStrategy
            .getSelectedFeatures(documentList);
        assertEquals("One token in selected terms list", expectedTokenList,
            selectedTokenList);
    }

    /**
     *  
     */
    public void testMoreTokens()
    {
        TokenizedDocument document1 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("a b", "e d d d"));
        TokenizedDocument document2 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("a", "e"));
        TokenizedDocument document3 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("c", "e b e"));
        List documentList = Arrays.asList(new TokenizedDocument []
        { document1, document2, document3 });

        double titleTfMultiplier = 2.5;
        FeatureSelectionStrategy featureSelectionStrategy = new TfFeatureSelectionStrategy(
            3, titleTfMultiplier);

        // Expected output
        ExtendedToken a = ModelTestUtils.createTokenStem(0,
            ExtendedToken.PROPERTY_TF, "a", 2 * titleTfMultiplier);
        ExtendedToken e = ModelTestUtils.createTokenStem(1,
            ExtendedToken.PROPERTY_TF, "e", 4);
        ExtendedToken b = ModelTestUtils.createTokenStem(2,
            ExtendedToken.PROPERTY_TF, "b", titleTfMultiplier + 1);
        ExtendedToken d = ModelTestUtils.createTokenStem(3,
            ExtendedToken.PROPERTY_TF, "d", 3);

        List expectedTokenList = Arrays.asList(new ExtendedToken []
        { a, e, b, d });

        List selectedTokenList = featureSelectionStrategy
            .getSelectedFeatures(documentList);

        assertEquals("More tokens in selected terms list", expectedTokenList,
            selectedTokenList);
    }

    /**
     *  
     */
    public void testFiltering()
    {
        TokenizedDocument document1 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("a title of a document",
                "a snippet of a document", "en"));
        TokenizedDocument document2 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("another title of a document",
                "another snippet of a document", "en"));
        TokenizedDocument document3 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("sentence separators",
                "one . two . ! three . . . ", "en"));
        List documentList = Arrays.asList(new TokenizedDocument []
        { document1, document2, document3 });

        FeatureSelectionStrategy featureSelectionStrategy = new TfFeatureSelectionStrategy(
            2, 1);

        // Expected output
        int index = 0;
        ExtendedToken document = ModelTestUtils.createTokenStem(index++,
            ExtendedToken.PROPERTY_TF, "document", 4.0,
            new String []
            { "document" }, new double []
            { 4.0 });
        ExtendedToken titl = ModelTestUtils.createTokenStem(index++,
            ExtendedToken.PROPERTY_TF, "titl", 2.0,
            new String []
            { "title"}, new double []
            { 2.0 });
        ExtendedToken snippet = ModelTestUtils.createTokenStem(index++,
            ExtendedToken.PROPERTY_TF, "snippet", 2.0,
            new String []
            { "snippet" }, new double []
            { 2.0 });

        List expectedTokenList = Arrays.asList(new ExtendedToken []
        { document, titl, snippet});

        List selectedTokenList = featureSelectionStrategy
            .getSelectedFeatures(documentList);

        assertEquals("More tokens in selected terms list", expectedTokenList,
            selectedTokenList);
    }

    /**
     * 
     */
    public void testStemmedTokens()
    {
        TokenizedDocument document1 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("title identical01stem",
                "title c body", "en"));
        TokenizedDocument document2 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("titled",
                "titles identical01stem body", "en"));
        TokenizedDocument document3 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("c", "c identical01stem c", "en"));
        List documentList = Arrays.asList(new TokenizedDocument []
        { document1, document2, document3 });

        double titleDfMultiplier = 2.5;
        FeatureSelectionStrategy featureSelectionStrategy = new TfFeatureSelectionStrategy(
            3, titleDfMultiplier);

        // Expected output
        ExtendedToken titl = ModelTestUtils.createTokenStem(0,
            ExtendedToken.PROPERTY_TF, "titl", 2 * titleDfMultiplier + 2.0,
            new String []
            { "title", "titled", "titles" }, new double []
            { titleDfMultiplier + 1.0, titleDfMultiplier, 1.0 });
        ExtendedToken c = ModelTestUtils.createTokenStem(1,
            ExtendedToken.PROPERTY_TF, "c", titleDfMultiplier + 3,
            new String []
            { "c" }, new double []
            { titleDfMultiplier + 3 });
        ExtendedToken identicalStem = ModelTestUtils.createTokenStem(2,
            ExtendedToken.PROPERTY_TF, "identical01stem",
            titleDfMultiplier + 2, new String []
            { "identical01stem" }, new double []
            { titleDfMultiplier + 2 });

        List expectedTokenList = Arrays.asList(new ExtendedToken []
        { titl, c, identicalStem });

        List selectedTokenList = featureSelectionStrategy
            .getSelectedFeatures(documentList);

        assertEquals("More tokens in selected terms list", expectedTokenList,
            selectedTokenList);
    }
}