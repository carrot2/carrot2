/*
 * DfFeatureSelectionStrategyTest.java Created on 2004-05-16
 */
package com.stachoodev.carrot.filter.lingo.model;

import java.util.*;

import junit.framework.*;

import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.util.tokenizer.*;
import com.dawidweiss.carrot.util.tokenizer.parser.*;

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
        ExtendedToken a = createTokenStem("a", 2 * titleDfMultiplier + 3);

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

        double titleDfMultiplier = 2.5;
        FeatureSelectionStrategy featureSelectionStrategy = new TfFeatureSelectionStrategy(
            3, titleDfMultiplier);

        // Expected output
        ExtendedToken a = createTokenStem("a", 2 * titleDfMultiplier);
        ExtendedToken e = createTokenStem("e", 4);
        ExtendedToken b = createTokenStem("b", titleDfMultiplier + 1);
        ExtendedToken d = createTokenStem("d", 3);

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
        ExtendedToken titl = createTokenStem("titl",
            2 * titleDfMultiplier + 2.0, new String []
            { "title", "titled", "titles" }, new double []
            { titleDfMultiplier + 1.0, titleDfMultiplier, 1.0 });
        ExtendedToken identicalStem = createTokenStem("identical01stem",
            titleDfMultiplier + 2, new String []
            { "identical01stem" }, new double []
            { titleDfMultiplier + 2 });
        ExtendedToken c = createTokenStem("c", titleDfMultiplier + 3,
            new String []
            { "c" }, new double []
            { titleDfMultiplier + 3 });

        List expectedTokenList = Arrays.asList(new ExtendedToken []
        { titl, c, identicalStem });

        List selectedTokenList = featureSelectionStrategy
            .getSelectedFeatures(documentList);

        assertEquals("More tokens in selected terms list", expectedTokenList,
            selectedTokenList);
    }

    /**
     * @param stem
     * @param stemTf
     * @param originalTokens
     * @param originalTf
     * @return
     */
    private ExtendedToken createTokenStem(String stem, double stemTf,
        String [] originalTokens, double [] originalTf)
    {
        StringTypedToken token = new StringTypedToken();
        token.assign(stem, (short) 0);
        ExtendedToken extendedTokenStem = new ExtendedToken(
            new TokenStem(token));
        extendedTokenStem.setDoubleProperty(ExtendedToken.PROPERTY_TF, stemTf);

        List originalExtendedTokens = new ArrayList(originalTokens.length);
        for (int i = 0; i < originalTokens.length; i++)
        {
            StringTypedToken originalToken = new StringTypedToken();
            originalToken.assign(originalTokens[i], (short) 0);
            ExtendedToken originalExtendedToken = new ExtendedToken(
                originalToken);
            originalExtendedToken.setDoubleProperty(ExtendedToken.PROPERTY_TF,
                originalTf[i]);
            originalExtendedTokens.add(originalExtendedToken);
        }
        extendedTokenStem.setProperty(ExtendedToken.PROPERTY_ORIGINAL_TOKENS,
            originalExtendedTokens);

        return extendedTokenStem;
    }

    /**
     * @param stem
     * @param stemTf
     * @return
     */
    private ExtendedToken createTokenStem(String stem, double stemTf)
    {
        return createTokenStem(stem, stemTf, new String []
        { stem }, new double []
        { stemTf });
    }
}