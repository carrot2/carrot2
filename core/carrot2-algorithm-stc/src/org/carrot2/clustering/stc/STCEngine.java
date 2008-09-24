package org.carrot2.clustering.stc;

import java.util.*;

import org.carrot2.text.suffixtrees.Edge;

/**
 * A rough implementation of Suffix Tree Algorithm (O. Zamir, O. Etzioni). The steps to
 * perform STC with this class are as follows:
 * <ol>
 * <li> create the object of this class. </li>
 * <li> perform stemming and stop-words marking </li>
 * <li> create suffix tree and base clusters </li>
 * <li> merge base clusters. </li>
 * </ol>
 * In many of the above steps you will need additional objects like a stemmer or
 * stop-words filter.
 */
@SuppressWarnings("unchecked")
public class STCEngine
{
    /** Suffix tree of the base clusters */
    protected STCTree suffixTree = new STCTree();

    /** Base clusters vector */
    protected FastVector baseClusters;

    /** Merged clusters list */
    protected FastVector clusters;

    /**
     * Numbed of documents passed to {@link #createSuffixTree(StemmedTerm[][])}.
     */
    private int documentCount;

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
     * Phase 2. Creation of base clusters. [todo] An interface for the tree object should
     * be created at some time in the future. As for now only
     * util.suffixtrees.GeneralizedSuffixTree is used.
     */
    public long createSuffixTree(StemmedTerm [][] documentData)
    {
        long time = 0;

        this.documentCount = documentData.length;

        // Add stemmed sentences to suffix tree
        for (StemmedTerm [] document : documentData)
        {
            long tmp = System.currentTimeMillis();

            ArrayStemmedSnippet ass = new ArrayStemmedSnippet(document);
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
    public long createBaseClusters(final STCClusteringParameters params)
    {
        final float minBaseClusterScore = (float) params.minBaseClusterScore;
        final int ignoreIfInFewerDocs = params.ignoreWordIfInFewerDocs;
        final float ignoreIfInHigherDocsPercent = (float) params.ignoreWordIfInHigherDocsPercent;

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

                // TODO: Possibility of improvement. In the original Grouper
                // implementation,
                // authors choose only 'trimmed' sentences to insert into the suffix tree.
                // Trimmed sentences are those which start and end with a non-stopword.
                // However
                // to make use of Ukkonen's algorithm, we need to insert all substrings of
                // a sentence into the suffix tree, not only those starting and ending
                // with
                // a non-stop term.
                List phrase = current.getPhrase();

                if ((phrase.size() > 0)
                    && (((StemmedTerm) phrase.get(0)).isStopWord() || ((StemmedTerm) phrase
                        .get(phrase.size() - 1)).isStopWord()))
                {
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
                    final Edge wedge = suffixTree.getRootNode()
                        .findEdgeMatchingFirstElement(term);
                    if (wedge != null)
                    {
                        // MUST ALWAYS BE != NULL, but just to make sure... :)
                        final int sec = ((PhraseNode) wedge.getEndNode())
                            .getSuffixedDocumentsCount();
                        if ((sec < ignoreIfInFewerDocs)
                            || (sec > (int) (ignoreIfInHigherDocsPercent * documentCount)))
                        {
                            continue;
                        }
                    }

                    effectivePhraseLength++;
                }

                // effective length equals zero? Don't take this cluster.
                if (effectivePhraseLength == 0)
                {
                    continue;
                }

                // The phrase length is corrected with a function. The original
                // STC algorithm uses linear gradient. I modify it here to penalize
                // very long phrases (which usually denote repeated snippets).
                final float score = calculateModifiedBaseClusterScore(
                    effectivePhraseLength, suffixedDocumentsCount, params);
                // final float score =
                // calculateOriginalBaseClusterScore(effectivePhraseLength,
                // suffixedDocumentsCount);

                if (score > minBaseClusterScore)
                {
                    final BaseCluster baseCluster = new BaseCluster(current, score);
                    baseClusters.add(baseCluster);
                }
            }
        }

        // sort base clusters according to their score
        Arrays.sort(baseClusters.getInternalArray(), 0, baseClusters.size(),
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
            });

        // Take only N first base clusters and only those existing in more than
        // X documents
        final int noMoreBaseClustersThan = params.maxBaseClusters;
        final int minimalGroupSize = params.minBaseClusterSize;
        for (int i = 0; i < baseClusters.lastIndex(); i++)
        {
            if (((BaseCluster) baseClusters.elementAt(i)).getNode()
                .getSuffixedDocumentsCount() < minimalGroupSize)
            {
                baseClusters.eraseElementAt(i);
                continue;
            }

            if (i >= noMoreBaseClustersThan)
            {
                baseClusters.eraseElementAt(i);
                continue;
            }
            
            if (((BaseCluster) baseClusters.elementAt(i)).getPhrase().getTerms().size() >
                params.maxDescPhraseLength)
            {
                baseClusters.eraseElementAt(i);
                continue;
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
     * <p>
     * Modified base cluster scoring formula.
     * <p>
     * The boost is calculated as a gaussian function of density around the "optimum"
     * expected phrase length (average) and "tolerance" towards shorter and longer phrases
     * (standard deviation). You can draw this score multiplier's characteristic with
     * gnuplot. One word-phrases can be given a fixed boost, if
     * {@link STCConstants#SINGLE_TERM_BOOST} is greater than zero.
     * 
     * <pre>
     * reset
     * 
     * set xrange [0:10]
     * set yrange [0:]
     * set samples 11
     * set boxwidth 1 absolute
     * 
     * set xlabel &quot;Phrase length&quot;
     * set ylabel &quot;Score multiplier&quot;
     * 
     * set border 3
     * set key noautotitles
     * 
     * set grid
     * 
     * set xtics border nomirror 1
     * set ytics border nomirror
     * set ticscale 1.0
     * show tics
     * 
     * set size ratio .5
     * 
     * # Base cluster boost function.
     * boost(x) = exp(-(x - optimal) * (x - optimal) / (2 * tolerance * tolerance)) 
     * 
     * plot optimal=2, tolerance=2, boost(x) with histeps title &quot;optimal=2, tolerance=2&quot;, \
     *      optimal=2, tolerance=4, boost(x) with histeps title &quot;optimal=2, tolerance=4&quot;, \
     *      optimal=2, tolerance=6, boost(x) with histeps title &quot;optimal=2, tolerance=6&quot;
     * 
     * pause -1
     * </pre>
     * 
     * @param phraseLength Effective phrase length (number of non-stopwords).
     * @param documentCount Number of documents this phrase occurred in.
     * @return Returns the base cluster score calculated as a function of the number of
     *         documents the phrase occurred in and a function of the effective length of
     *         the phrase.
     */
    private float calculateModifiedBaseClusterScore(final int phraseLength,
        final int documentCount, final STCClusteringParameters params)
    {
        final double singleTermBoost = params.singleTermBoost;
        final int phraseLengthOptimum = params.optimalPhraseLength;
        final double phraseLengthTolerance = params.optimalPhraseLengthDev;
        final double documentCountBoost = params.documentCountBoost;

        final double boost;
        if (phraseLength == 1 && singleTermBoost > 0)
        {
            boost = singleTermBoost;
        }
        else
        {
            final int tmp = phraseLength - phraseLengthOptimum;
            boost = Math.exp((-tmp * tmp)
                / (2 * phraseLengthTolerance * phraseLengthTolerance));
        }

        return (float) (boost * (documentCount * documentCountBoost));
    }

    /**
     * Phase 4. Create merged clusters
     */
    public long createMergedClusters(final STCClusteringParameters parameters)
    {
        final float MERGE_THRESHOLD = (float) parameters.mergeThreshold;
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

                final double a_and_b_docCount = a.getNode()
                    .getInternalDocumentsRepresentation().numberOfSetBitsAfterAnd(
                        b.getNode().getInternalDocumentsRepresentation());

                // BUG: This check should be bidirectional (see Zamir's paper).
                if (((a_and_b_docCount / b.getNode().getSuffixedDocumentsCount()) > MERGE_THRESHOLD)
                    && ((a_and_b_docCount / a_docCount) > MERGE_THRESHOLD))
                {
                    // add links to base cluster graph. This is actually redundant as
                    // we're adding two
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

                    if (baseCluster.isMerged())
                    {
                        continue;
                    }

                    baseCluster.setMerged(true);
                    mergedCluster.include(baseCluster);

                    if (baseCluster.getNeighborsList() != null)
                    {
                        for (Iterator it = baseCluster.getNeighborsList().iterator(); it
                            .hasNext();)
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
