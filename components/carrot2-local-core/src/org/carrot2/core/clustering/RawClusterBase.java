
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

package org.carrot2.core.clustering;

import java.util.*;

import org.carrot2.util.PropertyProviderBase;

/**
 * A complete base implementation of the {@link RawCluster}interface.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class RawClusterBase extends PropertyProviderBase implements RawCluster
{
    /** This cluster's labels */
    private List labels;

    /** This cluster's subclusters */
    private List subclusters;

    /** Documents belonging to this cluster */
    private List documents;

    /**
     * Creates a new cluster with no labels, no documents, no subclusters and no
     * properties;
     */
    public RawClusterBase()
    {
        this.labels = new ArrayList();
        this.documents = new ArrayList();
        this.subclusters = new ArrayList();
    }

    /**
     * Returns all properties of this cluster.
     * 
     * @return all properties of this cluster.
     */
    public Map getProperties()
    {
        return super.getProperties();
    }

    /**
     * A convenience method returning this cluster's score or -1 if the score
     * has not been set.
     * 
     * @return this cluster's score or -1 if the score has not been set.
     */
    public double getScore()
    {
        return getDoubleProperty(RawCluster.PROPERTY_SCORE, -1);
    }

    /**
     * A convenience method setting score for this cluster.
     * 
     * @param score score to be set
     */
    public void setScore(double score)
    {
        setDoubleProperty(RawCluster.PROPERTY_SCORE, score);
    }

    /**
     * Adds a document to this cluster.
     * 
     * @param rawDocument the document to be added.
     */
    public void addDocument(RawDocument rawDocument)
    {
        documents.add(rawDocument);
    }

    /**
     * Adds a label to this cluster.
     * 
     * @param label the label to be added.
     */
    public void addLabel(String label)
    {
        labels.add(label);
    }

    /**
     * Adds a subcluster to this cluster.
     * 
     * @param rawCluster the subcluster to be added.
     */
    public void addSubcluster(RawCluster rawCluster)
    {
        subclusters.add(rawCluster);
    }

    /**
     * Returns a multiline String representing this cluster's content, including
     * the contents of this cluster's documents.
     * 
     * @return this cluster's content
     */
    public String getFullInfo()
    {
        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append(labels.get(0));
        stringBuffer.append(" (" + documents.size() + ")\n");
        for (Iterator iter = documents.iterator(); iter.hasNext();)
        {
            RawDocument document = (RawDocument) iter.next();
            stringBuffer.append("\t");
            stringBuffer.append(document.toString());
            stringBuffer.append("\n");
        }

        return stringBuffer.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.clustering.RawCluster#getClusterDescription()
     */
    public List getClusterDescription()
    {
        return labels;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.clustering.RawCluster#getSubclusters()
     */
    public List getSubclusters()
    {
        return subclusters;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.clustering.RawCluster#getDocuments()
     */
    public List getDocuments()
    {
        return documents;
    }
}