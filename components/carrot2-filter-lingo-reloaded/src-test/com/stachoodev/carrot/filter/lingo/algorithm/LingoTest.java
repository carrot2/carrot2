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
package com.stachoodev.carrot.filter.lingo.algorithm;

import java.util.*;

import junit.framework.*;

import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.util.tokenizer.*;
import com.stachoodev.carrot.filter.lingo.model.*;

/**
 * Simple unit tests for the Lingo algorithm.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class LingoTest extends TestCase
{
    /** A helper SnippetTokenizer */
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
    public void testNoClusters()
    {
        TokenizedDocument document01 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("", "a b c d e"));
        TokenizedDocument document02 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("", "f g h i j"));
        TokenizedDocument document03 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("", "k l m n o"));

        List documentList = Arrays.asList(new TokenizedDocument []
        { document01, document02, document03 });

        LingoWeb lingo = new LingoWeb();
//        lingo.setCandidateLabelsCount(10);
        List clusters = lingo.cluster(documentList, null);

        assertEquals("No clusters generated", 0, clusters.size());
    }

    /**
     *  
     */
    public void SkiptestIdfOmittedDocuments()
    {
        TokenizedDocument document00 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("", "value", "en"));
        TokenizedDocument document01 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("",
                "large scale singular value computations", "en"));
        TokenizedDocument document02 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("",
                "software library for the sparse singular value decomposition",
                "en"));
        TokenizedDocument document03 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("",
                "value introduction to modern information retrieval", "en"));
        TokenizedDocument document04 = snippetTokenizer
            .tokenize(new RawDocumentSnippet(
                "",
                "using linear algebra for intelligent information value retrieval",
                "en"));
        TokenizedDocument document05 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("",
                "value of matrix computations", "en"));
        TokenizedDocument document06 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("",
                "singular value analysis of cryptograms", "en"));
        TokenizedDocument document07 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("",
                "automatic information value organization", "en"));

        List documentList = Arrays.asList(new TokenizedDocument []
        { document00, document01, document02, document03, document04,
         document05, document06, document07 });

        Map parameters = new HashMap();
        parameters.put(LingoWeb.PARAMETER_TD_MATRIX_BUILDING_STRATEGY,
            new TfIdfTdMatrixBuilding());
        parameters.put(LingoWeb.PARAMETER_FEATURE_SELECTION_STRATEGY,
            new TfFeatureSelection());

        LingoWeb lingo = new LingoWeb(parameters);
//        lingo.setCandidateLabelsCount(2);
        List clusters = lingo.cluster(documentList, null);

        // Just check if the first document ended up in the Other Topics group
        assertTrue("document00 in the Other Topics group",
            ((RawCluster) clusters.get(clusters.size() - 1)).getDocuments()
                .contains(
                    document00
                        .getProperty(TokenizedDocument.PROPERTY_RAW_DOCUMENT)));
    }

    /**
     *  
     */
    public void SkiptestTfOmittedDocuments()
    {
        TokenizedDocument document00 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("", "library cryptograms", "en"));
        TokenizedDocument document08 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("", "analysis decomposition", "en"));
        TokenizedDocument document01 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("",
                "large scale singular value computations", "en"));
        TokenizedDocument document02 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("",
                "software library for the sparse singular value decomposition",
                "en"));
        TokenizedDocument document03 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("",
                "introduction to modern information retrieval", "en"));
        TokenizedDocument document04 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("",
                "using linear algebra for intelligent information retrieval",
                "en"));
        TokenizedDocument document05 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("", "matrix computations", "en"));
        TokenizedDocument document06 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("",
                "singular value analysis of cryptograms", "en"));
        TokenizedDocument document07 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("",
                "automatic information organization", "en"));

        List documentList = Arrays.asList(new TokenizedDocument []
        { document01, document00, document02, document03, document04,
         document05, document08, document06, document07 });

        Map parameters = new HashMap();
        parameters.put(LingoWeb.PARAMETER_TD_MATRIX_BUILDING_STRATEGY,
            new TfIdfTdMatrixBuilding());
        parameters.put(LingoWeb.PARAMETER_FEATURE_SELECTION_STRATEGY,
            new TfFeatureSelection());

        LingoWeb lingo = new LingoWeb(parameters);
//        lingo.setCandidateLabelsCount(2);
        List clusters = lingo.cluster(documentList, null);

        // Documents 00 08 05 in the Other Topics group
        assertTrue("document00 in the Other Topics group",
            ((RawCluster) clusters.get(clusters.size() - 1)).getDocuments()
                .contains(
                    document00
                        .getProperty(TokenizedDocument.PROPERTY_RAW_DOCUMENT)));
        assertTrue("document08 in the Other Topics group",
            ((RawCluster) clusters.get(clusters.size() - 1)).getDocuments()
                .contains(
                    document08
                        .getProperty(TokenizedDocument.PROPERTY_RAW_DOCUMENT)));
        assertTrue("document05 in the Other Topics group",
            ((RawCluster) clusters.get(clusters.size() - 1)).getDocuments()
                .contains(
                    document05
                        .getProperty(TokenizedDocument.PROPERTY_RAW_DOCUMENT)));
    }

    /**
     *  
     */
    public void testLabelDiscovery()
    {
        TokenizedDocument document01 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("",
                "large scale singular value computations", "en"));
        TokenizedDocument document02 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("",
                "software library for the sparse singular value decomposition",
                "en"));
        TokenizedDocument document03 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("",
                "introduction to modern information retrieval", "en"));
        TokenizedDocument document04 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("",
                "using linear algebra for intelligent information retrieval",
                "en"));
        TokenizedDocument document05 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("", "matrix computations", "en"));
        TokenizedDocument document06 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("",
                "singular value analysis of cryptograms", "en"));
        TokenizedDocument document07 = snippetTokenizer
            .tokenize(new RawDocumentSnippet("",
                "automatic information organization", "en"));

        List documentList = Arrays.asList(new TokenizedDocument []
        { document01, document02, document03, document04, document05,
         document06, document07 });

        Map parameters = new HashMap();
        parameters.put(LingoWeb.PARAMETER_TD_MATRIX_BUILDING_STRATEGY,
            new TfIdfTdMatrixBuilding());
        parameters.put(LingoWeb.PARAMETER_FEATURE_SELECTION_STRATEGY,
            new TfFeatureSelection());

        LingoWeb lingo = new LingoWeb(parameters);
//        lingo.setCandidateLabelsCount(10);
        List clusters = lingo.cluster(documentList, null);
        
        // No more than three clusters generated
        assertTrue("No more than three clusters", clusters.size() <= 3);
    }
}