/*
 * LingoTest.java Created on 2004-06-23
 */
package com.stachoodev.carrot.filter.lingo.algorithm;

import java.util.*;

import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.util.tokenizer.*;

import junit.framework.*;

/**
 * @author stachoo
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

        Lingo lingo = new Lingo();
        lingo.setCandidateLabelsCount(10);
        List clusters = lingo.cluster(documentList);
        
        assertEquals("No clusters generated", 0, clusters.size());
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

        Lingo lingo = new Lingo();
        lingo.setCandidateLabelsCount(10);
        List clusters = lingo.cluster(documentList);

        for (Iterator iter = clusters.iterator(); iter.hasNext();)
        {
            LingoRawCluster cluster = (LingoRawCluster) iter.next();
            System.out.println(cluster.getFullInfo() + "\n");
        }
    }
}