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
package com.dawidweiss.carrot.core.local.clustering;

import java.util.*;

import com.stachoodev.util.common.*;

/**
 * A complete base implementation of the {@link RawCluster}interface.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class RawClusterBase implements RawCluster, PropertyProvider
{
    /** This cluster's labels */
    private List labels;

    /** This cluster's subclusters */
    private List subclusters;

    /** Documents belonging to this cluster */
    private List documents;

    /** Properties of this cluster */
    private PropertyHelper propertyHelper;

    /**
     * Creates a new cluster with no labels, no documents, no subclusters and no
     * properties;
     */
    public RawClusterBase()
    {
        this.labels = new ArrayList();
        this.documents = new ArrayList();
        this.subclusters = new ArrayList();
        this.propertyHelper = new PropertyHelper();
    }

    /**
     * Returns all properties of this cluster.
     * 
     * @return all properties of this cluster.
     */
    public Map getProperties()
    {
        return propertyHelper.getProperties();
    }

    /**
     * A convenience method returning this cluster's score or -1 if the score
     * has not been set.
     * 
     * @return this cluster's score or -1 if the score has not been set.
     */
    public double getScore()
    {
        return propertyHelper.getDoubleProperty(RawCluster.PROPERTY_SCORE, -1);
    }

    /**
     * A convenience method setting score for this cluster.
     * 
     * @param score score to be set
     */
    public void setScore(double score)
    {
        propertyHelper.setDoubleProperty(RawCluster.PROPERTY_SCORE, score);
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
     * @see com.dawidweiss.carrot.core.local.clustering.RawCluster#getClusterDescription()
     */
    public List getClusterDescription()
    {
        return labels;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.clustering.RawCluster#getSubclusters()
     */
    public List getSubclusters()
    {
        return subclusters;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.clustering.RawCluster#getDocuments()
     */
    public List getDocuments()
    {
        return documents;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.clustering.RawCluster#getProperty(java.lang.String)
     */
    public Object getProperty(String propertyName)
    {
        return propertyHelper.getProperty(propertyName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.clustering.RawCluster#setProperty(java.lang.String,
     *      java.lang.Object)
     */
    public Object setProperty(String propertyName, Object value)
    {
        return propertyHelper.setProperty(propertyName, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.util.common.PropertyProvider#getIntProperty(java.lang.String)
     */
    public int getIntProperty(String propertyName, int defaultValue)
    {
        return propertyHelper.getIntProperty(propertyName, defaultValue);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.util.common.PropertyProvider#setIntProperty(java.lang.String,
     *      int)
     */
    public Object setIntProperty(String propertyName, int value)
    {
        return propertyHelper.setIntProperty(propertyName, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.util.common.PropertyProvider#getDoubleProperty(java.lang.String)
     */
    public double getDoubleProperty(String propertyName, double defaultValue)
    {
        return propertyHelper.getDoubleProperty(propertyName, defaultValue);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.util.common.PropertyProvider#setDoubleProperty(java.lang.String,
     *      double)
     */
    public Object setDoubleProperty(String propertyName, double value)
    {
        return propertyHelper.setDoubleProperty(propertyName, value);
    }

}