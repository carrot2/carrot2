
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

package com.dawidweiss.carrot.filter.stc.algorithm;


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
    public boolean merged = false;

    /** A Node in suffix tree this BaseCluster is associated with. */
    protected PhraseNode node;

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
     * Getter for this node's phrase object
     */
    public Phrase getPhrase()
    {
        if (phrase == null)
        {
            phrase = new Phrase();
        }

        return phrase;
    }

    /**
     * Phrase class being a description of this base cluster.
     */
    public class Phrase
    {
        /**
         * Percent of documents in a merged cluster this phrase exists in (in use in Cluster class)
         */
        public float coverage;

        /** Most specific phrase flag. */
        public boolean mostSpecific;

        /** Most general phrase flag. */
        public boolean mostGeneral;

        /**
         * Phrase selected for displaying. All phrases are initially marked as selected, pruning
         * process turns this flag off.
         */
        public boolean selected;

        /** Terms of this phrase */
        protected Collection phrase;

        /**
         * Construction of Phrase objects allowed only within package
         */
        protected Phrase()
        {
            phrase = BaseCluster.this.getNode().getPhrase();
            mostSpecific = true;
            mostGeneral = true;
            selected = true;
        }

        /**
         * Returns the collection of phrase terms (StemmedTerm objects)
         */
        public Collection getTerms()
        {
            return phrase;
        }


        /**
         * Returns the collection of phrase terms, formatted to a string.
         */
        public String userFriendlyTerms()
        {
            StringBuffer s = new StringBuffer();
            Collection terms = getTerms();

            for (Iterator i = terms.iterator(); i.hasNext();)
            {
                final StemmedTerm t = (StemmedTerm) i.next();
                final String image = t.getTerm();
                if (s.length() > 0 && 
                        !(",".equals(image) || "?".equals(image)
                                || "!".equals(image)
                                || ";".equals(image))) {
                    s.append(' ');
                }
                s.append(image);
            }

            return s.toString();
        }


        /**
         * Returns the BaseCluster of this phrase
         */
        public BaseCluster getBaseCluster()
        {
            return BaseCluster.this;
        }


        public String toString()
        {
            return "[cid=" + BaseCluster.this.getNode().id + ",d="
            + getNode().getSuffixedDocumentsCount() + ",c=" + coverage + ",("
            + (selected ? "S "
                        : "  ") + (mostSpecific ? "MS "
                                                : "   ") + (mostGeneral ? "MG"
                                                                        : "  ") + ") : "
            + getTerms() + "]";
        }
    }
}
