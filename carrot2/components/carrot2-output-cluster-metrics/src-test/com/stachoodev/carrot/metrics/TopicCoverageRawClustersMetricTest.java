
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package com.stachoodev.carrot.metrics;

import java.util.*;

import com.dawidweiss.carrot.core.local.clustering.*;

import junit.framework.*;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class TopicCoverageRawClustersMetricTest extends TestCase
{
    /** Clusters metric under test */
    private RawClustersMetric rawClustersMetric;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        rawClustersMetric = new TopicCoverageRawClustersMetric();
    }

    /**
     *  
     */
    public void testEmptyClusterList()
    {
        assertEquals("Empty metrics map", Collections.EMPTY_MAP,
            rawClustersMetric.compute(Collections.EMPTY_LIST,
                Collections.EMPTY_LIST));
    }

    /**
     *  
     */
    public void testEmptyClusters()
    {
        RawClusterBase rawCluster01 = new RawClusterBase();
        rawCluster01.setProperty(RawDocumentsProducer.PROPERTY_CATID, "1");
        RawClusterBase rawCluster02 = new RawClusterBase();
        rawCluster02.setProperty(RawDocumentsProducer.PROPERTY_CATID, "1");
        RawClusterBase rawCluster03 = new RawClusterBase();
        rawCluster03.setProperty(RawDocumentsProducer.PROPERTY_CATID, "1");

        List rawClusters = Arrays.asList(new RawCluster []
        { rawCluster01, rawCluster02, rawCluster03 });

        assertEquals("Empty metrics map", new HashMap(), rawClustersMetric
            .compute(rawClusters, rawClusters));
    }

    /**
     *  
     */
    public void testOnePredominantCatid()
    {
        RawClusterBase rawCluster = new RawClusterBase();
        RawDocumentBase rawDocument;

        for (int i = 0; i < 3; i++)
        {
            rawDocument = new RawDocumentSnippet("", "");
            rawDocument.setProperty(RawDocumentsProducer.PROPERTY_CATID, "12");
            rawCluster.addDocument(rawDocument);
        }
        rawDocument = new RawDocumentSnippet("", "");
        rawDocument.setProperty(RawDocumentsProducer.PROPERTY_CATID, "13");
        rawCluster.addDocument(rawDocument);
        rawDocument = new RawDocumentSnippet("", "");
        rawDocument.setProperty(RawDocumentsProducer.PROPERTY_CATID, "13");
        rawCluster.addDocument(rawDocument);

        rawClustersMetric.compute(Arrays.asList(new RawCluster []
        { rawCluster }), Collections.EMPTY_LIST);

        assertEquals(
            "Predominant catid",
            "12",
            rawCluster
                .getProperty(TopicCoverageRawClustersMetric.PROPERTY_CLUSTER_PREDOMINANT_CATID));
    }

    /**
     *  
     */
    public void testMorePredominantCatids()
    {
        RawClusterBase rawCluster = new RawClusterBase();
        RawDocumentBase rawDocument;

        for (int i = 0; i < 3; i++)
        {
            rawDocument = new RawDocumentSnippet("", "");
            rawDocument.setProperty(RawDocumentsProducer.PROPERTY_CATID, "12");
            rawCluster.addDocument(rawDocument);
        }
        for (int i = 0; i < 3; i++)
        {
            rawDocument = new RawDocumentSnippet("", "");
            rawDocument.setProperty(RawDocumentsProducer.PROPERTY_CATID, "14");
            rawCluster.addDocument(rawDocument);
        }
        rawDocument = new RawDocumentSnippet("", "");
        rawDocument.setProperty(RawDocumentsProducer.PROPERTY_CATID, "13");
        rawCluster.addDocument(rawDocument);
        rawDocument = new RawDocumentSnippet("", "");
        rawDocument.setProperty(RawDocumentsProducer.PROPERTY_CATID, "13");
        rawCluster.addDocument(rawDocument);

        rawClustersMetric.compute(Arrays.asList(new RawCluster []
        { rawCluster }), Collections.EMPTY_LIST);

        assertEquals(
            "Predominant catid",
            null,
            rawCluster
                .getProperty(TopicCoverageRawClustersMetric.PROPERTY_CLUSTER_PREDOMINANT_CATID));
    }

    /**
     *  
     */
    public void testIncompleteCoverage()
    {
        RawDocumentBase rawDocument;

        // Assessed clusters
        RawClusterBase rawCluster01 = new RawClusterBase();
        rawDocument = new RawDocumentSnippet("", "");
        rawDocument.setProperty(RawDocumentsProducer.PROPERTY_CATID, "13");
        rawCluster01.addDocument(rawDocument);
        rawDocument = new RawDocumentSnippet("", "");
        rawDocument.setProperty(RawDocumentsProducer.PROPERTY_CATID, "13");
        rawCluster01.addDocument(rawDocument);
        rawDocument = new RawDocumentSnippet("", "");
        rawDocument.setProperty(RawDocumentsProducer.PROPERTY_CATID, "10");
        rawCluster01.addDocument(rawDocument);

        RawClusterBase rawCluster02 = new RawClusterBase();
        rawDocument = new RawDocumentSnippet("", "");
        rawDocument.setProperty(RawDocumentsProducer.PROPERTY_CATID, "12");
        rawCluster02.addDocument(rawDocument);

        RawClusterBase rawCluster03 = new RawClusterBase();
        rawDocument = new RawDocumentSnippet("", "");
        rawDocument.setProperty(RawDocumentsProducer.PROPERTY_CATID, "14");
        rawCluster03.addDocument(rawDocument);
        rawDocument = new RawDocumentSnippet("", "");
        rawDocument.setProperty(RawDocumentsProducer.PROPERTY_CATID, "12");
        rawCluster03.addDocument(rawDocument);

        RawCluster [] assessedClusters = new RawCluster []
        { rawCluster01, rawCluster02, rawCluster03 };

        // Original clusters
        List originalClusters = new ArrayList();
        RawClusterBase originalCluster;
        originalCluster = new RawClusterBase();
        originalCluster.setProperty(RawDocumentsProducer.PROPERTY_CATID, "10");
        originalClusters.add(originalCluster);
        originalCluster = new RawClusterBase();
        originalCluster.setProperty(RawDocumentsProducer.PROPERTY_CATID, "12");
        originalClusters.add(originalCluster);
        originalCluster = new RawClusterBase();
        originalCluster.setProperty(RawDocumentsProducer.PROPERTY_CATID, "13");
        originalClusters.add(originalCluster);
        originalCluster = new RawClusterBase();
        originalCluster.setProperty(RawDocumentsProducer.PROPERTY_CATID, "14");
        originalClusters.add(originalCluster);

        Map expectedMetrics = new HashMap();
        expectedMetrics.put(
            TopicCoverageRawClustersMetric.METRIC_EXTENDED_TOPIC_COVERAGE,
            new Double(2 / Math.sqrt(3 * 4)));
        expectedMetrics.put(
            TopicCoverageRawClustersMetric.METRIC_SIMPLE_TOPIC_COVERAGE,
            new Double(2.0 / 4.0));

        assertEquals("Coverage value", expectedMetrics, rawClustersMetric
            .compute(Arrays.asList(assessedClusters), originalClusters));
    }

    /**
     *  
     */
    public void testCompleteCoverage()
    {
        RawDocumentBase rawDocument;

        // Assessed clusters
        RawClusterBase rawCluster01 = new RawClusterBase();
        rawCluster01.setScore(0.5);
        rawDocument = new RawDocumentSnippet("", "");
        rawDocument.setProperty(RawDocumentsProducer.PROPERTY_CATID, "13");
        rawCluster01.addDocument(rawDocument);
        rawDocument = new RawDocumentSnippet("", "");
        rawDocument.setProperty(RawDocumentsProducer.PROPERTY_CATID, "13");
        rawCluster01.addDocument(rawDocument);

        RawClusterBase rawCluster02 = new RawClusterBase();
        rawCluster02.setScore(0.5);
        rawDocument = new RawDocumentSnippet("", "");
        rawDocument.setProperty(RawDocumentsProducer.PROPERTY_CATID, "12");
        rawCluster02.addDocument(rawDocument);

        RawClusterBase rawCluster03 = new RawClusterBase();
        rawCluster03.setScore(0.5);
        rawDocument = new RawDocumentSnippet("", "");
        rawDocument.setProperty(RawDocumentsProducer.PROPERTY_CATID, "13");
        rawCluster03.addDocument(rawDocument);

        RawClusterBase rawCluster04 = new RawClusterBase();
        rawCluster04.setScore(0.5);
        rawDocument = new RawDocumentSnippet("", "");
        rawDocument.setProperty(RawDocumentsProducer.PROPERTY_CATID, "14");
        rawCluster04.addDocument(rawDocument);

        List rawClusters = Arrays.asList(new RawCluster []
        { rawCluster01, rawCluster02, rawCluster03, rawCluster04 });

        // Original clusters
        List originalClusters = new ArrayList();
        RawClusterBase originalCluster;
        originalCluster = new RawClusterBase();
        originalCluster.setProperty(RawDocumentsProducer.PROPERTY_CATID, "12");
        originalClusters.add(originalCluster);
        originalCluster = new RawClusterBase();
        originalCluster.setProperty(RawDocumentsProducer.PROPERTY_CATID, "13");
        originalClusters.add(originalCluster);
        originalCluster = new RawClusterBase();
        originalCluster.setProperty(RawDocumentsProducer.PROPERTY_CATID, "14");
        originalClusters.add(originalCluster);

        Map expectedMetrics = new HashMap();
        expectedMetrics.put(
            TopicCoverageRawClustersMetric.METRIC_EXTENDED_TOPIC_COVERAGE,
            new Double(3 / Math.sqrt(4 * 3)));
        expectedMetrics.put(
            TopicCoverageRawClustersMetric.METRIC_SIMPLE_TOPIC_COVERAGE,
            new Double(3.0 / 3.0));

        assertEquals("Coverage value", expectedMetrics, rawClustersMetric
            .compute(rawClusters, originalClusters));
    }

    /**
     *  
     */
    public void testOptimumCompleteCoverage()
    {
        RawDocumentBase rawDocument;

        // Assessed clusters
        RawClusterBase rawCluster01 = new RawClusterBase();
        rawCluster01.setScore(0.5);
        rawDocument = new RawDocumentSnippet("", "");
        rawDocument.setProperty(RawDocumentsProducer.PROPERTY_CATID, "13");
        rawCluster01.addDocument(rawDocument);
        rawDocument = new RawDocumentSnippet("", "");
        rawDocument.setProperty(RawDocumentsProducer.PROPERTY_CATID, "13");
        rawCluster01.addDocument(rawDocument);

        RawClusterBase rawCluster02 = new RawClusterBase();
        rawCluster02.setScore(0.5);
        rawDocument = new RawDocumentSnippet("", "");
        rawDocument.setProperty(RawDocumentsProducer.PROPERTY_CATID, "12");
        rawCluster02.addDocument(rawDocument);

        RawClusterBase rawCluster04 = new RawClusterBase();
        rawCluster04.setScore(0.5);
        rawDocument = new RawDocumentSnippet("", "");
        rawDocument.setProperty(RawDocumentsProducer.PROPERTY_CATID, "14");
        rawCluster04.addDocument(rawDocument);

        // Original clusters
        List originalClusters = new ArrayList();
        RawClusterBase originalCluster;
        originalCluster = new RawClusterBase();
        originalCluster.setProperty(RawDocumentsProducer.PROPERTY_CATID, "12");
        originalClusters.add(originalCluster);
        originalCluster = new RawClusterBase();
        originalCluster.setProperty(RawDocumentsProducer.PROPERTY_CATID, "13");
        originalClusters.add(originalCluster);
        originalCluster = new RawClusterBase();
        originalCluster.setProperty(RawDocumentsProducer.PROPERTY_CATID, "14");
        originalClusters.add(originalCluster);

        Map expectedMetrics = new HashMap();
        expectedMetrics.put(
            TopicCoverageRawClustersMetric.METRIC_EXTENDED_TOPIC_COVERAGE,
            new Double(1));
        expectedMetrics.put(
            TopicCoverageRawClustersMetric.METRIC_SIMPLE_TOPIC_COVERAGE,
            new Double(1));

        List rawClusters = Arrays.asList(new RawCluster []
        { rawCluster01, rawCluster02, rawCluster04 });
        assertEquals("Coverage value", expectedMetrics, rawClustersMetric
            .compute(rawClusters, originalClusters));
    }
}