/*
 * DfFeatureSelectionStrategyTest.java Created on 2004-05-16
 */
package com.stachoodev.carrot.filter.lingo.model;

import java.util.*;

import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
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
        List documentList = Arrays.asList(new TokenizedDocument[]
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
        List documentList = Arrays.asList(new TokenizedDocument[]
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
        TokenizedDocument document1 = snippetTokenizer
                .tokenize(new RawDocumentSnippet("a b", "a c"));
        TokenizedDocument document2 = snippetTokenizer
                .tokenize(new RawDocumentSnippet("e e", "a d e"));
        List documentList = Arrays.asList(new TokenizedDocument[]
        { document1, document2 });

        double titleDfMultiplier = 2.5;
        FeatureSelectionStrategy featureSelectionStrategy = new DfFeatureSelectionStrategy(
                3, titleDfMultiplier);

        StringTypedToken A = new StringTypedToken();
        A.assign("a", TypedToken.TOKEN_TYPE_TERM);
        ExtendedToken tokenA = new ExtendedToken(new TokenStem(A));
        tokenA.setDoubleProperty(ExtendedToken.PROPERTY_DF,
                titleDfMultiplier + 1);

        List tokenList = Arrays.asList(new ExtendedToken[]
        { tokenA });

        List selectedFeatures = featureSelectionStrategy
                .getSelectedFeatures(documentList);
        assertEquals("One token in selected terms list", tokenList,
                selectedFeatures);
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
        List documentList = Arrays.asList(new TokenizedDocument[]
        { document1, document2, document3 });

        double titleDfMultiplier = 2.5;
        FeatureSelectionStrategy featureSelectionStrategy = new DfFeatureSelectionStrategy(
                3, titleDfMultiplier);

        StringTypedToken A = new StringTypedToken();
        A.assign("a", TypedToken.TOKEN_TYPE_TERM);
        ExtendedToken tokenA = new ExtendedToken(new TokenStem(A));
        tokenA.setDoubleProperty(ExtendedToken.PROPERTY_DF,
                2 * titleDfMultiplier);

        StringTypedToken B = new StringTypedToken();
        B.assign("b", TypedToken.TOKEN_TYPE_TERM);
        ExtendedToken tokenB = new ExtendedToken(new TokenStem(B));
        tokenB.setDoubleProperty(ExtendedToken.PROPERTY_DF,
                titleDfMultiplier + 1);

        StringTypedToken E = new StringTypedToken();
        E.assign("e", TypedToken.TOKEN_TYPE_TERM);
        ExtendedToken tokenE = new ExtendedToken(new TokenStem(E));
        tokenE.setDoubleProperty(ExtendedToken.PROPERTY_DF, 3);

        List tokenList = Arrays.asList(new ExtendedToken[]
        { tokenA, tokenB, tokenE });

        List selectedFeatures = featureSelectionStrategy
                .getSelectedFeatures(documentList);
        assertEquals("More tokens in selected terms list", tokenList,
                selectedFeatures);
    }

    /**
     * 
     */
    public void testStemmedTokens()
    {
        TokenizedDocument document1 = snippetTokenizer
                .tokenize(new RawDocumentSnippet("title identical01stem",
                        "c body", "en"));
        TokenizedDocument document2 = snippetTokenizer
                .tokenize(new RawDocumentSnippet("titled",
                        "identical01stem body bodys bodied", "en"));
        TokenizedDocument document3 = snippetTokenizer
                .tokenize(new RawDocumentSnippet("c", "c identical01stem c",
                        "en"));
        List documentList = Arrays.asList(new TokenizedDocument[]
        { document1, document2, document3 });

        double titleDfMultiplier = 2.5;
        FeatureSelectionStrategy featureSelectionStrategy = new DfFeatureSelectionStrategy(
                3, titleDfMultiplier);

        StringTypedToken A = new StringTypedToken();
        A.assign("titl", TypedToken.TOKEN_TYPE_TERM);
        ExtendedToken tokenA = new ExtendedToken(new TokenStem(A));
        tokenA.setDoubleProperty(ExtendedToken.PROPERTY_DF,
                2 * titleDfMultiplier);

        StringTypedToken B = new StringTypedToken();
        B.assign("identical01stem", TypedToken.TOKEN_TYPE_TERM);
        ExtendedToken tokenB = new ExtendedToken(new TokenStem(B));
        tokenB.setDoubleProperty(ExtendedToken.PROPERTY_DF,
                titleDfMultiplier + 2);

        StringTypedToken E = new StringTypedToken();
        E.assign("c", TypedToken.TOKEN_TYPE_TERM);
        ExtendedToken tokenE = new ExtendedToken(new TokenStem(E));
        tokenE.setDoubleProperty(ExtendedToken.PROPERTY_DF,
                titleDfMultiplier + 1);

        List tokenList = Arrays.asList(new ExtendedToken[]
        { tokenA, tokenB, tokenE });

        List selectedFeatures = featureSelectionStrategy
                .getSelectedFeatures(documentList);
        assertEquals("More tokens in selected terms list", tokenList,
                selectedFeatures);
    }
}