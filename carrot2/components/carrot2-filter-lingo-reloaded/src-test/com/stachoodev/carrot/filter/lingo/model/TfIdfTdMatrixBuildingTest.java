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
public class TfIdfTdMatrixBuildingTest extends TestCase
{
    /** A helper tokenized document */
    private SnippetTokenizer snippetTokenizer;

    /** Td matrix building under tests */
    private TfIdfTdMatrixBuilding tdMatrixBuildingStrategy;

    /** Helper feature selection */
    private TfFeatureSelection featureSelectionStrategy;

    /** */
    private ModelBuilderContext context;

    /**
     *  
     */
    public TfIdfTdMatrixBuildingTest()
    {
        context = new ModelBuilderContext();
        featureSelectionStrategy = new TfFeatureSelection();
        snippetTokenizer = new SnippetTokenizer();
        tdMatrixBuildingStrategy = new TfIdfTdMatrixBuilding();

        tdMatrixBuildingStrategy.setProperty(
            TfIdfTdMatrixBuilding.PROPERTY_IDF_FORMULA, IdfFormula.linear);
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
    public void testEmptyDocumentAndFeatureList()
    {
        context.initialize(Collections.EMPTY_LIST);
        featureSelectionStrategy.getSelectedFeatures(context);
        DoubleMatrix2D tdMatrix = tdMatrixBuildingStrategy.getTdMatrix(context);

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
        context.initialize(documentList);

        featureSelectionStrategy.setDoubleProperty(
            TfFeatureSelection.PROPERTY_TF_THRESHOLD, 10);
        featureSelectionStrategy.getSelectedFeatures(context);

        // Build the matrix
        DoubleMatrix2D tdMatrix = tdMatrixBuildingStrategy.getTdMatrix(context);

        assertEquals("Non-Zero column dimension", 2, tdMatrix.columns());
        assertEquals("Zero row dimension", 0, tdMatrix.rows());
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
        context.initialize(documentList);

        // Selected features
        featureSelectionStrategy.getSelectedFeatures(context);

        // Build the matix
        tdMatrixBuildingStrategy.setProperty(
            TfIdfTdMatrixBuilding.PROPERTY_IDF_FORMULA, IdfFormula.classic);
        DoubleMatrix2D tdMatrix = tdMatrixBuildingStrategy.getTdMatrix(context);

        DoubleMatrix2D expectedTdMatrix = DoubleFactory2D.sparse
            .make(new double [] []
            {
             { 3.5 * Math.log(3.0 / 3.0), 1.0 * Math.log(3.0 / 3.0),
              1.0 * Math.log(3.0 / 3.0) },
             { 1.0 * Math.log(3.0 / 2.0), 0.0 * Math.log(3.0 / 2.0),
              6.0 * Math.log(3.0 / 2.0) },
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
        context.initialize(documentList);

        // Selected features
        featureSelectionStrategy.getSelectedFeatures(context);

        // Build the matix
        DoubleMatrix2D tdMatrix = tdMatrixBuildingStrategy.getTdMatrix(context);

        DoubleMatrix2D expectedTdMatrix = DoubleFactory2D.sparse
            .make(new double [] []
            {
             { 3.5 * (3.0 - 3.0) / (3.0 - 1), 1.0 * (3.0 - 3.0) / (3.0 - 1),
              1.0 * (3.0 - 3.0) / (3.0 - 1) },
             { 1.0 * (3.0 - 2.0) / (3.0 - 1), 0.0,
              6.0 * (3.0 - 2.0) / (3.0 - 1) },
             { 1.0 * (3.0 - 2.0) / (3.0 - 1), 0.0,
              3.5 * (3.0 - 2.0) / (3.0 - 1) },
             { 0.0 * (3.0 - 2.0) / (3.0 - 1.0),
              3.5 * (3.0 - 1.0) / (3.0 - 1.0), 0.0 * (3.0 - 1.0) / (3.0 - 1.0) } });

        assertEquals("Term-document matrix", expectedTdMatrix, tdMatrix);
    }

    /**
     *  
     */
//    public void testOmitDocument()
//    {
//        // Input documents
//        TokenizedDocument document01 = snippetTokenizer
//            .tokenize(new RawDocumentSnippet("dd", "bb ee dd"));
//        TokenizedDocument document02 = snippetTokenizer
//            .tokenize(new RawDocumentSnippet("bb", "dd cc"));
//        TokenizedDocument document03 = snippetTokenizer
//            .tokenize(new RawDocumentSnippet("cc aa aa", "bb aa cc dd"));
//        List documentList = Arrays.asList(new TokenizedDocument []
//        { document01, document02, document03 });
//        context.initialize(documentList);
//
//        // Selected features
//        featureSelectionStrategy.getSelectedFeatures(context);
//
//        // Build the matix
//        DoubleMatrix2D tdMatrix = tdMatrixBuildingStrategy.getTdMatrix(context);
//
//        DoubleMatrix2D expectedTdMatrix = DoubleFactory2D.sparse
//            .make(new double [] []
//            {
//             { 1.0 * 0.0, 1.0 * 0 },
//             { 3.5 * 0.0, 1.0 * 0 },
//             { 1.0 * (3.0 - 2.0) / (3.0 - 1.0), 3.5 * (3.0 - 2.0) / (3.0 - 1.0) },
//             { 0.0, 6.0 * (3.0 - 1.0) / (3.0 - 1.0) } });
//
//        assertEquals("Term-document matrix", expectedTdMatrix, tdMatrix);
//        assertNotNull("Document omitted property set", document01
//            .getProperty(TdMatrixBuilding.PROPERTY_DOCUMENT_OMITTED));
//    }
}