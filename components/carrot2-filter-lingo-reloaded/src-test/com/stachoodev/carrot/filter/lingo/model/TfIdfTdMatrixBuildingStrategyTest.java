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
public class TfIdfTdMatrixBuildingStrategyTest extends TestCase
{
    /** A helper tokenized document */
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
    public void testEmptyDocumentAndFeatureList()
    {
        TdMatrixBuildingStrategy tdMatrixBuildingStrategy = new TfIdfTdMatrixBuildingStrategy();
        DoubleMatrix2D tdMatrix = tdMatrixBuildingStrategy.getTdMatrix(
            new ArrayList(), new ArrayList());

        assertEquals("Zero column dimension", 0, tdMatrix.columns());
        assertEquals("Zero row dimension", 0, tdMatrix.rows());
    }

    /**
     *  
     */
    public void testNonEmptyDocumentList()
    {
        // Input documents
        TokenizedDocument document01 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("aa bb cc", "aa bb cc"));
        TokenizedDocument document02 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("dd ee ff", "ad ee ff"));
        List documentList = Arrays.asList(new TokenizedDocument []
        { document01, document02 });

        // Build the matrix
        TdMatrixBuildingStrategy tdMatrixBuildingStrategy = new TfIdfTdMatrixBuildingStrategy();
        DoubleMatrix2D tdMatrix = tdMatrixBuildingStrategy.getTdMatrix(
            documentList, new ArrayList());

        assertEquals("Zero column dimension", 0, tdMatrix.columns());
        assertEquals("Zero row dimension", 0, tdMatrix.rows());
    }

    /**
     *  
     */
    public void testNonEmptyFeatureList()
    {
        // Input documents
        TokenizedDocument document01 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("aa bb cc", "aa bb cc"));
        TokenizedDocument document02 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("aa bb cc", "aa bb cc"));
        List documentList = Arrays.asList(new TokenizedDocument []
        { document01, document02 });

        // Selected features
        FeatureSelectionStrategy featureSelectionStrategy = new TfFeatureSelectionStrategy();
        List selectedFeatures = featureSelectionStrategy
            .getSelectedFeatures(documentList);

        // Build the matix
        TdMatrixBuildingStrategy tdMatrixBuildingStrategy = new TfTdMatrixBuildingStrategy();
        DoubleMatrix2D tdMatrix = tdMatrixBuildingStrategy.getTdMatrix(
            new ArrayList(), selectedFeatures);

        assertEquals("Non-zero column dimension", 0, tdMatrix.columns());
        assertEquals("Zero row dimension", selectedFeatures.size(), tdMatrix
            .rows());
    }

    /**
     *  
     */
    public void testNonEmptyTdMatrixClassicIdf()
    {
        // Input documents
        TokenizedDocument document01 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("dd", "aa cc dd"));
        TokenizedDocument document02 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("bb", "bb dd"));
        TokenizedDocument document03 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("cc aa aa", "aa ee cc dd"));
        List documentList = Arrays.asList(new TokenizedDocument []
        { document01, document02, document03 });

        // Selected features
        FeatureSelectionStrategy featureSelectionStrategy = new TfFeatureSelectionStrategy();
        List selectedFeatures = featureSelectionStrategy
            .getSelectedFeatures(documentList);

        // Build the matix
        TdMatrixBuildingStrategy tdMatrixBuildingStrategy = new TfIdfTdMatrixBuildingStrategy(
            TfIdfTdMatrixBuildingStrategy.DEFAULT_TITLE_TOKEN_TF_MULTIPLIER,
            TfIdfTdMatrixBuildingStrategy.DEFAULT_DOUBLE_FACTORY_2D,
            IdfFormula.classic);
        DoubleMatrix2D tdMatrix = tdMatrixBuildingStrategy.getTdMatrix(
            documentList, selectedFeatures);

        DoubleMatrix2D expectedTdMatrix = DoubleFactory2D.sparse
            .make(new double [] []
            {
             { 1.0 * Math.log(3.0 / 2.0), 0.0 * Math.log(3.0 / 2.0),
              6.0 * Math.log(3.0 / 2.0) },
             { 3.5 * Math.log(3.0 / 3.0), 1.0 * Math.log(3.0 / 3.0),
              1.0 * Math.log(3.0 / 3.0) },
             { 1.0 * Math.log(3.0 / 2.0), 0.0 * Math.log(3.0 / 2.0),
              3.5 * Math.log(3.0 / 2.0) },
             { 0.0 * Math.log(3.0 / 1.0), 3.5 * Math.log(3.0 / 1.0),
              0.0 * Math.log(3.0 / 1.0) } });

        assertEquals("Term-document matrix", expectedTdMatrix, tdMatrix);
    }

    /**
     *  
     */
    public void testNonEmptyTdMatrixLinearIdf()
    {
        // Input documents
        TokenizedDocument document01 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("dd", "aa cc dd"));
        TokenizedDocument document02 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("bb", "bb dd"));
        TokenizedDocument document03 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("cc aa aa", "aa ee cc dd"));
        List documentList = Arrays.asList(new TokenizedDocument []
        { document01, document02, document03 });

        // Selected features
        FeatureSelectionStrategy featureSelectionStrategy = new TfFeatureSelectionStrategy();
        List selectedFeatures = featureSelectionStrategy
            .getSelectedFeatures(documentList);

        // Build the matix
        TdMatrixBuildingStrategy tdMatrixBuildingStrategy = new TfIdfTdMatrixBuildingStrategy(
            TfIdfTdMatrixBuildingStrategy.DEFAULT_TITLE_TOKEN_TF_MULTIPLIER,
            TfIdfTdMatrixBuildingStrategy.DEFAULT_DOUBLE_FACTORY_2D,
            IdfFormula.linear);
        DoubleMatrix2D tdMatrix = tdMatrixBuildingStrategy.getTdMatrix(
            documentList, selectedFeatures);

        DoubleMatrix2D expectedTdMatrix = DoubleFactory2D.sparse
            .make(new double [] []
            {
             { 1.0 * (3.0 - 2.0) / (3.0 - 1), 0.0,
              6.0 * (3.0 - 2.0) / (3.0 - 1) },
             { 3.5 * (3.0 - 3.0) / (3.0 - 1), 1.0 * (3.0 - 3.0) / (3.0 - 1),
              1.0 * (3.0 - 3.0) / (3.0 - 1) },
             { 1.0 * (3.0 - 2.0) / (3.0 - 1), 0.0,
              3.5 * (3.0 - 2.0) / (3.0 - 1) },
             { 0.0 * (3.0 - 2.0) / (3.0 - 1.0),
              3.5 * (3.0 - 1.0) / (3.0 - 1.0), 0.0 * (3.0 - 1.0) / (3.0 - 1.0) } });

        assertEquals("Term-document matrix", expectedTdMatrix, tdMatrix);
    }

    /**
     *  
     */
    public void testOmitDocument()
    {
        // Input documents
        TokenizedDocument document01 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("dd", "bb ee dd"));
        TokenizedDocument document02 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("bb", "dd cc"));
        TokenizedDocument document03 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("cc aa aa", "bb aa cc dd"));
        List documentList = Arrays.asList(new TokenizedDocument []
        { document01, document02, document03 });

        // Selected features
        FeatureSelectionStrategy featureSelectionStrategy = new TfFeatureSelectionStrategy();
        List selectedFeatures = featureSelectionStrategy
            .getSelectedFeatures(documentList);

        // Build the matix
        TdMatrixBuildingStrategy tdMatrixBuildingStrategy = new TfIdfTdMatrixBuildingStrategy(
            TfIdfTdMatrixBuildingStrategy.DEFAULT_TITLE_TOKEN_TF_MULTIPLIER,
            TfIdfTdMatrixBuildingStrategy.DEFAULT_DOUBLE_FACTORY_2D,
            IdfFormula.linear);
        DoubleMatrix2D tdMatrix = tdMatrixBuildingStrategy.getTdMatrix(
            documentList, selectedFeatures);

        DoubleMatrix2D expectedTdMatrix = DoubleFactory2D.sparse
            .make(new double [] []
            {
             { 0.0, 6.0 * (3.0 - 1.0) / (3.0 - 1.0) },
             { 1.0 * 0.0, 1.0 * 0 },
             { 3.5 * 0.0, 1.0 * 0 },
             { 1.0 * (3.0 - 2.0) / (3.0 - 1.0), 3.5 * (3.0 - 2.0) / (3.0 - 1.0) } });

        assertEquals("Term-document matrix", expectedTdMatrix, tdMatrix);
    }
}