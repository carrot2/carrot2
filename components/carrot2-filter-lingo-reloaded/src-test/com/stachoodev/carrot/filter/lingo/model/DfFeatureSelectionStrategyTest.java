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
import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
import com.dawidweiss.carrot.util.tokenizer.*;
import com.stachoodev.carrot.filter.lingo.model.DfFeatureSelectionStrategy;
import com.stachoodev.carrot.filter.lingo.model.FeatureSelectionStrategy;

import junit.framework.*;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
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
            .tokenize(new RawDocumentSnippet("aa bb", "aa dd ee"));
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
            .tokenize(new RawDocumentSnippet("aa bb", "aa cc"));
        TokenizedDocument document2 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("ee ee", "aa dd ee"));
        List documentList = Arrays.asList(new TokenizedDocument []
        { document1, document2 });

        // Selection strategy
        double titleDfMultiplier = 2.5;
        FeatureSelectionStrategy featureSelectionStrategy = new DfFeatureSelectionStrategy(
            3, titleDfMultiplier);

        // Expected output
        ExtendedToken a = ModelTestUtils.createTokenStem(0,
            ExtendedToken.PROPERTY_DF, "aa", titleDfMultiplier + 1);

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
            .tokenize(new RawDocumentSnippet("aa bb", "ee dd"));
        TokenizedDocument document2 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("aa", "ee dd"));
        TokenizedDocument document3 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("cc", "ee bb ee"));
        List documentList = Arrays.asList(new TokenizedDocument []
        { document1, document2, document3 });

        double titleDfMultiplier = 2.5;
        FeatureSelectionStrategy featureSelectionStrategy = new DfFeatureSelectionStrategy(
            3, titleDfMultiplier);

        // Expected output
        ExtendedToken a = ModelTestUtils.createTokenStem(0,
            ExtendedToken.PROPERTY_DF, "aa", 2 * titleDfMultiplier);
        ExtendedToken b = ModelTestUtils.createTokenStem(1,
            ExtendedToken.PROPERTY_DF, "bb", titleDfMultiplier + 1);
        ExtendedToken e = ModelTestUtils.createTokenStem(2,
            ExtendedToken.PROPERTY_DF, "ee", 3);

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
                "cc body titles", "en"));
        TokenizedDocument document2 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("titled",
                "identical01stem body bodys bodied titles", "en"));
        TokenizedDocument document3 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("cc", "cc identical01stem cc", "en"));
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
            ExtendedToken.PROPERTY_DF, "cc", titleDfMultiplier + 1,
            new String []
            { "cc" }, new double []
            { titleDfMultiplier + 1 });

        List expectedTokenList = Arrays.asList(new ExtendedToken []
        { titl, identicalStem, c });

        List selectedTokenList = featureSelectionStrategy
            .getSelectedFeatures(documentList);

        assertEquals("More tokens in selected terms list", expectedTokenList,
            selectedTokenList);
    }
}