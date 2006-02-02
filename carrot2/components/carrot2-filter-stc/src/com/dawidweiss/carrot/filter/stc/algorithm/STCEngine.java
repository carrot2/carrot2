
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

import com.dawidweiss.carrot.filter.stc.StcParameters;
import com.dawidweiss.carrot.filter.stc.suffixtree.Edge;


/**
 * A rough implementation of Suffix Tree Algorithm (O. Zamir, O. Etzioni). The steps to perform STC
 * with this class are as follows:
 * 
 * <ol>
 * <li>
 * create the object of this class.
 * </li>
 * <li>
 * perform stemming and stop-words marking
 * </li>
 * <li>
 * create suffix tree and base clusters
 * </li>
 * <li>
 * merge base clusters.
 * </li>
 * </ol>
 * 
 * In many of the above steps you will need additional objects like a stemmer or stop-words filter.
 */
public class STCEngine
{
    /** Source List of snippets */
    protected List snippets;

    /** Suffix tree of the base clusters */
    protected STCTree suffixTree;

    /** Base clusters vector */
    protected FastVector baseClusters;

    /** Merged clusters list */
    protected FastVector clusters;

    public STCEngine(List snippets)
    {
        this.snippets = snippets;
        suffixTree = new STCTree();
    }

    public List getBaseClusters()
    {
        baseClusters.removeGaps();

        return baseClusters;
    }

    public List getClusters()
    {
        clusters.removeGaps();

        return clusters;
    }

    /**
     * Phase 2. Creation of base clusters. [todo] An interface for the tree object should be
     * created at some time in the future. As for now only util.suffixtrees.GeneralizedSuffixTree
     * is used.
     */
    public long createSuffixTree()
    {
        long time = 0;

        // Add stemmed sentences to suffix tree
        for (ListIterator z = snippets.listIterator(); z.hasNext();)
        {
            DocReference dr = (DocReference) z.next();

            // add sentence suffixes to suffix tree
            long tmp = System.currentTimeMillis();
            ArrayStemmedSnippet ass = dr.getStemmedSnippet();
            ass.trimEdgeStopWords();

            for (int i = 0; i < ass.size(); i++)
            {
                suffixTree.add(ass.getSentence(i));
            }

            suffixTree.nextDocument();

            time += (System.currentTimeMillis() - tmp);
        }

        return time;
    }


    /**
     * Phase 3. Create base clusters from the suffix tree.
     */
    public long createBaseClusters(final StcParameters params)
    {
        final float minBaseClusterScore = params.getMinBaseClusterScore();
        final int ignoreIfInFewerDocs = params.getIgnoreWordIfInFewerDocs();
        final float ignoreIfInHigherDocsPercent = params.getIgnoreWordIfInHigherDocsPercent();

        final long time = System.currentTimeMillis();
        final Stack nodes = new Stack();

        baseClusters = new FastVector(1000, 500);

        // push root node to the stack.
        if (suffixTree.getRootNode() == null)
        {
            return 0;
        }

        for (Iterator r = suffixTree.getRootNode().getEdgesIterator(); r.hasNext();)
        {
            nodes.push(((Edge) r.next()).getEndNode());
        }

        // traverse the tree.
        while (!nodes.empty())
        {
            PhraseNode current = (PhraseNode) nodes.pop();
            final int suffixedDocumentsCount = current.getSuffixedDocumentsCount();
            if (suffixedDocumentsCount > 1)
            {
                // push subnodes to the processing stack.
                for (Iterator r = current.getEdgesIterator(); r.hasNext();)
                {
                    PhraseNode p = (PhraseNode) ((Edge) r.next()).getEndNode();

                    if (!p.isEOSOnly())
                    {
                        nodes.push(p);
                    }
                }

                // TODO: Possibility of improvement. In the original Grouper implementation,
                // authors choose only 'trimmed' sentences to insert into the suffix tree.
                // Trimmed sentences are those which start and end with a non-stopword. However
                // to make use of Ukkonen's algorithm, we need to insert all substrings of
                // a sentence into the suffix tree, not only those starting and ending with
                // a non-stop term.
                List phrase = current.getPhrase();

                if (
                    (phrase.size() > 0)
                        && (((StemmedTerm) phrase.get(0)).isStopWord()
                        || ((StemmedTerm) phrase.get(phrase.size() - 1)).isStopWord())
                ) {
                    continue;
                }

                // calculate effective phrase length (number of non stop-list words)
                int effectivePhraseLength = 0;
                for (Iterator z = phrase.iterator(); z.hasNext();)
                {
                    final StemmedTerm term = (StemmedTerm) z.next();
                    // is in stop-list?
                    if (term.isStopWord())
                    {
                        continue;
                    }

                    // in more than 40% or less than 3 documents in a collection?
                    final Edge wedge = suffixTree.getRootNode().findEdgeMatchingFirstElement(term);
                    if (wedge != null)
                    {
                        // MUST ALWAYS BE != NULL, but just to make sure... :)
                        final int sec = ((PhraseNode) wedge.getEndNode()).getSuffixedDocumentsCount();
                        if ((sec < ignoreIfInFewerDocs)
                                || (sec > (int) (ignoreIfInHigherDocsPercent * snippets.size()))) {
                            continue;
                        }
                    }

                    effectivePhraseLength++;
                }

                // effective length equals zero? Don't take this cluster.
                if (effectivePhraseLength == 0) {
                    continue;
                }

                // The phrase length is corrected with a function. The original
                // STC algorithm uses linear gradient. I modify it here to penalize
                // very long phrases (which usually denote repeated snippets). 
                final float score = calculateModifiedBaseClusterScore(effectivePhraseLength, suffixedDocumentsCount);
                // final float score = calculateOriginalBaseClusterScore(effectivePhraseLength, suffixedDocumentsCount);

                if (score > minBaseClusterScore) {
                    final BaseCluster baseCluster = new BaseCluster(current, score);
                    baseClusters.add(baseCluster);
                }
            }
        }

        // sort base clusters according to their score
        Arrays.sort(
            baseClusters.getInternalArray(), 0, baseClusters.size(),
            new Comparator()
            {
                public int compare(Object a, Object b)
                {
                    BaseCluster ca = (BaseCluster) a;
                    BaseCluster cb = (BaseCluster) b;

                    if (ca.getScore() > cb.getScore())
                    {
                        return -1;
                    }
                    else if (ca.getScore() < cb.getScore())
                    {
                        return 1;
                    }
                    else
                    {
                        return 0;
                    }
                }
            }
        );


        // take only N first base clusters and only those existing in more than
        // X documents
        final int noMoreBaseClustersThan = params.getMaxBaseClusters();
        final int minimalGroupSize = params.getMinBaseClusterSize();
        for (int i = 0; i < baseClusters.lastIndex(); i++)
        {
            if (
                ((BaseCluster) baseClusters.elementAt(i)).getNode().getSuffixedDocumentsCount() < minimalGroupSize
            )
            {
                baseClusters.eraseElementAt(i);
            }

            if (i >= noMoreBaseClustersThan)
            {
                baseClusters.eraseElementAt(i);
            }
        }

        baseClusters.removeGaps();

        // assign id's for faster access
        for (int i = 0; i < baseClusters.size(); i++)
        {
            ((BaseCluster) baseClusters.elementAt(i)).setId(i);
        }

        return System.currentTimeMillis() - time;
    }


    /**
     * Modified base cluster scoring formula.
     * 
     * A formula with an exponential penalty for phrases around the "optimum" expected
     * phrase length. You can draw this score multiplier's characteristic
     * with gnuplot. One word-phrases are always set to boost 0.5.
     * <pre>
     * reset
     * set xrange [0:10]
     * set yrange [0:]
     * 
     * set xlabel "Phrase length"
     * set ylabel "Score multiplier"
     * 
     * set border 3
     * set boxwidth 0.6
     * set key off
     * 
     * set grid
     * 
     * set xtics border nomirror 1
     * set ytics border nomirror
     * set ticscale 1.0
     * show tics
     * 
     * avg = 3
     * dev = 2
     * 
     * plot exp(-(x - avg) * (x - avg) / (2 * dev * dev))
     * 
     * replot
     * </pre>
     */
    private float calculateModifiedBaseClusterScore(final int effectivePhraseLength, final int documentCount) {
        final double SINGLE_WORD_BOOST = 0.5f;
        final int optimalPhraseLength = 3;
        final int optimalPhraseLengthDev = 2;

        final double boost;
        if (effectivePhraseLength == 1) {
            boost = SINGLE_WORD_BOOST;
        } else {
            final int tmp = effectivePhraseLength - optimalPhraseLength;
            boost = Math.exp((-tmp * tmp) / (double) (2*optimalPhraseLengthDev*optimalPhraseLengthDev));
        }

        return (float) (boost * documentCount);
    }

    /**
     * Calculates base cluster score using the original formula used in STC paper. 
     */
    private float calculateOriginalBaseClusterScore(final int effectivePhraseLength, final int documentCount) {
        // Original STC base cluster scoring formula.
        final double SINGLE_WORD_BOOST = 0.5f;
        double boost;
        if (effectivePhraseLength == 1) {
            boost = SINGLE_WORD_BOOST;
        } else if (effectivePhraseLength >= 6) {
            boost = 1;
        } else {
            boost = (((float) (effectivePhraseLength - 1) / (6 - 1)) * 0.5f) + 0.5f;
        }
        return (float) boost * documentCount;
    }

    /**
     * Phase 4. Create merged clusters
     */
    public long createMergedClusters(final StcParameters parameters)
    {
        final float MERGE_THRESHOLD = parameters.getMergeThreshold();
        final long time = System.currentTimeMillis();

        // Create links in base clusters graph
        for (int i = 1; i < baseClusters.size(); i++)
        {
            BaseCluster a;
            BaseCluster b;
            
            a = (BaseCluster) baseClusters.elementAt(i);
            final long a_docCount = a.getNode().getSuffixedDocumentsCount();

            for (int j = 0; j < i; j++)
            {
                b = (BaseCluster) baseClusters.elementAt(j);

                final double a_and_b_docCount = a.getNode().getInternalDocumentsRepresentation()
                                    .numberOfSetBitsAfterAnd(
                        b.getNode().getInternalDocumentsRepresentation()
                    );

                // BUG: This check should be bidirectional (see Zamir's paper).
                if (((a_and_b_docCount / b.getNode().getSuffixedDocumentsCount()) > MERGE_THRESHOLD)
                        && ((a_and_b_docCount / a_docCount) > MERGE_THRESHOLD)) {
                    // add links to base cluster graph. This is actually redundant as we're adding two
                    // directed edges.
                    a.addLink(b);
                    b.addLink(a);
                }
            }
        }

        // merge base clusters and create final clusters
        clusters = new FastVector();
        for (int i = 0; i < baseClusters.size(); i++)
        {
            if (((BaseCluster) baseClusters.elementAt(i)).isMerged() == false)
            {
                final MergedCluster mergedCluster = new MergedCluster();
                clusters.add(mergedCluster);

                final Stack s = new Stack();
                s.push(baseClusters.elementAt(i));

                while (!s.empty())
                {
                    BaseCluster baseCluster = (BaseCluster) s.pop();

                    if (baseCluster.isMerged()) {
                        continue;
                    }

                    baseCluster.setMerged(true);
                    mergedCluster.include(baseCluster);

                    if (baseCluster.getNeighborsList() != null)
                    {
                        for (Iterator it = baseCluster.getNeighborsList().iterator(); it.hasNext();)
                        {
                            baseCluster = (BaseCluster) it.next();

                            if (baseCluster.isMerged() == false)
                            {
                                s.push(baseCluster);
                            }
                        }
                    }
                }
                // Create merged cluster's description. 
                mergedCluster.createDescription(parameters);
            }
        }

        Arrays.sort(clusters.getInternalArray(), 0, clusters.size());

        return System.currentTimeMillis() - time;
    }
}
