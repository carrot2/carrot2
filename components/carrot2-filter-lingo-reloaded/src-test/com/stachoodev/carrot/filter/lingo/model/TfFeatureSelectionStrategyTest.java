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

import junit.framework.*;

import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
import com.dawidweiss.carrot.util.tokenizer.*;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
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
            .tokenize(new RawDocumentSnippet("aa aa", "aa cc"));
        TokenizedDocument document2 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("ee", "aa aa"));
        List documentList = Arrays.asList(new TokenizedDocument []
        { document1, document2 });

        double titleDfMultiplier = 2.5;
        FeatureSelectionStrategy featureSelectionStrategy = new TfFeatureSelectionStrategy(
            4, titleDfMultiplier);

        // Expected output
        ExtendedToken a = ModelTestUtils.createTokenStem(0,
            ExtendedToken.PROPERTY_TF, "aa", 2 * titleDfMultiplier + 3);

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
            .tokenize(new RawDocumentSnippet("aa bb", "ee dd dd dd"));
        TokenizedDocument document2 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("aa", "ee"));
        TokenizedDocument document3 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("cc", "ee bb ee"));
        List documentList = Arrays.asList(new TokenizedDocument []
        { document1, document2, document3 });

        double titleTfMultiplier = 2.5;
        FeatureSelectionStrategy featureSelectionStrategy = new TfFeatureSelectionStrategy(
            3, titleTfMultiplier);

        // Expected output
        ExtendedToken a = ModelTestUtils.createTokenStem(0,
            ExtendedToken.PROPERTY_TF, "aa", 2 * titleTfMultiplier);
        ExtendedToken e = ModelTestUtils.createTokenStem(1,
            ExtendedToken.PROPERTY_TF, "ee", 4);
        ExtendedToken b = ModelTestUtils.createTokenStem(2,
            ExtendedToken.PROPERTY_TF, "bb", titleTfMultiplier + 1);
        ExtendedToken d = ModelTestUtils.createTokenStem(3,
            ExtendedToken.PROPERTY_TF, "dd", 3);

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
                "title cc body", "en"));
        TokenizedDocument document2 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("titled",
                "titles identical01stem body", "en"));
        TokenizedDocument document3 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("cc", "cc identical01stem cc", "en"));
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
            ExtendedToken.PROPERTY_TF, "cc", titleDfMultiplier + 3,
            new String []
            { "cc" }, new double []
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