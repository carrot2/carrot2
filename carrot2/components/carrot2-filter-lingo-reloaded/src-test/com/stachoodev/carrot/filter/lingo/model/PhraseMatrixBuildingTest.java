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
import cern.colt.matrix.*;

import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.util.tokenizer.*;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class PhraseMatrixBuildingTest extends TestCase
{
    /** A helper tokenized document */
    private SnippetTokenizer snippetTokenizer;

    /** Helper phrase extraction strategy */
    private PhraseExtraction phraseExtractionStrategy;

    /** Helper feature selection */
    private TfFeatureSelection featureSelectionStrategy;

    /** */
    private ModelBuilderContext context;

    /** Phrase matrix builder under tests */
    private PhraseMatrixBuilding phraseMatrixBuilding;

    /**
     *  
     */
    public PhraseMatrixBuildingTest()
    {
        snippetTokenizer = new SnippetTokenizer();
        phraseExtractionStrategy = new SAPhraseExtraction();
        featureSelectionStrategy = new TfFeatureSelection();
        phraseMatrixBuilding = new PhraseMatrixBuilding();
        context = new ModelBuilderContext();
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
    }

    /**
     *  
     */
    public void testEmptyData()
    {
        context.initialize(Collections.EMPTY_LIST);
        featureSelectionStrategy.getSelectedFeatures(context);
        phraseExtractionStrategy.getExtractedPhrases(context);

        DoubleMatrix2D phraseMatrix = phraseMatrixBuilding
            .getPhraseMatrix(context);
        assertEquals("Zero column-size", 0, phraseMatrix.columns());
        assertEquals("Zero row-size", 0, phraseMatrix.rows());
    }

    /**
     *  
     */
    public void testNoPhrases()
    {
        // Input documents
        TokenizedDocument document01 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("aa", "bb . cc"));
        TokenizedDocument document02 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("bb", "cc"));
        TokenizedDocument document03 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("cc", "aa"));
        List documentList = Arrays.asList(new TokenizedDocument []
        { document01, document02, document03 });
        context.initialize(documentList);

        featureSelectionStrategy.getSelectedFeatures(context);
        phraseExtractionStrategy.getExtractedPhrases(context);

        DoubleMatrix2D expectedPhraseMatrix = DoubleFactory2D.sparse
            .make(new double [] []
            {
            { 1, 0, 0 },
            { 0, 1, 0 },
            { 0, 0, 1 } });

        DoubleMatrix2D phraseMatrix = phraseMatrixBuilding
            .getPhraseMatrix(context);
        assertEquals("Correct phrase matrix", expectedPhraseMatrix,
            phraseMatrix);
    }

    /**
     *  
     */
    public void testPhrases()
    {
        // Input documents
        TokenizedDocument document01 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("aa", "bb cc"));
        TokenizedDocument document02 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("bb cc", "cc"));
        TokenizedDocument document03 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("aa", "aa"));
        List documentList = Arrays.asList(new TokenizedDocument []
        { document01, document02, document03 });
        context.initialize(documentList);
    
        featureSelectionStrategy.getSelectedFeatures(context);
        phraseExtractionStrategy.getExtractedPhrases(context);
    
        DoubleMatrix2D expectedPhraseMatrix = DoubleFactory2D.sparse
            .make(new double [] []
            {
             { 1, 0, 0, 0 },
             { 0, 1, 0, /** 0.6139406135149205 */ 3.5 * (3.0 - 2.0) / (3.0 - 1.0) },
             { 0, 0, 1, /** 0.7893522173763263 */ 4.5 * (3.0 - 2.0) / (3.0 - 1.0) } });
    
        DoubleMatrix2D phraseMatrix = phraseMatrixBuilding
            .getPhraseMatrix(context);
        assertEquals("Correct phrase matrix", expectedPhraseMatrix,
            phraseMatrix);
    }
}