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
import com.dawidweiss.carrot.core.local.linguistic.*;
import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
import com.dawidweiss.carrot.util.tokenizer.*;
import com.dawidweiss.carrot.util.tokenizer.languages.polish.*;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class TfFeatureSelectionTest extends TestCase
{
    /** A helper tokenizer factory */
    private SnippetTokenizer snippetTokenizer;

    /** Feature selection under tests */
    private TfFeatureSelection featureSelectionStrategy;

    /** */
    private ModelBuilderContext context;

    /** Polish language to be used */
    private Polish polishLanguage;

    /** Polish LanguageTokenizer */
    private LanguageTokenizer polishTokenizer;

    /**
     *  
     */
    public TfFeatureSelectionTest()
    {
        snippetTokenizer = new SnippetTokenizer();
        featureSelectionStrategy = new TfFeatureSelection();
        context = new ModelBuilderContext();
        polishLanguage = new PolishWithLametyzator();
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        context.clear();
        polishTokenizer = polishLanguage.borrowTokenizer();
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
        polishLanguage.returnTokenizer(polishTokenizer);
    }

    /**
     *  
     */
    public void testDocumentEmptyList()
    {
        context.initialize(Collections.EMPTY_LIST);
        assertEquals("Empty selected terms list", 0, featureSelectionStrategy
            .getSelectedFeatures(context).size());
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

        context.initialize(documentList);
        assertEquals("Empty selected terms list", 0, featureSelectionStrategy
            .getSelectedFeatures(context).size());
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
        context.initialize(documentList);

        featureSelectionStrategy.setDoubleProperty(
            TfFeatureSelection.PROPERTY_TF_THRESHOLD, 4);
        assertEquals("Empty selected terms list", 0, featureSelectionStrategy
            .getSelectedFeatures(context).size());
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
        context.initialize(documentList);

        double titleTfMultiplier = 2.5;
        featureSelectionStrategy.setDoubleProperty(
            TfFeatureSelection.PROPERTY_TF_THRESHOLD, 4);
        featureSelectionStrategy.setDoubleProperty(
            TfFeatureSelection.PROPERTY_TITLE_TF_MULTIPLIER, titleTfMultiplier);

        // Expected output
        ExtendedToken a = ComboModelTestUtils.createTokenStem("aa",
            2 * titleTfMultiplier + 3, 2);

        List expectedTokenList = Arrays.asList(new ExtendedToken []
        { a });

        List selectedTokenList = featureSelectionStrategy
            .getSelectedFeatures(context);
        assertEquals("One token in selected terms list", expectedTokenList,
            selectedTokenList);
    }

    /**
     *  
     */
    public void testAnotherOneToken()
    {
        TokenizedDocument document1 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("ee cc aa", "aa"));
        TokenizedDocument document2 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("aa", "aa aa"));
        List documentList = Arrays.asList(new TokenizedDocument []
        { document1, document2 });
        context.initialize(documentList);

        double titleTfMultiplier = 2.5;
        featureSelectionStrategy.setDoubleProperty(
            TfFeatureSelection.PROPERTY_TF_THRESHOLD, 4);
        featureSelectionStrategy.setDoubleProperty(
            TfFeatureSelection.PROPERTY_TITLE_TF_MULTIPLIER, titleTfMultiplier);

        // Expected output
        ExtendedToken a = ComboModelTestUtils.createTokenStem("aa",
            2 * titleTfMultiplier + 3, 2);

        List expectedTokenList = Arrays.asList(new ExtendedToken []
        { a });

        List selectedTokenList = featureSelectionStrategy
            .getSelectedFeatures(context);
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
        context.initialize(documentList);

        double titleTfMultiplier = 2.5;
        featureSelectionStrategy.setDoubleProperty(
            TfFeatureSelection.PROPERTY_TF_THRESHOLD, 3);
        featureSelectionStrategy.setDoubleProperty(
            TfFeatureSelection.PROPERTY_TITLE_TF_MULTIPLIER, titleTfMultiplier);

        // Expected output
        ExtendedToken a = ComboModelTestUtils.createTokenStem("aa",
            2 * titleTfMultiplier, 2);
        ExtendedToken e = ComboModelTestUtils.createTokenStem("ee", 4, 3);
        ExtendedToken b = ComboModelTestUtils.createTokenStem("bb",
            titleTfMultiplier + 1, 2);
        ExtendedToken d = ComboModelTestUtils.createTokenStem("dd", 3, 1);

        List expectedTokenList = Arrays.asList(new ExtendedToken []
        { a, b, e, d });

        List selectedTokenList = featureSelectionStrategy
            .getSelectedFeatures(context);

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
        context.initialize(documentList);

        featureSelectionStrategy.setDoubleProperty(
            TfFeatureSelection.PROPERTY_TF_THRESHOLD, 2);
        featureSelectionStrategy.setDoubleProperty(
            TfFeatureSelection.PROPERTY_TITLE_TF_MULTIPLIER, 1);

        // Expected output
        ExtendedToken document = ComboModelTestUtils.createTokenStem(
            "document", 4.0, 2.0, "document");
        ExtendedToken titl = ComboModelTestUtils.createTokenStem("titl", 2.0,
            2.0, "title");
        ExtendedToken snippet = ComboModelTestUtils.createTokenStem("snippet",
            2.0, 2.0, "snippet");

        List expectedTokenList = Arrays.asList(new ExtendedToken []
        { titl, document, snippet });

        List selectedTokenList = featureSelectionStrategy
            .getSelectedFeatures(context);

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
            .tokenize(new RawDocumentSnippet("cc", "cc identical01stem cc",
                "en"));
        List documentList = Arrays.asList(new TokenizedDocument []
        { document1, document2, document3 });
        context.initialize(documentList);

        double titleTfMultiplier = 2.5;
        featureSelectionStrategy.setDoubleProperty(
            TfFeatureSelection.PROPERTY_TF_THRESHOLD, 3);
        featureSelectionStrategy.setDoubleProperty(
            TfFeatureSelection.PROPERTY_TITLE_TF_MULTIPLIER, titleTfMultiplier);

        // Expected output
        ExtendedToken titl = ComboModelTestUtils.createTokenStem("titl",
            2 * titleTfMultiplier + 2.0, 2.0, "title");
        ExtendedToken c = ComboModelTestUtils.createTokenStem("cc",
            titleTfMultiplier + 3, 2.0, "cc");
        ExtendedToken identicalStem = ComboModelTestUtils.createTokenStem(
            "identical01stem", titleTfMultiplier + 2, 3.0, "identical01stem");

        List expectedTokenList = Arrays.asList(new ExtendedToken []
        { titl, identicalStem, c });

        List selectedTokenList = featureSelectionStrategy
            .getSelectedFeatures(context);

        assertEquals("More tokens in selected terms list", expectedTokenList,
            selectedTokenList);
    }

    /**
     *  
     */
    public void testOriginalTokens()
    {
        TokenizedDocument document1 = snippetTokenizer.tokenize(
            new RawDocumentSnippet("oślą łączkę", "oślej łączki"),
            polishTokenizer);
        TokenizedDocument document2 = snippetTokenizer.tokenize(
            new RawDocumentSnippet("oślej łączce", "ośla łączka"),
            polishTokenizer);
        List documentList = Arrays.asList(new TokenizedDocument []
        { document1, document2 });
        context.initialize(documentList);

        double titleTfMultiplier = 2.5;
        featureSelectionStrategy.setDoubleProperty(
            TfFeatureSelection.PROPERTY_TF_THRESHOLD, 3);
        featureSelectionStrategy.setDoubleProperty(
            TfFeatureSelection.PROPERTY_TITLE_TF_MULTIPLIER, titleTfMultiplier);

        // Expected output
        ExtendedToken titl = ComboModelTestUtils.createTokenStem("ośli",
            2 * titleTfMultiplier + 2.0, 2.0, "oślej");
        ExtendedToken c = ComboModelTestUtils.createTokenStem("łączka",
            2 * titleTfMultiplier + 2.0, 2.0, "łączkę");

        List expectedTokenList = Arrays.asList(new ExtendedToken []
        { titl, c });

        List selectedTokenList = featureSelectionStrategy
            .getSelectedFeatures(context);

        assertEquals("More tokens in selected terms list", expectedTokenList,
            selectedTokenList);
    }
}