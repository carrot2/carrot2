/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.metrics;

import com.dawidweiss.carrot.core.local.clustering.*;

import junit.framework.*;

import java.util.*;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class ContaminationRawClustersMetricTest extends TestCase
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
        rawClustersMetric = new ContaminationRawClustersMetric();
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
        RawClusterBase rawCluster02 = new RawClusterBase();
        RawClusterBase rawCluster03 = new RawClusterBase();

        rawCluster01.setProperty(RawDocumentsProducer.PROPERTY_CATID, "1");
        rawCluster02.setProperty(RawDocumentsProducer.PROPERTY_CATID, "2");
        rawCluster03.setProperty(RawDocumentsProducer.PROPERTY_CATID, "3");

        Map metrics = new HashMap();
        metrics.put(ContaminationRawClustersMetric.METRIC_AVG_CONTAMINATION,
            new Double(0.0));
        metrics.put(ContaminationRawClustersMetric.METRIC_MIN_CONTAMINATION,
            new Double(0.0));
        metrics.put(ContaminationRawClustersMetric.METRIC_MAX_CONTAMINATION,
            new Double(0.0));
        metrics.put(
            ContaminationRawClustersMetric.METRIC_STD_DEV_CONTAMINATION,
            new Double(0.0));

        List rawClusters = Arrays.asList(new RawCluster []
        { rawCluster01, rawCluster02, rawCluster03 });

        assertEquals("Zero metrics", metrics, rawClustersMetric.compute(
            rawClusters, rawClusters));
    }

    /**
     *  
     */
    public void testZeroContamination()
    {
        RawClusterBase rawCluster = new RawClusterBase();
        rawCluster.addLabel("12");
        RawDocumentBase rawDocument;

        for (int i = 0; i < 3; i++)
        {
            rawDocument = new RawDocumentSnippet("", "");
            rawDocument.setProperty(RawDocumentsProducer.PROPERTY_CATID, "12");
            rawCluster.addDocument(rawDocument);
            rawCluster.setProperty(RawDocumentsProducer.PROPERTY_CATID, "12");
        }

        List rawClusters = Arrays.asList(new RawCluster []
        { rawCluster });
        rawClustersMetric.compute(rawClusters, rawClusters);

        assertEquals("Zero conatmination measure", 0, rawCluster
            .getDoubleProperty(
                ContaminationRawClustersMetric.PROPERTY_CONTAMINATION, -1), 0.0);
    }

    /**
     *  
     */
    public void testMaxContamination()
    {
        // Original clusters
        RawClusterBase rawCluster = new RawClusterBase();
        List originalRawClusters = new ArrayList();
        for (int i = 0; i < 3; i++)
        {
            rawCluster = new RawClusterBase();
            rawCluster.setProperty(RawDocumentsProducer.PROPERTY_CATID, Integer
                .toString(100 - 5 * i));
            originalRawClusters.add(rawCluster);
        }

        // Measured clusters
        RawDocumentBase rawDocument;
        for (int i = 0; i < 3; i++)
        {
            rawDocument = new RawDocumentSnippet("", "");
            rawDocument.setProperty(RawDocumentsProducer.PROPERTY_CATID,
                Integer.toString(100 - 5 * i));
            rawCluster.addDocument(rawDocument);
        }
        List measuredRawClusters = Arrays.asList(new RawCluster []
        { rawCluster });

        rawClustersMetric.compute(measuredRawClusters, originalRawClusters);

        assertEquals("Maximum conatmination measure", 1.0, rawCluster
            .getDoubleProperty(
                ContaminationRawClustersMetric.PROPERTY_CONTAMINATION, -1), 0.0);
    }
}