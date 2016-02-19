
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.clustering.stc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.carrot2.clustering.stc.GeneralizedSuffixTree.SequenceBuilder;
import org.carrot2.core.Cluster;
import org.carrot2.core.Document;
import org.carrot2.core.IClusteringAlgorithm;
import org.carrot2.core.LanguageCode;
import org.carrot2.core.ProcessingComponentBase;
import org.carrot2.core.ProcessingException;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.attribute.CommonAttributes;
import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Internal;
import org.carrot2.core.attribute.Processing;
import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.text.analysis.TokenTypeUtils;
import org.carrot2.text.clustering.IMonolingualClusteringAlgorithm;
import org.carrot2.text.clustering.MultilingualClustering;
import org.carrot2.text.linguistic.ILexicalData;
import org.carrot2.text.preprocessing.LabelFormatter;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.text.preprocessing.pipeline.BasicPreprocessingPipeline;
import org.carrot2.text.preprocessing.pipeline.IPreprocessingPipeline;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.AttributeLevel;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.DefaultGroups;
import org.carrot2.util.attribute.Group;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Label;
import org.carrot2.util.attribute.Level;
import org.carrot2.util.attribute.Output;
import org.carrot2.util.attribute.Required;
import org.carrot2.util.attribute.constraint.DoubleRange;
import org.carrot2.util.attribute.constraint.ImplementingClasses;
import org.carrot2.util.attribute.constraint.IntRange;

import com.carrotsearch.hppc.BitSet;
import com.carrotsearch.hppc.BitSetIterator;
import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntStack;
import org.carrot2.shaded.guava.common.base.Predicate;
import org.carrot2.shaded.guava.common.collect.Collections2;
import org.carrot2.shaded.guava.common.collect.Lists;
import org.carrot2.shaded.guava.common.collect.Maps;

/**
 * Suffix Tree Clustering (STC) algorithm. Pretty much as described in: <i>Oren Zamir,
 * Oren Etzioni, Grouper: A Dynamic Clustering Interface to Web Search Results, 1999.</i>
 * Some liberties were taken wherever STC's description was not clear enough or where we
 * thought some improvements could be made.
 */
@Bindable(prefix = "STCClusteringAlgorithm", inherit = CommonAttributes.class)
@Label("STC Clustering")
public final class STCClusteringAlgorithm extends ProcessingComponentBase implements
    IClusteringAlgorithm
{
    /** {@link Group} name. */
    private final static String BASE_CLUSTERS = "Base clusters";
    /** {@link Group} name. */
    private final static String MERGING_AND_OUTPUT = "Merging and output";

    /**
     * Query that produced the documents. The query will help the algorithm to create
     * better clusters. Therefore, providing the query is optional but desirable.
     */
    @Processing
    @Input
    @Internal
    @Attribute(key = AttributeNames.QUERY, inherit = true)
    public String query = null;

    /**
     * Documents to cluster.
     */
    @Processing
    @Input
    @Required
    @Internal
    @Attribute(key = AttributeNames.DOCUMENTS, inherit = true)
    public List<Document> documents;

    /**
     * Clusters created by the algorithm.
     */
    @Processing
    @Output
    @Internal
    @Attribute(key = AttributeNames.CLUSTERS, inherit = true)
    public List<Cluster> clusters = null;

    /**
     * Minimum word-document recurrences.
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 2)
    @Level(AttributeLevel.MEDIUM)
    @Group(DefaultGroups.WORD_FILTERING)
    public int ignoreWordIfInFewerDocs = 2;

    /**
     * Maximum word-document ratio. A number between 0 and 1, if a word exists in more
     * snippets than this ratio, it is ignored.
     */
    @Processing
    @Input
    @Attribute
    @DoubleRange(min = 0, max = 1)
    @Level(AttributeLevel.MEDIUM)
    @Group(DefaultGroups.WORD_FILTERING)
    public double ignoreWordIfInHigherDocsPercent = 0.9d;

    /**
     * Minimum base cluster score.
     */
    @Processing
    @Input
    @Attribute
    @DoubleRange(min = 0, max = 10)
    @Level(AttributeLevel.ADVANCED)
    @Group(BASE_CLUSTERS)
    public double minBaseClusterScore = 2.0d;

    /**
     * Maximum base clusters count. Trims the base cluster array after N-th position for
     * the merging phase.
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 2)
    @Level(AttributeLevel.ADVANCED)
    @Group(BASE_CLUSTERS)
    public int maxBaseClusters = 300;

    /**
     * Minimum documents per base cluster.
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 2, max = 20)
    @Level(AttributeLevel.ADVANCED)
    @Group(BASE_CLUSTERS)
    public int minBaseClusterSize = 2;

    /**
     * Maximum final clusters.
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 1)
    @Level(AttributeLevel.BASIC)
    @Group(MERGING_AND_OUTPUT)
    public int maxClusters = 15;

    /**
     * Base cluster merge threshold.
     */
    @Processing
    @Input
    @Attribute
    @DoubleRange(min = 0, max = 1)
    @Level(AttributeLevel.ADVANCED)
    @Group(MERGING_AND_OUTPUT)
    public double mergeThreshold = 0.6d;

    /**
     * Maximum cluster phrase overlap.
     */
    @Processing
    @Input
    @Attribute
    @DoubleRange(min = 0, max = 1)
    @Level(AttributeLevel.ADVANCED)
    @Group(DefaultGroups.LABELS)
    public double maxPhraseOverlap = 0.6d;

    /**
     * Minimum general phrase coverage. Minimum phrase coverage to appear in cluster
     * description.
     */
    @Processing
    @Input
    @Attribute
    @DoubleRange(min = 0, max = 1)
    @Level(AttributeLevel.ADVANCED)
    @Group(DefaultGroups.LABELS)
    public double mostGeneralPhraseCoverage = 0.5d;

    /**
     * Maximum words per label. Base clusters formed by phrases with more words than this
     * ratio are trimmed.
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 1)
    @Level(AttributeLevel.BASIC)
    @Group(DefaultGroups.LABELS)    
    public int maxDescPhraseLength = 4;

    /**
     * Maximum phrases per label. Maximum number of phrases from base clusters promoted
     * to the cluster's label.
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 1)
    @Level(AttributeLevel.BASIC)
    @Group(DefaultGroups.LABELS)
    public int maxPhrases = 3;

    /**
     * Single term boost. A factor in calculation of the base cluster score. If greater
     * then zero, single-term base clusters are assigned this value regardless of the
     * penalty function.
     */
    @Processing
    @Input
    @Attribute
    @DoubleRange(min = 0)
    @Level(AttributeLevel.MEDIUM)
    @Group(BASE_CLUSTERS)    
    public double singleTermBoost = 0.5d;

    /**
     * Optimal label length. A factor in calculation of the base cluster score.
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 1)
    @Level(AttributeLevel.BASIC)
    @Group(BASE_CLUSTERS)
    public int optimalPhraseLength = 3;

    /**
     * Phrase length tolerance. A factor in calculation of the base cluster score.
     */
    @Processing
    @Input
    @Attribute
    @DoubleRange(min = 0.5)
    @Level(AttributeLevel.MEDIUM)
    @Group(BASE_CLUSTERS)
    public double optimalPhraseLengthDev = 2.0d;

    /**
     * Document count boost. A factor in calculation of the base cluster score, boosting
     * the score depending on the number of documents found in the base cluster.
     */
    @Processing
    @Input
    @Attribute
    @DoubleRange(min = 0)
    @Level(AttributeLevel.MEDIUM)
    @Group(BASE_CLUSTERS)
    public double documentCountBoost = 1.0d;
    
    /**
     * Common preprocessing tasks handler.
     */
    @Init
    @Input
    @Attribute
    @Internal
    @ImplementingClasses(classes = {
        BasicPreprocessingPipeline.class
    }, strict = false)
    @Level(AttributeLevel.ADVANCED)
    public IPreprocessingPipeline preprocessingPipeline = new BasicPreprocessingPipeline();

    /**
     * Balance between cluster score and size during cluster sorting. Value equal to 0.0
     * will sort clusters based only on cluster size. Value equal to 1.0
     * will sort clusters based only on cluster score.
     */
    @Input
    @Processing
    @Attribute
    @DoubleRange(min = 0.0, max = 1.0)
    @Label("Size-Score sorting ratio")
    @Level(AttributeLevel.MEDIUM)
    @Group(DefaultGroups.CLUSTERS)
    public double scoreWeight = 1.0;

    /**
     * Merge all stem-equivalent base clusters before running the merge phase.
     * 
     * @see "http://issues.carrot2.org/browse/CARROT-1008"
     */
    @Input
    @Processing
    @Attribute
    @Label("Merge all stem-equivalent phrases when discovering base clusters")
    @Level(AttributeLevel.MEDIUM)
    @Group(DefaultGroups.CLUSTERS)
    public boolean mergeStemEquivalentBaseClusters = true;

    /**
     * A helper for performing multilingual clustering.
     */
    public final MultilingualClustering multilingualClustering = new MultilingualClustering();

    /**
     * Stores the preprocessing context during {@link #process()}.
     */
    PreprocessingContext context;
    
    /**
     * Suffix tree and suffix tree input during {@link #process()}.
     */
    GeneralizedSuffixTree.SequenceBuilder sb;

    /**
     * Helper class for computing merged cluster labels.
     * 
     * @see STCClusteringAlgorithm#merge
     */
    private static final class PhraseCandidate
    {
        final ClusterCandidate cluster;
        final float coverage;
        
        /** If <code>false</code> the phrase should not be selected (various criteria). */
        boolean selected = true;
    
        /** @see STCClusteringAlgorithm#markSubSuperPhrases(ArrayList) */
        boolean mostGeneral = true;
    
        /** @see STCClusteringAlgorithm#markSubSuperPhrases(ArrayList) */
        boolean mostSpecific = true;
        
        PhraseCandidate(ClusterCandidate c, float coverage)
        {
            this.cluster = c;
            this.coverage = coverage;
        }
    }

    /**
     * Returns a collection of {@link PhraseCandidate}s that have
     * {@link PhraseCandidate#selected} set to <code>false</code>. 
     */
    private final static Predicate<PhraseCandidate> notSelected = new Predicate<PhraseCandidate>()
    {
        public boolean apply(PhraseCandidate p)
        {
            return !p.selected;
        }
    };

    /**
     * Performs STC clustering of {@link #documents}.
     */
    @Override
    public void process() throws ProcessingException
    {
        // There is a tiny trick here to support multilingual clustering without
        // refactoring the whole component: we remember the original list of documents
        // and invoke clustering for each language separately within the 
        // IMonolingualClusteringAlgorithm implementation below. This is safe because
        // processing components are not thread-safe by definition and 
        // IMonolingualClusteringAlgorithm forbids concurrent execution by contract.
        final List<Document> originalDocuments = documents;
        clusters = multilingualClustering.process(documents,
            new IMonolingualClusteringAlgorithm()
            {
                public List<Cluster> process(List<Document> documents,
                    LanguageCode language)
                {
                    STCClusteringAlgorithm.this.documents = documents;
                    STCClusteringAlgorithm.this.cluster(language);
                    return STCClusteringAlgorithm.this.clusters;
                }
            });
        documents = originalDocuments;
    }

    /**
     * Performs the actual clustering with an assumption that all documents are written in
     * one <code>language</code>.
     */
    private void cluster(LanguageCode language)
    {
        clusters = new ArrayList<Cluster>();

        /*
         * Step 1. Preprocessing: tokenization, stop word marking and stemming (if available).
         */
        context = preprocessingPipeline.preprocess(documents, query, language);

        /*
         * Step 2: Create a generalized suffix tree from phrases in the input.
         */
        sb = new GeneralizedSuffixTree.SequenceBuilder();

        final int [] tokenIndex = context.allTokens.wordIndex;
        final short [] tokenType = context.allTokens.type;
        for (int i = 0; i < tokenIndex.length; i++)
        {
            /* Advance until the first real token. */
            if (tokenIndex[i] == -1)
            {
                if ((tokenType[i] & (ITokenizer.TF_SEPARATOR_DOCUMENT | ITokenizer.TF_TERMINATOR)) != 0)
                {
                    sb.endDocument();
                }
                continue;
            }

            /* We have the first token. Advance until non-token. */
            final int s = i;

            while (tokenIndex[i + 1] != -1) i++;
            final int phraseLength = 1 + i - s; 
            if (phraseLength >= 1)
            {
                /* We have a phrase. */
                sb.addPhrase(tokenIndex, s, phraseLength);
            }
        }
        sb.buildSuffixTree();

        /*
         * Step 3: Find "base" clusters by looking up frequently recurring phrases in the 
         * generalized suffix tree.
         */
        List<ClusterCandidate> baseClusters = createBaseClusters(sb);

        /*
         * Step 4: Merge base clusters that overlap too much to form final clusters.
         */
        List<ClusterCandidate> mergedClusters = createMergedClusters(baseClusters);

        /*
         * Step 5: Create the junk (unassigned documents) cluster and create the final
         * set of clusters in Carrot2 format.
         */
        postProcessing(mergedClusters);
    }

    /**
     * Memory cleanups.
     */
    @Override
    public void afterProcessing()
    {
        super.afterProcessing();
        this.context = null;
        this.sb = null;
    }

    /**
     * Create <i>base clusters</i>. Base clusters are frequently occurring words and
     * phrases. We extract them by walking the generalized suffix tree constructed for
     * each phrase, and extracting paths from those internal tree states, that occurred in
     * more than one document.
     */
    private List<ClusterCandidate> createBaseClusters(SequenceBuilder sb)
    {
        /*
         * Collect all phrases that will form base clusters, 
         * initially filtered to fulfill the minimum acceptance criteria.
         */
        final List<ClusterCandidate> candidates = Lists.newArrayList();

        // Walk the internal nodes of the suffix tree.
        new GeneralizedSuffixTree.Visitor(sb, minBaseClusterSize) {
            protected void visit(int state, int cardinality, 
                BitSet documents, IntStack path)
            {
                // Check minimum base cluster cardinality.
                assert cardinality >= minBaseClusterSize;

                /*
                 * Consider certain special cases of internal suffix tree nodes.  
                 */
                if (!checkAcceptablePhrase(path))
                {
                    return;
                }

                // Calculate "effective phrase length", which is the number of non-stopwords.
                final int effectivePhraseLen = effectivePhraseLength(path);
                if (effectivePhraseLen == 0)
                {
                    return;
                }

                /*
                 * Calculate base cluster's score as a function of effective phrase's length.
                 * STC originally used a linear gradient, we modified it to penalize very long
                 * phrases (which usually correspond to duplicated snippets anyway). 
                 */
                final float score = baseClusterScore(effectivePhraseLen, cardinality);
                candidates.add(
                    new ClusterCandidate(path.toArray(), 
                        (BitSet) documents.clone(), cardinality, score));
            }
        }.visit();

        /*
         * Combine all phrases that are stem-equivalent into one candidate.
         */
        if (mergeStemEquivalentBaseClusters)
        {
            mergeStemEquivalentBaseClusters(sb, candidates);
        }

        /*
         * Remove any base clusters that fall below the minimum score.
         */
        int j = 0;
        for (int max = candidates.size(), i = 0; i < max; i++) 
        {
            ClusterCandidate cc = candidates.get(i);
            if (cc.score >= minBaseClusterScore) {
                candidates.set(j++, cc);
            }
        }
        candidates.subList(j, candidates.size()).clear();

        /*
         * We limit the number of base clusters to the one requested by the user.
         * First we sort by the base clusters score, then pick the top-K entries,
         * filtering out any stop labels on the way.
         */
        Collections.sort(candidates, new Comparator<ClusterCandidate>()
        {
            @Override
            public int compare(ClusterCandidate c1, ClusterCandidate c2)
            {
                return -Float.compare(c1.score, c2.score);
            }
        });

        j = 0;
        ILexicalData lexicalData = context.language.getLexicalData();
        for (int max = candidates.size(), i = 0; i < max && j < maxBaseClusters; i++) 
        {
            ClusterCandidate cc = candidates.get(i);
            // Build the candidate cluster's label for filtering. This may be costly so
            // we only do this for base clusters which are promoted to merging phase.
            assert cc.phrases.size() == 1;
            if (!lexicalData.isStopLabel(buildLabel(cc.phrases.get(0))))
            {
                candidates.set(j++, cc);
            }
        }

        if (j < candidates.size())
        {
            candidates.subList(j, candidates.size()).clear();
            assert candidates.size() == j; 
        }

        return candidates;
    }

    /* */
    private void mergeStemEquivalentBaseClusters(SequenceBuilder sb, final List<ClusterCandidate> candidates)
    {
        // Look for candidates to merge.
        Map<IntArrayList, ClusterCandidate> merged = Maps.newHashMap();
        int j = 0;
        for (int max = candidates.size(), i = 0; i < max; i++)
        {
            ClusterCandidate cc = candidates.get(i);
            candidates.set(j, cc);

            // Convert word indices to stem indices.
            assert cc.phrases.size() == 1;
            int [] stemIndices = context.allWords.stemIndex;
            int [] phraseWords = cc.phrases.get(0);
            IntArrayList stemList = new IntArrayList(phraseWords.length);
            for (int seqIndex : phraseWords)
            {
                int termIndex = sb.input.get(seqIndex);
                stemList.add(stemIndices[termIndex]);
            }
            
            // Check if we have stem-equivalent phrase like this.
            ClusterCandidate equivalent = merged.get(stemList);
            if (equivalent == null)
            {
                merged.put(stemList, cc);
                j++;
            }
            else
            {
                // Merge the two candidates. The surface form with the highest cardinality
                // is taken as the representation of an equivalence group.
                if (equivalent.cardinality < cc.cardinality)
                {
                    equivalent.cardinality = cc.cardinality;
                    equivalent.phrases.add(0, cc.phrases.get(0));
                }
                else
                {
                    equivalent.phrases.add(cc.phrases.get(0));
                }

                // Collect actual documents to recompute cardinality later on.
                equivalent.documents.or(cc.documents);
            }
        }

        // Trim to only include shifted merged candidates.
        candidates.subList(j, candidates.size()).clear();

        // Recalculate score after merging.
        IntStack scratch = new IntStack();
        for (ClusterCandidate cc : candidates)
        {
            if (cc.phrases.size() > 1)
            {
                cc.cardinality = (int) cc.documents.cardinality();
                scratch.buffer = cc.phrases.get(0);
                scratch.elementsCount = scratch.buffer.length;
                cc.score = baseClusterScore(
                    effectivePhraseLength(scratch),
                    cc.cardinality);

                // Clear any other phrase variants. 
                cc.phrases.subList(1, cc.phrases.size()).clear();
            }
        }
    }

    /**
     * Create final clusters by merging base clusters and pruning their labels. Cluster
     * merging is a greedy process of compacting clusters with document sets that overlap
     * by a certain ratio. In other words, phrases that "cover" nearly identical document
     * sets will be conflated.
     */
    private ArrayList<ClusterCandidate> createMergedClusters(List<ClusterCandidate> baseClusters)
    {
        /*
         * Calculate overlap between base clusters first, saving adjacency lists for
         * each base cluster.
         */

        // [i] - next neighbor or END, [i + 1] - neighbor cluster index.
        final int END = -1;
        final IntStack neighborList = new IntStack();
        neighborList.push(END);
        final int [] neighbors = new int [baseClusters.size()];
        final float m = (float) mergeThreshold;
        for (int i = 0; i < baseClusters.size(); i++)
        {
            for (int j = i + 1; j < baseClusters.size(); j++)
            {
                final ClusterCandidate c1 = baseClusters.get(i);
                final ClusterCandidate c2 = baseClusters.get(j);

                final float a = c1.cardinality;
                final float b = c2.cardinality;
                final float c = BitSet.intersectionCount(c1.documents, c2.documents);

                if (c / a > m && c / b > m)
                {
                    neighborList.push(neighbors[i], j);
                    neighbors[i] = neighborList.size() - 2;
                    neighborList.push(neighbors[j], i);
                    neighbors[j] = neighborList.size() - 2;
                }
            }
        }

        /*
         * Find connected components in the similarity graph using Tarjan's algorithm
         * (flattened to use the stack instead of recursion).
         */

        final int NO_INDEX = -1;
        final int [] merged = new int [baseClusters.size()];
        Arrays.fill(merged, NO_INDEX);

        final ArrayList<ClusterCandidate> mergedClusters = 
            Lists.newArrayListWithCapacity(baseClusters.size());
        final IntStack stack = new IntStack(baseClusters.size());
        final IntStack mergeList = new IntStack(baseClusters.size());
        int mergedIndex = 0;
        for (int v = 0; v < baseClusters.size(); v++)
        {
            if (merged[v] != NO_INDEX) continue;

            // Recursively mark all connected components from an unmerged cluster.
            stack.push(v);
            while (stack.size() > 0)
            {
                final int c = stack.pop();

                assert merged[c] == NO_INDEX || merged[c] == mergedIndex;
                if (merged[c] == mergedIndex) continue;

                merged[c] = mergedIndex;
                mergeList.push(c);

                for (int i = neighbors[c]; neighborList.get(i) != END;)
                {
                    final int neighbor = neighborList.get(i + 1);
                    if (merged[neighbor] == NO_INDEX)
                    {
                        stack.push(neighbor);
                    }
                    else
                    {
                        assert merged[neighbor] == mergedIndex;
                    }
                    i = neighborList.get(i);
                }
            }
            mergedIndex++;

            /*
             * Aggregate documents from each base cluster of the current merge, compute 
             * the score and labels.
             */
            mergedClusters.add(merge(mergeList, baseClusters));
            mergeList.clear();
        }

        /*
         * Sort merged clusters.
         */
        Collections.sort(mergedClusters, new Comparator<ClusterCandidate>() {
            public int compare(ClusterCandidate c1, ClusterCandidate c2) {
                if (c1.score < c2.score) return 1;
                if (c1.score > c2.score) return -1;
                if (c1.cardinality < c2.cardinality) return 1;
                if (c1.cardinality > c2.cardinality) return -1;
                return 0;
            };
        });
        
        if (mergedClusters.size() > maxClusters)
        {
            mergedClusters.subList(maxClusters, mergedClusters.size()).clear();
        }

        return mergedClusters;
    }

    /**
     * Merge a list of base clusters into one.
     */
    private ClusterCandidate merge(IntStack mergeList, 
        List<ClusterCandidate> baseClusters)
    {
        assert mergeList.size() > 0;
        final ClusterCandidate result = new ClusterCandidate(); 

        /*
         * Merge documents from all base clusters and update the score.
         */
        for (int i = 0; i < mergeList.size(); i++)
        {
            final ClusterCandidate cc = baseClusters.get(mergeList.get(i));
            result.documents.or(cc.documents);
            result.score += cc.score;
        }
        result.cardinality = (int) result.documents.cardinality();

        /*
         * Combine cluster labels and try to find the best description for the cluster.
         */
        final ArrayList<PhraseCandidate> phrases = 
            new ArrayList<PhraseCandidate>(mergeList.size());
        for (int i = 0; i < mergeList.size(); i++)
        {
            final ClusterCandidate cc = baseClusters.get(mergeList.get(i));
            final float coverage = cc.cardinality / (float) result.cardinality;
            phrases.add(new PhraseCandidate(cc, coverage));
        }

        markSubSuperPhrases(phrases);
        Collections2.filter(phrases, notSelected).clear();

        markOverlappingPhrases(phrases);
        Collections2.filter(phrases, notSelected).clear();

        Collections.sort(phrases, new Comparator<PhraseCandidate>() {
            public int compare(PhraseCandidate p1, PhraseCandidate p2) {
                if (p1.coverage < p2.coverage) return 1;
                if (p1.coverage > p2.coverage) return -1;
                return 0;
            };
        });

        int max = maxPhrases;
        for (PhraseCandidate p : phrases)
        {
            if (max-- <= 0) break;
            result.phrases.add(p.cluster.phrases.get(0));
        }

        return result;
    }

    /**
     * Leave only most general (no other phrase is a substring of this one) and 
     * most specific (no other phrase is a superstring of this one) phrases.
     */
    private void markSubSuperPhrases(ArrayList<PhraseCandidate> phrases)
    {
        final int max = phrases.size();

        // A list of all words for each candidate phrase.
        final IntStack words = new IntStack(
            maxDescPhraseLength * phrases.size());

        // Offset pairs in the words list -- a pair [start, length].
        final IntStack offsets = new IntStack(phrases.size() * 2);

        for (PhraseCandidate p : phrases)
        {
            appendWords(words, offsets, p);
        }

        /*
         * Mark phrases that cannot be most specific or most general.
         */
        for (int i = 0; i < max; i++)
        {
            for (int j = 0; j < max; j++)
            {
                if (i == j) continue;

                int index = indexOf(
                    words.buffer, offsets.get(2 * i), offsets.get(2 * i + 1),
                    words.buffer, offsets.get(2 * j), offsets.get(2 * j + 1));
                if (index >= 0)
                {
                    // j is a subphrase of i, hence i cannot be mostGeneral and j
                    // cannot be most specific.
                    phrases.get(i).mostGeneral = false;
                    phrases.get(j).mostSpecific = false;
                }
            }
        }

        /*
         * For most general phrases, do not display them if a more specific phrase
         * exists with pretty much the same coverage. 
         */
        for (int i = 0; i < max; i++)
        {
            final PhraseCandidate a = phrases.get(i); 
            if (!a.mostGeneral) continue;

            for (int j = 0; j < max; j++)
            {
                final PhraseCandidate b = phrases.get(j);
                if (i == j || !b.mostSpecific) continue;

                int index = indexOf(
                    words.buffer, offsets.get(2 * j), offsets.get(2 * j + 1),
                    words.buffer, offsets.get(2 * i), offsets.get(2 * i + 1));
                if (index >= 0)
                {
                    if (a.coverage - b.coverage < mostGeneralPhraseCoverage)
                    {
                        a.selected = false;
                        j = max;
                    }
                }
            }
        }

        /*
         * Mark phrases that should be removed from the candidate set.
         */
        for (PhraseCandidate p : phrases)
        {
            if (!p.mostGeneral && !p.mostSpecific)
            {
                p.selected = false;
            }
        }
    }

    /**
     * Mark those phrases that overlap with other phrases by more than
     * {@link #maxPhraseOverlap} and have lower coverage.
     */
    private void markOverlappingPhrases(ArrayList<PhraseCandidate> phrases)
    {
        final int max = phrases.size();

        // A list of all unique words for each candidate phrase.
        final IntStack words = new IntStack(
            maxDescPhraseLength * phrases.size());

        // Offset pairs in the words list -- a pair [start, length].
        final IntStack offsets = new IntStack(phrases.size() * 2);

        for (PhraseCandidate p : phrases)
        {
            appendUniqueWords(words, offsets, p);
        }

        for (int i = 0; i < max; i++)
        {
            for (int j = i + 1; j < max; j++)
            {
                final PhraseCandidate a = phrases.get(i);
                final PhraseCandidate b = phrases.get(j);

                final int a_words = offsets.get(2 * i + 1);
                final int b_words = offsets.get(2 * j + 1);

                final float intersection = computeIntersection(
                    words.buffer, offsets.get(2 * i), a_words,
                    words.buffer, offsets.get(2 * j), b_words);

                if ((intersection / b_words) > maxPhraseOverlap 
                    && b.coverage < a.coverage)
                {
                    b.selected = false;
                }

                if ((intersection / a_words) > maxPhraseOverlap
                    && a.coverage < b.coverage) 
                {
                    a.selected = false;
                }
            }
        }
    }

    /**
     * Compute the number of common elements in two (sorted) lists. 
     */
    static int computeIntersection(int [] a, int aPos, int aLength, int [] b, int bPos, int bLength)
    {
        final int maxa = aPos + aLength;
        final int maxb = bPos + bLength;

        int ea;
        int eb;
        int common = 0;
        while (aPos < maxa && bPos < maxb)
        {
            ea = a[aPos]; eb = b[bPos];
            if (ea >= eb) bPos++; 
            if (ea <= eb) aPos++;
            if (ea == eb) common++;
        }

        return common;
    }

    /**
     * Collect all unique non-stop word from a phrase. 
     */
    private void appendUniqueWords(IntStack words, IntStack offsets, PhraseCandidate p)
    {
        assert p.cluster.phrases.size() == 1;

        final int start = words.size();
        final int [] phraseIndices  = p.cluster.phrases.get(0);
        final short [] tokenTypes = context.allWords.type;
        for (int i = 0; i < phraseIndices.length; i += 2)
        {
            for (int j = phraseIndices[i]; j <= phraseIndices[i + 1]; j++)
            {
                final int termIndex = sb.input.get(j);
                if (!TokenTypeUtils.isCommon(tokenTypes[termIndex]))
                {
                    words.push(termIndex);
                }
            }
        }

        // Sort words, we don't care about their order when counting subsets.
        Arrays.sort(words.buffer, start, words.size());

        // Reorder to keep only unique words.
        int j = start;
        for (int i = start + 1; i < words.size(); i++)
        {
            if (words.buffer[j] != words.buffer[i])
            {
                words.buffer[++j] = words.buffer[i];
            }
        }
        words.elementsCount = j + 1;

        offsets.push(start, words.size() - start);
    }

    /**
     * Collect all words from a phrase.
     */
    private void appendWords(IntStack words, IntStack offsets, PhraseCandidate p)
    {
        final int start = words.size();
        
        final int [] phraseIndices  = p.cluster.phrases.get(0);
        final short [] tokenTypes = context.allWords.type;
        for (int i = 0; i < phraseIndices.length; i += 2)
        {
            for (int j = phraseIndices[i]; j <= phraseIndices[i + 1]; j++)
            {
                final int termIndex = sb.input.get(j);
                if (!TokenTypeUtils.isCommon(tokenTypes[termIndex]))
                {
                    words.push(termIndex);
                }
            }
        }

        offsets.push(start, words.size() - start);
    }

    /**
     * Create the junk (unassigned documents) cluster and create the final
     * set of clusters in Carrot2 format. 
     */
    private void postProcessing(List<ClusterCandidate> clusters)
    {
        // Adapt to Carrot2 classes, counting used documents on the way.
        final BitSet all = new BitSet(documents.size());
        final ArrayList<Document> docs = Lists.newArrayListWithCapacity(documents.size());
        final ArrayList<String> phrases = Lists.newArrayListWithCapacity(3);
        for (ClusterCandidate c : clusters)
        {
            final Cluster c2 = new Cluster();
            c2.addPhrases(collectPhrases(phrases, c));
            c2.addDocuments(collectDocuments(docs, c.documents));
            c2.setScore((double) c.score);
            this.clusters.add(c2);

            all.or(c.documents);
            docs.clear(); 
            phrases.clear();
        }

        Collections.sort(this.clusters,
            Cluster.byReversedWeightedScoreAndSizeComparator(scoreWeight));

        Cluster.appendOtherTopics(this.documents, this.clusters);
    }
    
    /**
     * Collect phrases from a cluster.
     */
    private List<String> collectPhrases(List<String> l, ClusterCandidate c)
    {
        assert l != null;
        for (int [] phraseIndexes : c.phrases)
        {
            l.add(buildLabel(phraseIndexes));
        }
        return l;
    }

    /**
     * Collect documents from a bitset.
     */
    private List<Document> collectDocuments(List<Document> l, BitSet bitset)
    {
        if (l == null)
        {
            l = Lists.newArrayListWithCapacity((int) bitset.cardinality());
        }

        final BitSetIterator i = bitset.iterator();
        for (int d = i.nextSetBit(); d >= 0; d = i.nextSetBit())
        {
            l.add(documents.get(d));
        }
        return l;
    }

    /**
     * Build the cluster's label from suffix tree edge indices. 
     */
    private String buildLabel(int [] phraseIndices)
    {
        // Count the number of terms first.
        int termsCount = 0;
        for (int j = 0; j < phraseIndices.length; j += 2)
        {
            termsCount += phraseIndices[j + 1] - phraseIndices[j] + 1;
        }
    
        // Extract terms info for the phrase and construct the label.
        final boolean [] stopwords = new boolean[termsCount];
        final char [][] images = new char [termsCount][];
        final short [] tokenTypes = context.allWords.type;

        int k = 0;
        for (int i = 0; i < phraseIndices.length; i += 2)
        {
            for (int j = phraseIndices[i]; j <= phraseIndices[i + 1]; j++, k++)
            {
                final int termIndex = sb.input.get(j);
                images[k] = context.allWords.image[termIndex];
                stopwords[k] = TokenTypeUtils.isCommon(tokenTypes[termIndex]);
            }
        }
        
        return LabelFormatter.format(images, stopwords, 
            context.language.getLanguageCode().usesSpaceDelimiters());
    }

    @SuppressWarnings("unused")
    private String toString(PhraseCandidate c)
    {
        return String.format(Locale.ENGLISH, "%3.2f %s %s %s %s", 
            c.coverage, 
            buildLabel(c.cluster.phrases.get(0)),
            c.selected ? "S" : "",
            c.mostGeneral ? "MG" : "",
            c.mostSpecific ? "MS" : "");
    }

    /**
     * Build a cluster's label from suffix tree edge indices, including some debugging and
     * diagnostic information.
     */
    @SuppressWarnings("unused")
    private String buildDebugLabel(int [] phraseIndices)
    {
        final StringBuilder b = new StringBuilder();

        String sep = "";
        int k = 0;
        final short [] tokenTypes = context.allWords.type;
        for (int i = 0; i < phraseIndices.length; i += 2)
        {
            for (int j = phraseIndices[i]; j <= phraseIndices[i + 1]; j++, k++)
            {
                b.append(sep);

                final int termIndex = sb.input.get(j);
                b.append(context.allWords.image[termIndex]);

                if (TokenTypeUtils.isCommon(tokenTypes[termIndex])) b.append("[S]");
                sep = " ";
            }
            sep = "_";
        }

        return b.toString();
    }

    /**
     * Consider certain special cases of internal suffix tree nodes. The suffix tree may 
     * contain internal nodes with paths starting or ending with a stop word (common 
     * word). We have the following interesting scenarios:
     * 
     * <dl>
     * <dt>IF LEADING STOPWORD: IGNORE THE NODE.</dt>
     * <dd>
     * There MUST be a phrase with this stopword chopped off in the suffix tree 
     * (a suffix of this phrase) and its frequency will be just as high.</dd>
     * 
     * <dt>IF TRAILING STOPWORDS:</dt> 
     * <dd>
     * Check if the edge leading to the current node is composed entirely of stopwords. If so, 
     * there must be a parent node that contains non-stopwords and we can ignore the current node.
     * Otherwise we can chop off the trailing stopwords from the current node's phrase (this phrase
     * cannot be duplicated anywhere in the tree because if it were, there would have to be a branch
     * somewhere in the suffix tree on the edge).</dd>
     * </dl>
     */
    final boolean checkAcceptablePhrase(IntStack path)
    {
        assert path.size() > 0;

        final int [] terms = sb.input.buffer;
        final short [] tokenTypes = context.allWords.type;

        // Ignore nodes that start with a stop word.
        if (TokenTypeUtils.isCommon(tokenTypes[terms[path.get(0)]]))
        {
            return false;
        }

        // Check the last edge of the current node.
        int i = path.get(path.size() - 2);
        int j = path.get(path.size() - 1);
        final int k = j;
        while (i <= j && TokenTypeUtils.isCommon(tokenTypes[terms[j]]))
        {
            j--;
        }

        if (j < i)
        {
            // If the edge contains only stopwords, ignore the node.
            return false;
        }
        else if (j < k)
        {
            // There have been trailing stop words on the edge. Chop them off.
            path.buffer[path.size() - 1] = j;
        }

        // Check the total phrase length (in words, including stopwords).
        int termsCount = 0;
        for (j = 0; j < path.size(); j += 2)
        {
            termsCount += path.get(j + 1) - path.get(j) + 1;
        }

        if (termsCount >  maxDescPhraseLength)
        {
            return false;
        }

        return true;
    }

    /**
     * Calculate "effective phrase length", that is the number of non-ignored words
     * in the phrase.
     */
    final int effectivePhraseLength(IntStack path)
    {
        final int [] terms = sb.input.buffer;
        final int lower = ignoreWordIfInFewerDocs;
        final int upper = (int) (ignoreWordIfInHigherDocsPercent * documents.size());

        int effectivePhraseLen = 0;
        for (int i = 0; i < path.size(); i += 2)
        {
            for (int j = path.get(i); j <= path.get(i + 1); j++)
            {
                final int termIndex = terms[j];

                // If this term is a stop word, don't count it.
                if (TokenTypeUtils.isCommon(context.allWords.type[termIndex]))
                {
                    continue;
                }

                // If this word occurs in more than a given fraction of the input
                // collection don't count it.
                final int docCount = context.allWords.tfByDocument[termIndex].length / 2; 
                if (docCount < lower || docCount > upper)
                {
                    continue;
                }

                effectivePhraseLen++;
            }
        }

        return effectivePhraseLen;
    }

    /**
     * Calculates base cluster score.
     * <p>
     * The boost is calculated as a Gaussian function of density around the "optimum"
     * expected phrase length (average) and "tolerance" towards shorter and longer phrases
     * (standard deviation). You can draw this score multiplier's characteristic with
     * gnuplot:
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
     * One word-phrases can be given a fixed boost, if 
     * {@link #singleTermBoost} is greater than zero. 
     * 
     * @param phraseLength Effective phrase length (number of non-stopwords).
     * @param documentCount Number of documents this phrase occurred in.
     * @return Returns the base cluster score calculated as a function of the number of
     *         documents the phrase occurred in and a function of the effective length of
     *         the phrase.
     */
    final float baseClusterScore(final int phraseLength, final int documentCount)
    {
        final double boost;
        if (phraseLength == 1 && singleTermBoost > 0)
        {
            boost = singleTermBoost;
        }
        else
        {
            final int tmp = phraseLength - optimalPhraseLength;
            boost = Math.exp((-tmp * tmp) 
                / (2 * optimalPhraseLengthDev * optimalPhraseLengthDev));
        }

        return (float) (boost * (documentCount * documentCountBoost));
    }

    /**
     * Subsequence search in int arrays.
     */
    private static int indexOf(int [] source, int sourceOffset, int sourceCount, 
        int [] target, int targetOffset, int targetCount)
    {
        if (targetCount == 0)
        {
            return 0;
        }
    
        final int first = target[targetOffset];
        final int max = sourceOffset + (sourceCount - targetCount);
    
        for (int i = sourceOffset; i <= max; i++)
        {
            /* Look for first element. */
            if (source[i] != first)
            {
                while (++i <= max && source[i] != first) /* do nothing */;
            }
    
            /* Found first element, now look at the rest of the pattern */
            if (i <= max)
            {
                int j = i + 1;
                int end = j + targetCount - 1;
                for (int k = targetOffset + 1; j < end && source[j] == target[k]; j++, k++)
                    /* do nothing */;
    
                if (j == end)
                {
                    /* Found whole pattern. */
                    return i - sourceOffset;
                }
            }
        }
        return -1;
    }
}
