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
import com.dawidweiss.carrot.util.tokenizer.parser.*;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class SAPhraseExtractionTest extends TestCase
{
    /** PhraseExtractionStrategy under tests */
    private SAPhraseExtraction phraseExtractionStrategy;

    /** */
    private ModelBuilderContext context;

    /** A helper tokenized document factory */
    private SnippetTokenizer snippetTokenizer;

    /** Polish language to be used */
    private Polish polishLanguage;

    /** Polish LanguageTokenizer */
    private LanguageTokenizer polishTokenizer;

    /**
     *  
     */
    public SAPhraseExtractionTest()
    {
        phraseExtractionStrategy = new SAPhraseExtraction();
        context = new ModelBuilderContext();
        snippetTokenizer = new SnippetTokenizer();
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
        phraseExtractionStrategy.clear();
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
        // TODO Auto-generated method stub
        super.tearDown();
        polishLanguage.returnTokenizer(polishTokenizer);
    }

    /**
     *  
     */
    public void testEmptyInputData()
    {
        context.initialize(new ArrayList());
        assertEquals("Zero-length phrase list", 0, phraseExtractionStrategy
            .getExtractedPhrases(context).size());
    }

    /**
     *  
     */
    public void testNoFrequentPhrases()
    {
        TokenizedDocument document01 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("z b c", "d e f"));
        TokenizedDocument document02 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("g h i", "j k l"));

        context.initialize(new ArrayList(Arrays.asList(new TokenizedDocument []
        { document01, document02 })));
        assertEquals("Zero-length phrase list", 0, phraseExtractionStrategy
            .getExtractedPhrases(context).size());
    }

    /**
     *  
     */
    public void testOneFrequentPhrase()
    {
        TokenizedDocument document01 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("z b c", "d e f"));
        TokenizedDocument document02 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("z b i", "j k l"));

        StringTypedToken tokenA = new StringTypedToken();
        tokenA.assign("z", (short) 0);
        StringTypedToken tokenB = new StringTypedToken();
        tokenB.assign("b", (short) 0);

        TokenSequence tokenSequence = new MutableTokenSequence(
            new com.dawidweiss.carrot.core.local.linguistic.tokens.Token []
            { tokenA, tokenB });

        List documentList = Arrays.asList(new TokenizedDocument []
        { document01, document02 });

        context.initialize(documentList);
        List tokenSequenceList = phraseExtractionStrategy
            .getExtractedPhrases(context);

        assertEquals("One frequent phrase token sequence", Arrays
            .asList(new TokenSequence []
            { tokenSequence }), tokenSequenceList);
        assertEquals("One frequent phrase frequency", 2.0,
            ((ExtendedTokenSequence) tokenSequenceList.get(0))
                .getDoubleProperty(ExtendedTokenSequence.PROPERTY_TF, 0), 0);
    }

    /**
     *  
     */
    public void testMoreFrequentPhrases()
    {
        TokenizedDocument document01 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("a b c", "d e f"));
        TokenizedDocument document02 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("a b i", "j q r s l"));
        TokenizedDocument document03 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("a b z q r s", "j k l"));

        StringTypedToken tokenA = new StringTypedToken();
        tokenA.assign("a", (short) 0);
        StringTypedToken tokenB = new StringTypedToken();
        tokenB.assign("b", (short) 0);
        StringTypedToken tokenQ = new StringTypedToken();
        tokenQ.assign("q", (short) 0);
        StringTypedToken tokenR = new StringTypedToken();
        tokenR.assign("r", (short) 0);
        StringTypedToken tokenS = new StringTypedToken();
        tokenS.assign("s", (short) 0);

        TokenSequence tokenSequenceAB = new MutableTokenSequence(
            new com.dawidweiss.carrot.core.local.linguistic.tokens.Token []
            { tokenA, tokenB });

        TokenSequence tokenSequenceQRS = new MutableTokenSequence(
            new com.dawidweiss.carrot.core.local.linguistic.tokens.Token []
            { tokenQ, tokenR, tokenS });

        List documentList = Arrays.asList(new TokenizedDocument []
        { document01, document02, document03 });

        context.initialize(documentList);
        List tokenSequenceList = phraseExtractionStrategy
            .getExtractedPhrases(context);

        assertEquals("More frequent phrases token sequences", Arrays
            .asList(new TokenSequence []
            { tokenSequenceAB, tokenSequenceQRS }), tokenSequenceList);
        assertEquals("One frequent phrase frequency", 3.0,
            ((ExtendedTokenSequence) tokenSequenceList.get(0))
                .getDoubleProperty(ExtendedTokenSequence.PROPERTY_TF, 0), 0);
        assertEquals("One frequent phrase frequency", 2.0,
            ((ExtendedTokenSequence) tokenSequenceList.get(1))
                .getDoubleProperty(ExtendedTokenSequence.PROPERTY_TF, 0), 0);
    }

    /**
     *  
     */
    public void testSimpleStopWordStripping()
    {
        TokenizedDocument document01 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("a nice snippet title",
                "a nice snippet title", "en"));
        TokenizedDocument document02 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("a nice snippet title", "", "en"));

        StringTypedToken tokenNice = new StringTypedToken();
        tokenNice.assign("nice", (short) 0);
        StringTypedToken tokenTitle = new StringTypedToken();
        tokenTitle.assign("titl", (short) 0);
        StringTypedToken tokenBody = new StringTypedToken();
        tokenBody.assign("snippet", (short) 0);

        TokenSequence tokenSequenceNiceTitle = new MutableTokenSequence(
            new com.dawidweiss.carrot.core.local.linguistic.tokens.Token []
            { tokenNice, tokenBody, tokenTitle });

        List documentList = Arrays.asList(new TokenizedDocument []
        { document01, document02 });

        context.initialize(documentList);
        List tokenSequenceList = phraseExtractionStrategy
            .getExtractedPhrases(context);

        assertEquals("Stop word stripping phrases", Arrays
            .asList(new TokenSequence []
            { tokenSequenceNiceTitle }), tokenSequenceList);
        assertEquals("Stop word stripping frequency", 3.0,
            ((ExtendedTokenSequence) tokenSequenceList.get(0))
                .getDoubleProperty(ExtendedTokenSequence.PROPERTY_TF, 0), 0);
    }

    /**
     *  
     */
    public void testStopWordStripping()
    {
        TokenizedDocument document01 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("and this is a nice title of the",
                "a nice snippet", "en"));
        TokenizedDocument document02 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("and this is a nice title of the",
                "the nice body", "en"));
        TokenizedDocument document03 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("some nice title",
                "really nice body", "en"));

        StringTypedToken tokenNice = new StringTypedToken();
        tokenNice.assign("nice", (short) 0);
        StringTypedToken tokenTitle = new StringTypedToken();
        tokenTitle.assign("titl", (short) 0);
        StringTypedToken tokenBody = new StringTypedToken();
        tokenBody.assign("bodi", (short) 0);

        TokenSequence tokenSequenceNiceTitle = new MutableTokenSequence(
            new com.dawidweiss.carrot.core.local.linguistic.tokens.Token []
            { tokenNice, tokenTitle });

        TokenSequence tokenSequenceNiceBody = new MutableTokenSequence(
            new com.dawidweiss.carrot.core.local.linguistic.tokens.Token []
            { tokenNice, tokenBody });

        List documentList = Arrays.asList(new TokenizedDocument []
        { document01, document02, document03 });

        context.initialize(documentList);
        List tokenSequenceList = phraseExtractionStrategy
            .getExtractedPhrases(context);

        assertEquals("Stop word stripping phrases", Arrays
            .asList(new TokenSequence []
            { tokenSequenceNiceTitle, tokenSequenceNiceBody }),
            tokenSequenceList);
        assertEquals("Stop word stripping frequency", 3.0,
            ((ExtendedTokenSequence) tokenSequenceList.get(0))
                .getDoubleProperty(ExtendedTokenSequence.PROPERTY_TF, 0), 0);
        assertEquals("Stop word stripping frequency", 2.0,
            ((ExtendedTokenSequence) tokenSequenceList.get(1))
                .getDoubleProperty(ExtendedTokenSequence.PROPERTY_TF, 0), 0);
    }

    /**
     *  
     */
    public void testGeneralizedPhraseWithSingleOriginals()
    {
        // Input documents
        TokenizedDocument document01 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("ośla łączka", "oślej łączki"),
                polishTokenizer);
        TokenizedDocument document02 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("oślej łączce", "oślą łączkę"),
                polishTokenizer);
        TokenizedDocument document03 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("oślą łączką", ""), polishTokenizer);

        List documentList = Arrays.asList(new TokenizedDocument []
        { document01, document02, document03 });

        // Generalized token sequences
        TokenSequence osliLaczka = createTokenStemSequence("ośli łączka", 5.0,
            "oślej łączce", polishTokenizer);

        List expectedTokenSequenceList = Arrays.asList(new TokenSequence []
        { osliLaczka });

        // Extract phrases
        context.initialize(documentList);
        List tokenSequenceList = phraseExtractionStrategy
            .getExtractedPhrases(context);

        // Check the generalized phrase
        assertEquals("Generalized phrases", expectedTokenSequenceList,
            tokenSequenceList);
    }

    /**
     *  
     */
    public void testGeneralizedPhrasesWithSingleOriginals()
    {
        // Input documents
        TokenizedDocument document01 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("ośla łączka . złotej gorączka",
                "oślej łączki . złotej gorączki"), polishTokenizer);
        TokenizedDocument document02 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("oślej łączce . złotej gorączce",
                "oślą łączkę . złotą gorączkę"), polishTokenizer);
        TokenizedDocument document03 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("oślą łączką . złotą gorączką",
                ""), polishTokenizer);

        List documentList = Arrays.asList(new TokenizedDocument []
        { document01, document02, document03 });

        // Generalized token sequences
        TokenSequence osliLaczka = createTokenStemSequence("ośli łączka", 5.0,
            "oślej łączki", polishTokenizer);
        TokenSequence zlotoGoraczka = createTokenStemSequence("złoty gorączka",
            5.0, "złotą gorączkę", polishTokenizer);

        List expectedTokenSequenceList = Arrays.asList(new TokenSequence []
        { osliLaczka, zlotoGoraczka });

        // Extract phrases
        context.initialize(documentList);
        List tokenSequenceList = phraseExtractionStrategy
            .getExtractedPhrases(context);

        // Check the generalized phrase
        assertEquals("Generalized phrases", expectedTokenSequenceList,
            tokenSequenceList);
    }

    /**
     *  
     */
    public void testGeneralizedPhrasesWithMultipleOriginals()
    {
        // Input documents
        TokenizedDocument document01 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("ośla łączka", "oślej łączki"),
                polishTokenizer);
        TokenizedDocument document02 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("oślej łączki", "ośla łączka"),
                polishTokenizer);
        TokenizedDocument document03 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("oślej łączki", ""), polishTokenizer);

        List documentList = Arrays.asList(new TokenizedDocument []
        { document01, document02, document03 });

        // Generalized token sequences
        TokenSequence osliLaczka = createTokenStemSequence("ośli łączka", 5.0,
            "oślej łączki", polishTokenizer);

        List expectedTokenSequenceList = Arrays.asList(new TokenSequence []
        { osliLaczka });

        // Extract phrases
        context.initialize(documentList);
        List tokenSequenceList = phraseExtractionStrategy
            .getExtractedPhrases(context);

        // Check the generalized phrase
        assertEquals("Generalized phrases", expectedTokenSequenceList,
            tokenSequenceList);
    }

    /**
     *  
     */
    public void testOverlappingGeneralizedPhrases()
    {
        // Input documents
        TokenizedDocument document01 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("dałeś chamie złoty róg",
                "nie dał cham złotego rogu"), polishTokenizer);
        TokenizedDocument document02 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("dał chamie złoty róg",
                "dałeś chamie"), polishTokenizer);
        TokenizedDocument document03 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("dałeś mamie złoty róg",
                "miałeś chamie złoty róg"), polishTokenizer);

        List documentList = Arrays.asList(new TokenizedDocument []
        { document01, document02, document03 });

        // Generalized token sequences
        TokenSequence zlotyRog = createTokenStemSequence("złoty róg", 5.0,
            "złoty róg", polishTokenizer);

        TokenSequence dacCham = createTokenStemSequence("dać cham", 4.0,
            "dałeś chamie", polishTokenizer);

        TokenSequence chamZlotyRog = createTokenStemSequence("cham złoty róg",
            4.0, "chamie złoty róg", polishTokenizer);

        TokenSequence dacChamZlotyRog = createTokenStemSequence(
            "dać cham złoty róg", 3.0, "dałeś chamie złoty róg", polishTokenizer);

        List expectedTokenSequenceList = Arrays.asList(new TokenSequence []
        { dacChamZlotyRog, dacCham, chamZlotyRog, zlotyRog });

        // Extract phrases
        context.initialize(documentList);
        List tokenSequenceList = phraseExtractionStrategy
            .getExtractedPhrases(context);

        // Check the generalized phrase
        assertEquals("Generalized phrases", expectedTokenSequenceList,
            tokenSequenceList);
    }

    /**
     * @param stemmedSequence
     * @param stemmedTf
     * @param originalSequence
     * @param lang
     * @return
     */
    private ExtendedTokenSequence createTokenStemSequence(
        String stemmedSequence, double stemmedTf, String originalSequence,
        LanguageTokenizer languageTokenizer)
    {
        // Create a sequence of token stems
        TokenSequence tokenSequence = snippetTokenizer.tokenize(
            stemmedSequence, languageTokenizer);
        MutableTokenSequence tokenStemSequence = new MutableTokenSequence();
        for (int t = 0; t < tokenSequence.getLength(); t++)
        {
            tokenStemSequence.addToken(new TokenStem((TypedToken) tokenSequence
                .getTokenAt(t)));
        }
        ExtendedTokenSequence extendedTokenStemSequence = new ExtendedTokenSequence(
            tokenStemSequence);
        extendedTokenStemSequence.setDoubleProperty(
            ExtendedTokenSequence.PROPERTY_TF, stemmedTf);

        // Create the original token sequence
        MutableTokenSequence originalExtendedTokenSequence = new MutableTokenSequence();
        TokenSequence tokenized = snippetTokenizer.tokenize(originalSequence,
            languageTokenizer);
        for (int i = 0; i < tokenized.getLength(); i++)
        {
            originalExtendedTokenSequence.addToken(tokenized.getTokenAt(i));
        }

        // Attach the original sentences
        extendedTokenStemSequence
            .setProperty(
                ExtendedTokenSequence.PROPERTY_MOST_FREQUENT_ORIGINAL_TOKEN_SEQUENCE,
                originalExtendedTokenSequence);

        return extendedTokenStemSequence;
    }
}