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
public class TfTdMatrixBuildingStrategyTest extends TestCase
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
        TdMatrixBuildingStrategy tdMatrixBuildingStrategy = new TfTdMatrixBuildingStrategy();
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
            .tokenize(new RawDocumentSnippet("dd ee ff", "dd ee ff"));
        List documentList = Arrays.asList(new TokenizedDocument []
        { document01, document02 });

        // Build the matrix
        TdMatrixBuildingStrategy tdMatrixBuildingStrategy = new TfTdMatrixBuildingStrategy();
        DoubleMatrix2D tdMatrix = tdMatrixBuildingStrategy.getTdMatrix(
            documentList, new ArrayList());

        assertEquals("Zero column dimension", 0, tdMatrix
            .columns());
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
    public void testNonEmptyTdMatrix()
    {
        // Input documents
        TokenizedDocument document01 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("aa", "aa cc"));
        TokenizedDocument document02 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("bb", "ee dd cc"));
        TokenizedDocument document03 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("cc dd aa", "aa bb cc dd"));
        List documentList = Arrays.asList(new TokenizedDocument []
        { document01, document02, document03 });

        // Selected features
        FeatureSelectionStrategy featureSelectionStrategy = new TfFeatureSelectionStrategy();
        List selectedFeatures = featureSelectionStrategy
            .getSelectedFeatures(documentList);

        // Build the matix
        TdMatrixBuildingStrategy tdMatrixBuildingStrategy = new TfTdMatrixBuildingStrategy();
        DoubleMatrix2D tdMatrix = tdMatrixBuildingStrategy.getTdMatrix(
            documentList, selectedFeatures);

        DoubleMatrix2D expectedTdMatrix = DoubleFactory2D.sparse
            .make(new double [] []
            {
            { 3.5, 0.0, 3.5 },
            { 1.0, 1.0, 3.5 },
            { 0.0, 1.0, 3.5 },
            { 0.0, 2.5, 1.0 } });

        assertEquals("Term-document matrix", expectedTdMatrix, tdMatrix);
    }


    /**
     *  
     */
    public void testOmitDocument()
    {
        // Input documents
        TokenizedDocument document01 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("", "ee"));
        TokenizedDocument document02 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("bb", "dd cc"));
        TokenizedDocument document03 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("cc dd aa", "aa bb cc dd"));
        List documentList = Arrays.asList(new TokenizedDocument []
        { document01, document02, document03 });

        // Selected features
        FeatureSelectionStrategy featureSelectionStrategy = new TfFeatureSelectionStrategy();
        List selectedFeatures = featureSelectionStrategy
            .getSelectedFeatures(documentList);

        // Build the matix
        TdMatrixBuildingStrategy tdMatrixBuildingStrategy = new TfTdMatrixBuildingStrategy();
        DoubleMatrix2D tdMatrix = tdMatrixBuildingStrategy.getTdMatrix(
            documentList, selectedFeatures);

        DoubleMatrix2D expectedTdMatrix = DoubleFactory2D.sparse
            .make(new double [] []
            {
            { 1.0, 3.5 },
            { 1.0, 3.5 },
            { 0.0, 3.5 },
            { 2.5, 1.0 } });

        assertEquals("Term-document matrix", expectedTdMatrix, tdMatrix);
    }
}