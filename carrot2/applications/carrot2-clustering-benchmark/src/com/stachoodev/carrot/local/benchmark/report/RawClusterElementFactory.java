/*
 * RawClusterElementFactory.java
 * 
 * Created on 2004-06-30
 */
package com.stachoodev.carrot.local.benchmark.report;

import java.util.*;

import org.dom4j.*;

import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.util.common.*;
import com.stachoodev.carrot.filter.lingo.algorithm.*;

/**
 * @author stachoo
 */
public class RawClusterElementFactory implements ElementFactory
{
    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.carrot.local.benchmark.report.ElementFactory#createElement(java.lang.Object)
     */
    public Element createElement(Object object)
    {
        RawCluster rawCluster = (RawCluster) object;
        Element rawClusterElement = DocumentHelper.createElement("raw-cluster");

        // Cluster size
        rawClusterElement.addElement("size").addText(
            Integer.toString(clusterSize(rawCluster)));

        // Cluster score
        if (rawCluster.getProperty(LingoRawCluster.PROPERTY_SCORE) != null)
        {
            rawClusterElement.addElement("score").addText(
                StringUtils.toString((Double) rawCluster
                    .getProperty(LingoRawCluster.PROPERTY_SCORE), "#.##"));
        }

        // Labels
        rawClusterElement.add(XMLReportUtils.createListElement(rawCluster
            .getClusterDescription(), "labels", "label"));

        // Subclusters
        if (rawCluster.getSubclusters() != null)
        {
            rawClusterElement.add(XMLReportUtils.createListElement(rawCluster
                .getSubclusters(), "raw-clusters", "to-string-entry"));
        }

        // Raw documents
        if (rawCluster.getDocuments() != null)
        {
            rawClusterElement.add(XMLReportUtils.createListElement(rawCluster
                .getDocuments(), "raw-documents", "to-string-entry"));
        }

        return rawClusterElement;
    }

    /**
     * Calculates real size of a cluster including documents in all nested
     * subclusters.
     * 
     * @param rawCluster
     * @return
     */
    private int clusterSize(RawCluster rawCluster)
    {
        int size = rawCluster.getDocuments().size();
        List clusters = rawCluster.getSubclusters();
        for (Iterator clustersIter = clusters.iterator(); clustersIter
            .hasNext();)
        {
            RawCluster cluster = (RawCluster) clustersIter.next();
            size += clusterSize(cluster);
        }

        return size;
    }
}