/*
 * DfFeatureSelectionStrategyTest.java Created on 2004-05-16
 */
package com.stachoodev.carrot.filter.lingo.model;

import java.util.*;

import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.util.tokenizer.*;
import com.stachoodev.carrot.filter.lingo.model.DfFeatureSelectionStrategy;
import com.stachoodev.carrot.filter.lingo.model.ExtendedToken;
import com.stachoodev.carrot.filter.lingo.model.FeatureSelectionStrategy;

import junit.framework.*;

/**
 * @author stachoo
 */
public class DfFeatureSelectionStrategyTest extends TestCase
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
        FeatureSelectionStrategy featureSelectionStrategy = new DfFeatureSelectionStrategy();
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

        FeatureSelectionStrategy featureSelectionStrategy = new DfFeatureSelectionStrategy();
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

        FeatureSelectionStrategy featureSelectionStrategy = new DfFeatureSelectionStrategy(
            3);
        assertEquals("Empty selected terms list", 0, featureSelectionStrategy
            .getSelectedFeatures(documentList).size());
    }

    /**
     *  
     */
    public void testOneToken()
    {
        // Input documents
        TokenizedDocument document1 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("a b", "a c"));
        TokenizedDocument document2 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("e e", "a d e"));
        List documentList = Arrays.asList(new TokenizedDocument []
        { document1, document2 });

        // Selection strategy
        double titleDfMultiplier = 2.5;
        FeatureSelectionStrategy featureSelectionStrategy = new DfFeatureSelectionStrategy(
            3, titleDfMultiplier);

        // Expected output
        ExtendedToken a = ModelTestUtils.createTokenStem(0,
            ExtendedToken.PROPERTY_DF, "a", titleDfMultiplier + 1);

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
            .tokenize(new RawDocumentSnippet("a b", "e d"));
        TokenizedDocument document2 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("a", "e d"));
        TokenizedDocument document3 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("c", "e b e"));
        List documentList = Arrays.asList(new TokenizedDocument []
        { document1, document2, document3 });

        double titleDfMultiplier = 2.5;
        FeatureSelectionStrategy featureSelectionStrategy = new DfFeatureSelectionStrategy(
            3, titleDfMultiplier);

        // Expected output
        ExtendedToken a = ModelTestUtils.createTokenStem(0,
            ExtendedToken.PROPERTY_DF, "a", 2 * titleDfMultiplier);
        ExtendedToken b = ModelTestUtils.createTokenStem(1,
            ExtendedToken.PROPERTY_DF, "b", titleDfMultiplier + 1);
        ExtendedToken e = ModelTestUtils.createTokenStem(2,
            ExtendedToken.PROPERTY_DF, "e", 3);

        List expectedTokenList = Arrays.asList(new ExtendedToken []
        { a, b, e });

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

        FeatureSelectionStrategy featureSelectionStrategy = new DfFeatureSelectionStrategy(
            2, 1);

        // Expected output
        ExtendedToken titl = ModelTestUtils.createTokenStem(0,
            ExtendedToken.PROPERTY_DF, "titl", 2.0,
            new String []
            { "title"}, new double []
            { 2.0 });
        ExtendedToken document = ModelTestUtils.createTokenStem(1,
            ExtendedToken.PROPERTY_DF, "document", 2.0,
            new String []
            { "document" }, new double []
            { 2.0 });
        ExtendedToken snippet = ModelTestUtils.createTokenStem(2,
            ExtendedToken.PROPERTY_DF, "snippet", 2.0,
            new String []
            { "snippet" }, new double []
            { 2.0 });

        List expectedTokenList = Arrays.asList(new ExtendedToken []
        { titl, document, snippet});

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
                "c body titles", "en"));
        TokenizedDocument document2 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("titled",
                "identical01stem body bodys bodied titles", "en"));
        TokenizedDocument document3 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("c", "c identical01stem c", "en"));
        List documentList = Arrays.asList(new TokenizedDocument []
        { document1, document2, document3 });

        double titleDfMultiplier = 2.5;
        FeatureSelectionStrategy featureSelectionStrategy = new DfFeatureSelectionStrategy(
            3, titleDfMultiplier);

        // Expected output
        ExtendedToken titl = ModelTestUtils.createTokenStem(0,
            ExtendedToken.PROPERTY_DF, "titl", 2 * titleDfMultiplier,
            new String []
            { "titled", "title", "titles" }, new double []
            { titleDfMultiplier, titleDfMultiplier, 2.0 });
        ExtendedToken identicalStem = ModelTestUtils.createTokenStem(1,
            ExtendedToken.PROPERTY_DF, "identical01stem",
            titleDfMultiplier + 2, new String []
            { "identical01stem" }, new double []
            { titleDfMultiplier + 2 });
        ExtendedToken c = ModelTestUtils.createTokenStem(2,
            ExtendedToken.PROPERTY_DF, "c", titleDfMultiplier + 1,
            new String []
            { "c" }, new double []
            { titleDfMultiplier + 1 });

        List expectedTokenList = Arrays.asList(new ExtendedToken []
        { titl, identicalStem, c });

        List selectedTokenList = featureSelectionStrategy
            .getSelectedFeatures(documentList);

        assertEquals("More tokens in selected terms list", expectedTokenList,
            selectedTokenList);
    }
}