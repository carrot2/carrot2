
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


import com.dawidweiss.carrot.filter.stc.suffixtree.*;
import java.util.*;


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
     * Phase 1. Stemming of snippets
     *
     * @param stemmer GenericStemmer object.
     */
    public long stemSnippets(ImmediateStemmer stemmer, StopWordsDetector stopWords)
    {
        long time = 0;
        ImmediateReferenceStemmer refStemmer;
        refStemmer = new ImmediateReferenceStemmer(stemmer);

        // stem snippets
        for (ListIterator z = snippets.listIterator(); z.hasNext();)
        {
            DocReference dr = (DocReference) z.next();

            // stem snippet and update the time.
            long tmp = System.currentTimeMillis();

            refStemmer.process(dr);

            time += (System.currentTimeMillis() - tmp);

            // mark stop-words
            ArrayStemmedSnippet ass = (dr.getStemmedSnippet());

            for (int i = 0; i < ass.size(); i++)
            {
                SuffixableElement sentence = ass.getSentence(i);

                for (int j = 0; j < sentence.size(); j++)
                {
                    if (sentence.get(j) instanceof StemmedTerm)
                    {
                        StemmedTerm t = (StemmedTerm) sentence.get(j);

                        if (stopWords.isStopWord(t.getTerm()))
                        {
                            t.setStopWord(true);
                        }
                    }
                }
            }
        }

        return time;
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
     *
     * @param minBaseClusterScore minimal score of a potential base cluster in order to be added to
     *        base clusters.
     * @param ignoreWordIfInMorePercentOfDocumentsThan a number between 0 and 1, if a word exists
     *        in more snippets than this ratio, it is ignored.
     * @param ignoreWordIfInLessDocumentsThan ignore word if it exists in less documents (number,
     *        not percent!) than specified.
     * @param noMoreBaseClustersThan Trims the base cluster array after N-th position
     * @param minimalGroupSize Minimal documents in a group, if less the base cluster is removed
     */
    public long createBaseClusters(
        float minBaseClusterScore, int ignoreWordIfInLessDocumentsThan,
        float ignoreWordIfInMorePercentOfDocumentsThan, int noMoreBaseClustersThan,
        int minimalGroupSize
    )
    {
        float SCORE_THRESHOLD = minBaseClusterScore;
        int STOPWORD_LESSTHAN = ignoreWordIfInLessDocumentsThan;
        float STOPWORD_MORETHAN = ignoreWordIfInMorePercentOfDocumentsThan;
        long time = System.currentTimeMillis();
        Stack nodes = new Stack();

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
            int m;

            if ((m = current.getSuffixedDocumentsCount()) > 1)
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
                )
                {
                    continue;
                }

                // calculate effective phrase length (number of non stop-list words)
                int mp = 0;

                for (Iterator z = phrase.iterator(); z.hasNext();)
                {
                    StemmedTerm term = (StemmedTerm) z.next();

                    // in stop-list?
                    if (term.isStopWord())
                    {
                        continue;
                    }

                    // in more than 40% or less than 3 documents in a collection?
                    Edge wedge = suffixTree.getRootNode().findEdgeMatchingFirstElement(term);

                    if (wedge != null)
                    {
                        // MUST ALWAYS BE != NULL, but just to make sure... :)
                        int sec = ((PhraseNode) wedge.getEndNode()).getSuffixedDocumentsCount();

                        if (
                            (sec < STOPWORD_LESSTHAN)
                                || (sec > (int) (STOPWORD_MORETHAN * snippets.size()))
                        )
                        {
                            continue;
                        }
                    }

                    mp++;
                }

                // effective length equals zero? Don't take this cluster.
                if (mp == 0)
                {
                    continue;
                }

                // effective length of the phrase corrects the score.
                float fmp;

                if (mp == 1)
                {
                    fmp = 0.5f;
                }
                else if (mp >= 6)
                {
                    fmp = 1;
                }
                else
                {
                    fmp = (float) ((((float) (mp - 1) / (6 - 1)) * 0.5f) + 0.5);
                }

                // calculate base cluster's score
                float score = m * fmp;

                if (score > SCORE_THRESHOLD)
                {
                    baseClusters.add(new BaseCluster(current, score));
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
     * Phase 4. Create merged clusters
     */
    public long createMergedClusters(float MERGE_THRESHOLD)
    {
        long time = System.currentTimeMillis();

        // Create links in base clusters graph
        for (int i = 1; i < baseClusters.size(); i++)
        {
            BaseCluster a;
            BaseCluster b;
            long a_docCount;

            a = (BaseCluster) baseClusters.elementAt(i);
            a_docCount = a.getNode().getSuffixedDocumentsCount();

            for (int j = 0; j < i; j++)
            {
                b = (BaseCluster) baseClusters.elementAt(j);

                double a_and_b_docCount;

                a_and_b_docCount = a.getNode().getInternalDocumentsRepresentation()
                                    .numberOfSetBitsAfterAnd(
                        b.getNode().getInternalDocumentsRepresentation()
                    );

                if (
                    ((a_and_b_docCount / b.getNode().getSuffixedDocumentsCount()) > MERGE_THRESHOLD)
                        && ((a_and_b_docCount / a_docCount) > MERGE_THRESHOLD)
                )
                {
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
            if (((BaseCluster) baseClusters.elementAt(i)).merged == false)
            {
                Cluster c = new Cluster();
                Stack s = new Stack();

                clusters.add(c);
                s.push(baseClusters.elementAt(i));

                while (!s.empty())
                {
                    BaseCluster b = (BaseCluster) s.pop();

                    if (b.merged)
                    {
                        continue;
                    }

                    b.merged = true;

                    c.include(b);

                    if (b.getNeighborsList() != null)
                    {
                        for (Iterator it = b.getNeighborsList().iterator(); it.hasNext();)
                        {
                            b = (BaseCluster) it.next();

                            if (b.merged == false)
                            {
                                s.push(b);
                            }
                        }
                    }
                }
            }
        }

        Arrays.sort(clusters.getInternalArray(), 0, clusters.size());

        return System.currentTimeMillis() - time;
    }
}
