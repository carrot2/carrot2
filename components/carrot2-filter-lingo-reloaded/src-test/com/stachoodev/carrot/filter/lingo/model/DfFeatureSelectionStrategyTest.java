/*
 * DfFeatureSelectionStrategyTest.java Created on 2004-05-16
 */
package com.stachoodev.carrot.filter.lingo.model;

import java.util.*;

import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.util.tokenizer.*;
import com.dawidweiss.carrot.util.tokenizer.parser.*;
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
        ExtendedToken a = createTokenStem("a", titleDfMultiplier + 1);

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
        ExtendedToken a = createTokenStem("a", 2 * titleDfMultiplier);
        ExtendedToken b = createTokenStem("b", titleDfMultiplier + 1);
        ExtendedToken e = createTokenStem("e", 3);

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
        ExtendedToken titl = createTokenStem("titl", 2 * titleDfMultiplier,
            new String []
            { "titled", "title", "titles" }, new double []
            { titleDfMultiplier, titleDfMultiplier, 2.0 });
        ExtendedToken identicalStem = createTokenStem("identical01stem",
            titleDfMultiplier + 2, new String []
            { "identical01stem" }, new double []
            { titleDfMultiplier + 2 });
        ExtendedToken c = createTokenStem("c", titleDfMultiplier + 1,
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

    /**
     * @param stem
     * @param stemDf
     * @param originalTokens
     * @param originalDf
     * @return
     */
    private ExtendedToken createTokenStem(String stem, double stemDf,
        String [] originalTokens, double [] originalDf)
    {
        StringTypedToken token = new StringTypedToken();
        token.assign(stem, (short) 0);
        ExtendedToken extendedTokenStem = new ExtendedToken(
            new TokenStem(token));
        extendedTokenStem.setDoubleProperty(ExtendedToken.PROPERTY_DF, stemDf);

        List originalExtendedTokens = new ArrayList(originalTokens.length);
        for (int i = 0; i < originalTokens.length; i++)
        {
            StringTypedToken originalToken = new StringTypedToken();
            originalToken.assign(originalTokens[i], (short) 0);
            ExtendedToken originalExtendedToken = new ExtendedToken(
                originalToken);
            originalExtendedToken.setDoubleProperty(ExtendedToken.PROPERTY_DF,
                originalDf[i]);
            originalExtendedTokens.add(originalExtendedToken);
        }
        extendedTokenStem.setProperty(ExtendedToken.PROPERTY_ORIGINAL_TOKENS,
            originalExtendedTokens);

        return extendedTokenStem;
    }

    /**
     * @param stem
     * @param stemDf
     * @return
     */
    private ExtendedToken createTokenStem(String stem, double stemDf)
    {
        return createTokenStem(stem, stemDf, new String []
        { stem }, new double []
        { stemDf });
    }
}