

/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.dawidweiss.carrot.filter.stc.algorithm;


import com.dawidweiss.carrot.filter.stc.suffixtree.*;
import java.util.*;


/**
 * A cluster (after merging base clusters).
 */
public class Cluster
    implements Comparable
{
    /** Merged base clusters list. */
    protected ArrayList baseClusters = new ArrayList();

    /** Score of all merged clusters. */
    protected float score = 0;

    /** Description phrases */
    protected List phrases = null;

    /** Number of documents in cluster */
    public ExtendedBitSet documents = new ExtendedBitSet();

    /**
     * Getter for score variable
     */
    public float getScore()
    {
        return score;
    }


    public float updateScore()
    {
        float score = 0;

        for (int i = 0; i < baseClusters.size(); i++)
        {
            score += ((BaseCluster) baseClusters.get(i)).getScore();
        }

        this.score = score;

        return score;
    }


    /**
     * Adds a base cluster to this cluster and updates the Cluster score.
     */
    public void include(BaseCluster b)
    {
        baseClusters.add(b);
        updateScore();
        documents.or(b.getNode().getInternalDocumentsRepresentation());
    }


    /**
     * Implementation of Comparable interface
     */
    public int compareTo(Object ob)
    {
        float obScore = ((Cluster) ob).score;

        if (score < obScore)
        {
            return 1;
        }
        else if (score > obScore)
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }


    /**
     * Returns a list of base clusters
     */
    public List getBaseClustersList()
    {
        return baseClusters;
    }


    /**
     * Retrieves the meaningful phrases describing this cluster
     */
    public List getPhrases()
    {
        if (phrases == null)
        {
            phrases = createDescriptionPhrases(0.6, 0.2);
        }

        return phrases;
    }


    /**
     * Creates a list of meaningful (sometimes ;) phrases describing this cluster For the
     * heuristics used in this step refer to Oren Zamir and Oren Etzioni's article about Grouper.
     *
     * @param OVERLAP_THRESHOLD Overlap threshold for step 2 of phrase pruning heuristic
     * @param MIN_DIFFERENCE_THRESHOLD Minimal difference between coverage of most-general and
     *        most-specific phrases in order for most-general phrase to be displayed (phase 3 of
     *        pruning heuristic)
     */
    public synchronized List createDescriptionPhrases(
        double OVERLAP_THRESHOLD, double MIN_DIFFERENCE_THRESHOLD
    )
    {
        ArrayList phrases = new ArrayList();

        // collect all possible choices first.
        for (Iterator l = getBaseClustersList().iterator(); l.hasNext();)
        {
            BaseCluster b = (BaseCluster) l.next();
            BaseCluster.Phrase p = b.getPhrase();

            phrases.add(p);

            // calculate coverage
            p.coverage = (float) b.getNode().getSuffixedDocumentsCount() / documents
                .numberOfSetBits();
        }

        // pruning step 2: removal of sub and super phrases
        // initially phrases are marked both most general and most specific
        // we'll set them to false if the condition doesn't hold.
        for (int i = 0; i < phrases.size(); i++)
        {
            BaseCluster.Phrase current = (BaseCluster.Phrase) phrases.get(i);

phraseLoop: 
            for (int j = 0; j < phrases.size(); j++)
            {
                BaseCluster.Phrase comp = (BaseCluster.Phrase) phrases.get(j);

                if (i != j)
                {
                    // check if current is a subphrase of comp
                    for (Iterator wrd = current.getTerms().iterator(); wrd.hasNext();)
                    {
                        StemmedTerm s = (StemmedTerm) wrd.next();

                        if (comp.getTerms().contains(s) == false)
                        {
                            continue phraseLoop;
                        }
                    }

                    // "current" is a subphrase of "comp" hence current is not most-specific and
                    // comp is not most-general
                    current.mostSpecific = false;
                    comp.mostGeneral = false;
                }
            }
        }

        // pruning step 3: Most general phrase with low coverage
        for (int i = 0; i < phrases.size(); i++)
        {
            BaseCluster.Phrase current = (BaseCluster.Phrase) phrases.get(i);

            if (current.mostGeneral)
            {
notThisPhrase: 
                for (int j = 0; j < phrases.size(); j++)
                {
                    BaseCluster.Phrase comp = (BaseCluster.Phrase) phrases.get(j);

                    if ((i != j) && comp.mostSpecific)
                    {
                        // check if current is a subphrase of comp
                        for (Iterator wrd = current.getTerms().iterator(); wrd.hasNext();)
                        {
                            StemmedTerm s = (StemmedTerm) wrd.next();

                            if (!s.isStopWord())
                            {
                                if (comp.getTerms().contains(s) == false)
                                {
                                    continue notThisPhrase;
                                }
                            }
                        }

                        // does most-general phrase have at least 20% higher coverage? if not,
                        // don't display it.
                        if ((current.coverage - comp.coverage) < MIN_DIFFERENCE_THRESHOLD)
                        {
                            current.selected = false;

                            break;
                        }
                    }
                }
            }
        }

        // final pass and deletion of irrelevant phrases (neither ms nor mg).
        for (int i = 0; i < phrases.size(); i++)
        {
            BaseCluster.Phrase current = (BaseCluster.Phrase) phrases.get(i);

            if (!current.mostGeneral && !current.mostSpecific)
            {
                current.selected = false;
            }
        }

        // pruning step 1. Word overlap - remove selected phrases with
        // words overlap higher than 60% and lower coverage. This was originally
        // the first step of pruning, however proved not to work well.
        for (int i = 0; i < phrases.size(); i++)
        {
            BaseCluster.Phrase current = (BaseCluster.Phrase) phrases.get(i);

            for (int j = 0; j < phrases.size(); j++)
            {
                BaseCluster.Phrase comp = (BaseCluster.Phrase) phrases.get(j);

                if (
                    (i != j) && current.selected && comp.selected
                        && (current.coverage < comp.coverage)
                )
                {
                    // check words overlap.
                    float overlap = 0;
                    float total = 0;

                    for (Iterator wrd = current.getTerms().iterator(); wrd.hasNext();)
                    {
                        StemmedTerm s = (StemmedTerm) wrd.next();

                        if (!s.isStopWord())
                        {
                            total++;

                            if (comp.getTerms().contains(s))
                            {
                                overlap += 1.0;
                            }
                        }
                    }

                    // mark for removal if overlap exceeds 60%
                    if ((overlap / total) > OVERLAP_THRESHOLD)
                    {
                        current.selected = false;
                    }
                }
            }
        }

        // sort phrases. Selected with highest coverage first.
        Object [] objects = phrases.toArray();
        Arrays.sort(
            objects, 0, objects.length,
            new Comparator()
            {
                public int compare(Object a, Object b)
                {
                    BaseCluster.Phrase pa = (BaseCluster.Phrase) a;
                    BaseCluster.Phrase pb = (BaseCluster.Phrase) b;

                    if ((pa.selected && pb.selected) || (!pa.selected && !pb.selected))
                    {
                        if (pa.coverage > pb.coverage)
                        {
                            return -1;
                        }
                        else if (pa.coverage < pb.coverage)
                        {
                            return 1;
                        }
                        else
                        {
                            return 0;
                        }
                    }
                    else
                    {
                        if (pa.selected)
                        {
                            return -1;
                        }
                        else
                        {
                            return 1;
                        }
                    }
                }
            }
        );

        return java.util.Arrays.asList(objects);
    }
}
