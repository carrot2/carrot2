/*
 * SAPhraseExtractionStrategyTest.java Created on 2004-06-16
 */
package com.stachoodev.carrot.filter.lingo.model;

import java.util.*;

import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
import com.dawidweiss.carrot.util.tokenizer.*;
import com.dawidweiss.carrot.util.tokenizer.parser.*;

import junit.framework.*;

/**
 * @author stachoo
 */
public class SAPhraseExtractionStrategyTest extends TestCase
{
    /** PhraseExtractionStrategy under tests */
    private PhraseExtractionStrategy phraseExtractionStrategy;

    /** A helper tokenized document factory */
    private SnippetTokenizer snippetTokenizer;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        phraseExtractionStrategy = new SAPhraseExtractionStrategy();
        snippetTokenizer = new SnippetTokenizer();
    }

    /**
     * 
     */
    public void testEmptyInputData()
    {
        assertEquals("Zero-length phrase list", 0, phraseExtractionStrategy
                .getExtractedPhrases(new ArrayList()).size());
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

        assertEquals("Zero-length phrase list", 0, phraseExtractionStrategy
                .getExtractedPhrases(
                        new ArrayList(Arrays.asList(new TokenizedDocument[]
                        { document01, document02 }))).size());
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
                new com.dawidweiss.carrot.core.local.linguistic.tokens.Token[]
                { tokenA, tokenB });

        List documentList = Arrays.asList(new TokenizedDocument[]
        { document01, document02 });

        List tokenSequenceList = phraseExtractionStrategy
                .getExtractedPhrases(documentList);

        assertEquals("One frequent phrase token sequence", Arrays
                .asList(new TokenSequence[]
                { tokenSequence }), tokenSequenceList);
        assertEquals("One frequent phrase frequency", 2.0,
                ((ExtendedTokenSequence) tokenSequenceList.get(0))
                        .getDoubleProperty(ExtendedTokenSequence.PROPERTY_TF, 0),
                0);
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
                new com.dawidweiss.carrot.core.local.linguistic.tokens.Token[]
                { tokenA, tokenB });

        TokenSequence tokenSequenceQRS = new MutableTokenSequence(
                new com.dawidweiss.carrot.core.local.linguistic.tokens.Token[]
                { tokenQ, tokenR, tokenS });

        List documentList = Arrays.asList(new TokenizedDocument[]
        { document01, document02, document03 });

        List tokenSequenceList = phraseExtractionStrategy
                .getExtractedPhrases(documentList);

        assertEquals("More frequent phrases token sequences", Arrays
                .asList(new TokenSequence[]
                { tokenSequenceAB, tokenSequenceQRS }), tokenSequenceList);
        assertEquals("One frequent phrase frequency", 3.0,
                ((ExtendedTokenSequence) tokenSequenceList.get(0))
                        .getDoubleProperty(ExtendedTokenSequence.PROPERTY_TF, 0),
                0);
        assertEquals("One frequent phrase frequency", 2.0,
                ((ExtendedTokenSequence) tokenSequenceList.get(1))
                        .getDoubleProperty(ExtendedTokenSequence.PROPERTY_TF, 0),
                0);
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
                .tokenize(new RawDocumentSnippet("a nice snippet title", "",
                        "en"));

        StringTypedToken tokenNice = new StringTypedToken();
        tokenNice.assign("nice", (short) 0);
        StringTypedToken tokenTitle = new StringTypedToken();
        tokenTitle.assign("titl", (short) 0);
        StringTypedToken tokenBody = new StringTypedToken();
        tokenBody.assign("snippet", (short) 0);

        TokenSequence tokenSequenceNiceTitle = new MutableTokenSequence(
                new com.dawidweiss.carrot.core.local.linguistic.tokens.Token[]
                { tokenNice, tokenBody, tokenTitle });

        List documentList = Arrays.asList(new TokenizedDocument[]
        { document01, document02 });

        List tokenSequenceList = phraseExtractionStrategy
                .getExtractedPhrases(documentList);

        assertEquals("Stop word stripping phrases", Arrays
                .asList(new TokenSequence[]
                { tokenSequenceNiceTitle }), tokenSequenceList);
        assertEquals("Stop word stripping frequency", 3.0,
                ((ExtendedTokenSequence) tokenSequenceList.get(0))
                        .getDoubleProperty(ExtendedTokenSequence.PROPERTY_TF, 0),
                0);
    }

    /**
     * 
     */
    public void testStopWordStripping()
    {
        TokenizedDocument document01 = snippetTokenizer
                .tokenize(new RawDocumentSnippet(
                        "and this is a nice title of the", "a nice snippet",
                        "en"));
        TokenizedDocument document02 = snippetTokenizer
                .tokenize(new RawDocumentSnippet(
                        "and this is a nice title of the", "the nice body",
                        "en"));
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
                new com.dawidweiss.carrot.core.local.linguistic.tokens.Token[]
                { tokenNice, tokenTitle });

        TokenSequence tokenSequenceNiceBody = new MutableTokenSequence(
                new com.dawidweiss.carrot.core.local.linguistic.tokens.Token[]
                { tokenNice, tokenBody });

        List documentList = Arrays.asList(new TokenizedDocument[]
        { document01, document02, document03 });

        List tokenSequenceList = phraseExtractionStrategy
                .getExtractedPhrases(documentList);

        assertEquals("Stop word stripping phrases", Arrays
                .asList(new TokenSequence[]
                { tokenSequenceNiceTitle, tokenSequenceNiceBody }),
                tokenSequenceList);
        assertEquals("Stop word stripping frequency", 3.0,
                ((ExtendedTokenSequence) tokenSequenceList.get(0))
                        .getDoubleProperty(ExtendedTokenSequence.PROPERTY_TF, 0),
                0);
        assertEquals("Stop word stripping frequency", 2.0,
                ((ExtendedTokenSequence) tokenSequenceList.get(1))
                        .getDoubleProperty(ExtendedTokenSequence.PROPERTY_TF, 0),
                0);
    }

    /**
     * 
     */
    public void testGeneralizedPhrasesWithSingleOriginals()
    {
        // Input documents
        TokenizedDocument document01 = snippetTokenizer
                .tokenize(new RawDocumentSnippet("ośla łączka", "oślej łączki",
                        "pl"));
        TokenizedDocument document02 = snippetTokenizer
                .tokenize(new RawDocumentSnippet("oślej łączce", "oślą łączkę",
                        "pl"));
        TokenizedDocument document03 = snippetTokenizer
                .tokenize(new RawDocumentSnippet("oślą łączką", "", "pl"));

        List documentList = Arrays.asList(new TokenizedDocument[]
        { document01, document02, document03 });

        // Generalized token sequences
        TokenSequence osliLaczka = createTokenStemSequence("ośli łączka", 5.0,
                new String[]
                { "ośla łączka", "oślej łączce", "oślej łączki", "oślą łączką",
                 "oślą łączkę" }, new double[]
                { 1.0, 1.0, 1.0, 1.0, 1.0 }, "pl");

        List expectedTokenSequenceList = Arrays.asList(new TokenSequence[]
        { osliLaczka });

        // Extract phrases
        List tokenSequenceList = phraseExtractionStrategy
                .getExtractedPhrases(documentList);

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
                .tokenize(new RawDocumentSnippet("ośla łączka", "oślej łączki",
                        "pl"));
        TokenizedDocument document02 = snippetTokenizer
                .tokenize(new RawDocumentSnippet("oślej łączki", "ośla łączka",
                        "pl"));
        TokenizedDocument document03 = snippetTokenizer
                .tokenize(new RawDocumentSnippet("oślej łączki", "", "pl"));

        List documentList = Arrays.asList(new TokenizedDocument[]
        { document01, document02, document03 });

        // Generalized token sequences
        TokenSequence osliLaczka = createTokenStemSequence("ośli łączka", 5.0,
                new String[]
                { "oślej łączki", "ośla łączka" }, new double[]
                { 3.0, 2.0 }, "pl");

        List expectedTokenSequenceList = Arrays.asList(new TokenSequence[]
        { osliLaczka });

        // Extract phrases
        List tokenSequenceList = phraseExtractionStrategy
                .getExtractedPhrases(documentList);

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
                        "nie dał cham złotego rogu", "pl"));
        TokenizedDocument document02 = snippetTokenizer
                .tokenize(new RawDocumentSnippet("dał chamie złoty róg",
                        "dałeś chamie", "pl"));
        TokenizedDocument document03 = snippetTokenizer
                .tokenize(new RawDocumentSnippet("dałeś mamie złoty róg",
                        "miałeś chamie złoty róg", "pl"));

        List documentList = Arrays.asList(new TokenizedDocument[]
        { document01, document02, document03 });

        // Generalized token sequences
        TokenSequence zlotyRog = createTokenStemSequence("złoty róg", 5.0,
                new String[]
                { "złoty róg", "złotego rogu" }, new double[]
                { 4.0, 1.0 }, "pl");

        TokenSequence dacCham = createTokenStemSequence("dać cham", 4.0,
                new String[]
                { "dałeś chamie", "dał chamie", "dał cham" }, new double[]
                { 2.0, 1.0, 1.0 }, "pl");

        TokenSequence chamZlotyRog = createTokenStemSequence("cham złoty róg",
                4.0, new String[]
                { "chamie złoty róg", "cham złotego rogu" }, new double[]
                { 3.0, 1.0 }, "pl");

        TokenSequence dacChamZlotyRog = createTokenStemSequence(
                "dać cham złoty róg", 3.0, new String[]
                { "dałeś chamie złoty róg", "dał chamie złoty róg",
                 "dał cham złotego rogu" }, new double[]
                { 1.0, 1.0, 1.0 }, "pl");

        List expectedTokenSequenceList = Arrays.asList(new TokenSequence[]
        { zlotyRog, dacCham, chamZlotyRog, dacChamZlotyRog });

        // Extract phrases
        List tokenSequenceList = phraseExtractionStrategy
                .getExtractedPhrases(documentList);

        // Check the generalized phrase
        assertEquals("Generalized phrases", expectedTokenSequenceList,
                tokenSequenceList);
    }

    /**
     * @param stemmedSequence
     * @param stemmedTf
     * @param originalSequences
     * @param originalTf
     * @param lang
     * @return
     */
    private ExtendedTokenSequence createTokenStemSequence(
            String stemmedSequence, double stemmedTf,
            String [] originalSequences, double [] originalTf, String lang)
    {
        // Create a sequence of token stems
        TokenSequence tokenSequence = snippetTokenizer.tokenize(
                stemmedSequence, lang);
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

        // Create the original token sequences
        List originalTokenSequencesList = new ArrayList(
                originalSequences.length);
        for (int i = 0; i < originalSequences.length; i++)
        {
            ExtendedTokenSequence originalExtendedTokenSequence = new ExtendedTokenSequence(
                    snippetTokenizer.tokenize(originalSequences[i], lang));
            originalExtendedTokenSequence.setDoubleProperty(
                    ExtendedTokenSequence.PROPERTY_TF, originalTf[i]);
            originalTokenSequencesList.add(originalExtendedTokenSequence);
        }

        // Attach the original sentences
        extendedTokenStemSequence.setProperty(
                ExtendedTokenSequence.PROPERTY_ORIGINAL_TOKEN_SEQUENCES,
                originalTokenSequencesList);

        return extendedTokenStemSequence;
    }
}
