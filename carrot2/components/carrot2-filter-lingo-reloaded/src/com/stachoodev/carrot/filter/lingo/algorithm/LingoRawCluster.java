/*
 * LingoRawCluster.java Created on 2004-06-17
 */
package com.stachoodev.carrot.filter.lingo.algorithm;

import java.util.*;

import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.util.common.*;

/**
 * @author stachoo
 */
public class LingoRawCluster implements RawCluster, PropertyProvider
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
     * The score of this cluster. The value of this property must be an instance
     * of {@link Double}
     */
    public final static String PROPERTY_SCORE = "score";

    /**
     * 
     */
    public LingoRawCluster()
    {
        this.labels = new ArrayList();
        this.documents = new ArrayList();
        this.subclusters = new ArrayList();
        this.propertyHelper = new PropertyHelper();
    }

    /**
     * @return
     */
    public double getScore()
    {
        return propertyHelper.getDoubleProperty(PROPERTY_SCORE);
    }

    /**
     * @param score
     */
    public void setScore(double score)
    {
        propertyHelper.setDoubleProperty(PROPERTY_SCORE, score);
    }

    /**
     * @param rawDocument
     */
    public void addDocument(RawDocument rawDocument)
    {
        documents.add(rawDocument);
    }

    /**
     * @param label
     */
    public void addLabel(String label)
    {
        labels.add(label);
    }

    /**
     * @param rawCluster
     */
    public void addSubcluster(RawCluster rawCluster)
    {
        subclusters.add(rawCluster);
    }

    /**
     * @return
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
    public int getIntProperty(String propertyName)
    {
        return propertyHelper.getIntProperty(propertyName);
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
    public double getDoubleProperty(String propertyName)
    {
        return propertyHelper.getDoubleProperty(propertyName);
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