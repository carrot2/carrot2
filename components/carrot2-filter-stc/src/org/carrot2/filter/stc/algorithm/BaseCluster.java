
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

package org.carrot2.filter.stc.algorithm;


import java.util.*;


/**
 * A base cluster in the STC algorithm. A base cluster is basically a Node in suffix tree with an
 * associated score and assigned later links to other nodes in base clusters graph.
 */
public final class BaseCluster
{
    /**
     * Indicates whether this base cluster has become part of a merged cluster or is still
     * available.
     */
    private boolean merged = false;

    /** A Node in suffix tree this BaseCluster is associated with. */
    private PhraseNode node;

    /**
     * Getter for node variable
     */
    public PhraseNode getNode()
    {
        return node;
    }

    /** Score of this base cluster (passed to constructor) */
    protected float score;

    /**
     * Getter for score variable
     */
    public float getScore()
    {
        return score;
    }

    /** Base clusters this object has links to in base clusters graph */
    protected List neighbors = null;

    /** id number assigned by the creator of this object */
    protected int id;

    /**
     * Setter for id
     */
    public void setId(int id)
    {
        this.id = id;
    }


    /**
     * getter for id
     */
    public int getId()
    {
        return id;
    }

    /**
     * Public constructor for BaseCluster requires a node and precalculated score
     */
    public BaseCluster(PhraseNode node, float score)
    {
        this.score = score;
        this.node = node;
    }

    /**
     * Adds a link to another BaseCluster (used when constructing base clusters graph
     */
    public void addLink(BaseCluster neighbor)
    {
        if (this.neighbors == null)
        {
            this.neighbors = new ArrayList();
        }

        // TODO: checking for doubled links?
        this.neighbors.add(neighbor);
    }


    /**
     * Retrieves the list of neighbors of this base cluster
     */
    public List getNeighborsList()
    {
        return this.neighbors;
    }

    /** This node's phrase */
    protected Phrase phrase = null;

    /**
     * Getter for this base cluster's phrase object
     */
    public Phrase getPhrase()
    {
        if (phrase == null)
        {
            phrase = new Phrase(this);
        }

        return phrase;
    }

    public void setMerged(boolean merged) {
        this.merged = merged;
    }


    public boolean isMerged() {
        return merged;
    }
}
