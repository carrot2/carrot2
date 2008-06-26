package org.carrot2.clustering.stc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.carrot2.text.suffixtrees.ExtendedBitSet;

/**
 * A merged cluster (composition of {@link org.carrot2.filter.stc.algorithm.BaseCluster}s).
 */
@SuppressWarnings("unchecked")
public class MergedCluster implements Comparable
{
    /** Merged base clusters list. */
    private ArrayList baseClusters = new ArrayList();

    /** Total score of all merged base clusters. */
    private float score = 0;

    /** All description phrases of this cluster (without pruning) */
    private List allPhrases;

    /** Number of documents in cluster */
    private final ExtendedBitSet documents = new ExtendedBitSet();

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

    public void createDescription(final STCClusteringParameters parameters)
    {
        this.allPhrases = this.createDescriptionPhrases(parameters.maxPhraseOverlap,
            parameters.mostGeneralPhraseCoverage);
    }

    /**
     * Implementation of Comparable interface
     */
    public int compareTo(Object ob)
    {
        float obScore = ((MergedCluster) ob).score;

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
     * Returns all phrases describing this cluster (union of phrases of base clusters).
     */
    public List getAllPhrases()
    {
        return allPhrases;
    }

    /**
     * Returns all phrases selected for the description.
     */
    public List getDescriptionPhrases()
    {
        final List all = getAllPhrases();

        int max = 0;
        for (; max < all.size(); max++)
        {
            final Phrase phrase = (Phrase) all.get(max);
            if (!phrase.isSelected())
            {
                break;
            }
        }
        if (max == 0)
        {
            // Prevent against clusters with no selected phrases
            // (this should perhaps be an error?)
            max = Math.min(1, all.size());
        }
        return all.subList(0, max);
    }

    /**
     * Creates a list of meaningful (sometimes ;) phrases describing this cluster For the
     * heuristics used in this step refer to Oren Zamir and Oren Etzioni's article about
     * Grouper.
     * 
     * @param maxPhraseOverlap Maximum overlap between selected description phrases (if
     *            exceeding, the subphrase is removed from the selection).
     * @param minMostGeneralPhraseCoverage Minimal difference between coverage of
     *            most-general and most-specific phrases in order for most-general phrase
     *            to be displayed (phase 3 of pruning heuristic)
     */
    private List createDescriptionPhrases(final double maxPhraseOverlap,
        final double minMostGeneralPhraseCoverage)
    {
        final ArrayList phrases = new ArrayList();

        // collect all possible choices first.
        for (Iterator l = getBaseClustersList().iterator(); l.hasNext();)
        {
            BaseCluster b = (BaseCluster) l.next();
            Phrase p = b.getPhrase();

            phrases.add(p);

            // calculate coverage
            p.setCoverage((float) b.getNode().getSuffixedDocumentsCount()
                / documents.numberOfSetBits());
        }

        // pruning step 2: removal of sub and super phrases
        // initially phrases are marked both most general and most specific
        // we'll set them to false if the condition doesn't hold.
        for (int i = 0; i < phrases.size(); i++)
        {
            final Phrase current = (Phrase) phrases.get(i);
            phraseLoop: for (int j = 0; j < phrases.size(); j++)
            {
                Phrase comp = (Phrase) phrases.get(j);

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

                    // "current" is a subphrase of "comp" hence current is not
                    // most-specific and
                    // comp is not most-general
                    current.mostSpecific = false;
                    comp.mostGeneral = false;
                }
            }
        }

        // pruning step 3: Most general phrase with low coverage
        for (int i = 0; i < phrases.size(); i++)
        {
            Phrase current = (Phrase) phrases.get(i);

            if (current.mostGeneral)
            {
                notThisPhrase: for (int j = 0; j < phrases.size(); j++)
                {
                    Phrase comp = (Phrase) phrases.get(j);

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

                        // does most-general phrase have at least 20% higher coverage? if
                        // not,
                        // don't display it.
                        if ((current.getCoverage() - comp.getCoverage()) < minMostGeneralPhraseCoverage)
                        {
                            current.setSelected(false);

                            break;
                        }
                    }
                }
            }
        }

        // final pass and deletion of irrelevant phrases (neither ms nor mg).
        for (int i = 0; i < phrases.size(); i++)
        {
            Phrase current = (Phrase) phrases.get(i);

            if (!current.mostGeneral && !current.mostSpecific)
            {
                current.setSelected(false);
            }
        }

        // pruning step 1. Word overlap - remove selected phrases with
        // words overlap higher than 60% and lower coverage. This was originally
        // the first step of pruning, however proved not to work well.
        for (int i = 0; i < phrases.size(); i++)
        {
            Phrase current = (Phrase) phrases.get(i);

            for (int j = 0; j < phrases.size(); j++)
            {
                Phrase comp = (Phrase) phrases.get(j);

                if ((i != j) && current.isSelected() && comp.isSelected()
                    && (current.getCoverage() < comp.getCoverage()))
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

                    // mark for removal if overlap exceeds the threshold
                    if ((overlap / total) > maxPhraseOverlap)
                    {
                        current.setSelected(false);
                    }
                }
            }
        }

        // Sort phrases. Selected with highest coverage first.
        final Object [] objects = phrases.toArray();
        Arrays.sort(objects, 0, objects.length, new Comparator()
        {
            public int compare(Object a, Object b)
            {
                Phrase pa = (Phrase) a;
                Phrase pb = (Phrase) b;

                if ((pa.isSelected() && pb.isSelected())
                    || (!pa.isSelected() && !pb.isSelected()))
                {
                    if (pa.getCoverage() > pb.getCoverage())
                    {
                        return -1;
                    }
                    else if (pa.getCoverage() < pb.getCoverage())
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
                    if (pa.isSelected())
                    {
                        return -1;
                    }
                    else
                    {
                        return 1;
                    }
                }
            }
        });

        return java.util.Arrays.asList(objects);
    }

    public ExtendedBitSet getDocuments()
    {
        return this.documents;
    }
}
