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

import cern.colt.list.*;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class LingoCluster implements Comparable
{
    /** Cluster label */
    private LingoClusterLabel label;

    /** Cluster members */
    private List members;

    /** Cluster members */
    private List subClusters;

    /** */
    private IntArrayList documentIndices;

    /** */
    private boolean junkCluster;

    /** */
    private LingoCluster parent;

    /** Cluster score */
    private double score;

    /**
     * @param parent
     * @param label
     * @param junkCluster
     */
    public LingoCluster(LingoCluster parent, LingoClusterLabel label,
        boolean junkCluster)
    {
        this(parent, label, junkCluster, null);
    }

    /**
     * @param parent
     * @param label
     * @param junkCluster
     * @param members
     */
    public LingoCluster(LingoCluster parent, LingoClusterLabel label,
        boolean junkCluster, IntArrayList documentIndices)
    {
        this.parent = parent;
        this.label = label;
        this.junkCluster = junkCluster;
        this.documentIndices = documentIndices;
        
        this.members = new ArrayList();
        this.subClusters = new ArrayList();
    }

    /**
     * @param member
     */
    public void addMember(LingoClusterMember member)
    {
        members.add(member);
        documentIndices = null;
    }

    /**
     * @param member
     */
    public void addSubclusters(List lingoClusters)
    {
        subClusters.addAll(lingoClusters);
    }

    /**
     * Returns this LingoCluster's <code>members</code>.
     * 
     * @return
     */
    public List getMembers()
    {
        return members;
    }

    /**
     * Returns this LingoCluster's <code>subClusters</code>.
     * 
     * @return 
     */
    public List getSubClusters()
    {
        return subClusters;
    }
    
    /**
     *  
     */
    public void sortMembers()
    {
        Collections.sort(members);
        documentIndices = null;
    }

    /**
     * 
     */
    public void clearMembers()
    {
        members.clear();
        documentIndices = null;
    }
    
    /**
     * Returns this LingoCluster's <code>label</code>.
     * 
     * @return
     */
    public LingoClusterLabel getLabel()
    {
        return label;
    }

    /**
     * Returns this LingoCluster's <code>junkCluster</code>.
     * 
     * @return
     */
    public boolean isJunkCluster()
    {
        return junkCluster;
    }

    /**
     * Returns this LingoCluster's <code>documentIndices</code>.
     * 
     * @return
     */
    public IntArrayList getDocumentIndices()
    {
        if (documentIndices == null)
        {
            documentIndices = new IntArrayList(members.size());
            for (Iterator iter = members.iterator(); iter.hasNext();)
            {
                LingoClusterMember member = (LingoClusterMember) iter.next();
                documentIndices.add(member.getDocumentIndex());
            }
        }
        return documentIndices;
    }

    /**
     * Returns this LingoCluster's <code>parent</code>.
     * 
     * @return
     */
    public LingoCluster getParent()
    {
        return parent;
    }

    /**
     * Returns this LingoCluster's <code>score</code>.
     * 
     * @return
     */
    public double getScore()
    {
        return score;
    }

    /**
     * Sets this LingoCluster's <code>score</code>.
     * 
     * @param score
     */
    public void setScore(double score)
    {
        this.score = score;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o)
    {
        LingoCluster other = (LingoCluster) o;

        if (score < other.score)
        {
            return 1;
        }
        else if (score > other.score)
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }
}