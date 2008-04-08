package org.carrot2.clustering.stc;

import java.util.HashMap;
import java.util.Map;

/**
 * A class for handling STC parameters and overriding them with mapped values.
 * 
 * @author Dawid Weiss
 */
public class STCParameters
{
    /**
     * Minimal score of a potential base cluster in order to be added to base clusters.
     */
    private double minBaseClusterScore;

    /**
     * Ignore word if it exists in less documents (number, not percent!) than specified.
     */
    private int ignoreWordIfInFewerDocs;

    /**
     * A number between 0 and 1, if a word exists in more snippets than this ratio, it is
     * ignored.
     */
    private double ignoreWordIfInHigherDocsPercent;

    /**
     * Trims the base cluster array after N-th position for the merging phase.
     */
    private int maxBaseClusters;

    /**
     * Minimal number of documents in a base cluster, if fewer, then the base cluster is
     * removed before merging.
     */
    private int minBaseClusterSize;

    private int maxClusters;

    /**
     * Merge threshold for base cluster merging.
     */
    private double mergeThreshold;

    /**
     * Maximum overlap of phrases selected to the cluster description.
     */
    private double maxPhraseOverlap;

    /**
     * Minimum general phrase coverage to appear in cluster description.
     */
    private double mostGeneralPhraseCoverage;

    /**
     * Labels containing more words than this ratio ate trimmed.
     * 
     * @author Karol Gołembniak
     */
    private int maxDescPhraseLength;

    /**
     * A factor in calculation of the base cluster score. If greater then zero,
     * single-term base clusters are assigned this value avoiding the penalty function.
     * 
     * @see STCEngine#calculateModifiedBaseClusterScore(int, int, STCParameters)
     */
    private float singleTermBoost;

    /**
     * A factor in calculation of the base cluster score.
     * 
     * @see STCEngine#calculateModifiedBaseClusterScore(int, int, STCParameters)
     */
    private int optimalPhraseLength;

    /**
     * A factor in calculation of the base cluster score.
     * 
     * @see STCEngine#calculateModifiedBaseClusterScore(int, int, STCParameters)
     */
    private double optimalPhraseLengthDev;

    /**
     * A factor in calculation of the base cluster score, boosting the score depending on
     * the number of documents found in the base cluster.
     * 
     * @see STCEngine#calculateModifiedBaseClusterScore(int, int, STCParameters)
     */
    private double documentCountBoost;

    /**
     * Creates a new objects with default settings.
     */
    public STCParameters()
    {
        this.minBaseClusterScore = STCConstants.DEFAULT_MIN_BASE_CLUSTER_SIZE;
        this.ignoreWordIfInFewerDocs = STCConstants.DEFAULT_IGNORED_WORD_IF_IN_FEWER_DOCS;
        this.ignoreWordIfInHigherDocsPercent = STCConstants.DEFAULT_IGNORED_WORD_IF_IN_MORE_DOCS;
        this.maxBaseClusters = STCConstants.DEFAULT_MAX_BASE_CLUSTERS_COUNT;
        this.minBaseClusterSize = STCConstants.DEFAULT_MIN_BASE_CLUSTER_SIZE;
        this.maxClusters = STCConstants.DEFAULT_MAX_CLUSTERS;
        this.mergeThreshold = STCConstants.DEFAULT_MERGE_THRESHOLD;
        this.maxPhraseOverlap = STCConstants.DEFAULT_MAX_PHRASE_OVERLAP;
        this.mostGeneralPhraseCoverage = STCConstants.DEFAULT_MOST_GENERAL_PHRASE_COVERAGE;
        this.maxDescPhraseLength = STCConstants.DEFAULT_MAX_PHRASE_LENGTH;
        this.singleTermBoost = STCConstants.DEFAULT_SINGLE_TERM_BOOST;
        this.optimalPhraseLength = STCConstants.DEFAULT_OPTIMAL_PHRASE_LENGTH;
        this.optimalPhraseLengthDev = STCConstants.DEFAULT_OPTIMAL_PHRASE_LENGTH_DEV;
        this.documentCountBoost = STCConstants.DEFAULT_DOCUMENT_COUNT_BOOST;
    }

    public static STCParameters fromMap(Map map)
    {
        final STCParameters params = new STCParameters();

        String value;

        value = (String) map.get(STCConstants.MERGE_THRESHOLD);
        if (value != null)
        {
            params.mergeThreshold = Double.parseDouble(value);
            if (params.mergeThreshold < 0.0d || params.mergeThreshold > 1.0d)
            {
                throw new RuntimeException("Illegal value range.");
            }
        }

        value = (String) map.get(STCConstants.MAX_CLUSTERS);
        if (value != null)
        {
            params.maxClusters = Integer.parseInt(value);
            if (params.maxClusters < 1)
            {
                throw new RuntimeException("Illegal value range.");
            }
        }

        value = (String) map.get(STCConstants.MIN_BASE_CLUSTER_SIZE);
        if (value != null)
        {
            params.minBaseClusterSize = Integer.parseInt(value);
            if (params.minBaseClusterSize < 2)
            {
                throw new RuntimeException("Illegal value range.");
            }
        }

        value = (String) map.get(STCConstants.MAX_BASE_CLUSTERS_COUNT);
        if (value != null)
        {
            params.maxBaseClusters = Integer.parseInt(value);
            if (params.maxBaseClusters < 0)
            {
                throw new RuntimeException("Illegal value range.");
            }
        }

        value = (String) map.get(STCConstants.MIN_BASE_CLUSTER_SCORE);
        if (value != null)
        {
            params.minBaseClusterScore = Double.parseDouble(value);
            if (params.minBaseClusterScore < 0.0d)
            {
                throw new RuntimeException("Illegal value range.");
            }
        }

        value = (String) map.get(STCConstants.IGNORED_WORD_IF_IN_FEWER_DOCS);
        if (value != null)
        {
            params.ignoreWordIfInFewerDocs = Integer.parseInt(value);
            if (params.ignoreWordIfInFewerDocs < 2)
            {
                throw new RuntimeException("Illegal value range.");
            }
        }

        value = (String) map.get(STCConstants.IGNORED_WORD_IF_IN_MORE_DOCS);
        if (value != null)
        {
            params.ignoreWordIfInHigherDocsPercent = Double.parseDouble(value);
            if (params.ignoreWordIfInHigherDocsPercent < 0.0d
                || params.ignoreWordIfInHigherDocsPercent > 1.0d)
            {
                throw new RuntimeException("Illegal value range.");
            }
        }

        value = (String) map.get(STCConstants.MAX_PHRASE_OVERLAP);
        if (value != null)
        {
            params.maxPhraseOverlap = Double.parseDouble(value);
            if (params.maxPhraseOverlap < 0.0d || params.maxPhraseOverlap > 1.0d)
            {
                throw new RuntimeException("Illegal value range.");
            }
        }

        value = (String) map.get(STCConstants.MOST_GENERAL_PHRASE_COVERAGE);
        if (value != null)
        {
            params.mostGeneralPhraseCoverage = Double.parseDouble(value);
            if (params.mostGeneralPhraseCoverage < 0.0d
                || params.mostGeneralPhraseCoverage > 1.0d)
            {
                throw new RuntimeException("Illegal value range.");
            }
        }

        value = (String) map.get(STCConstants.MAX_PHRASE_LENGTH);
        if (value != null)
        {
            params.maxDescPhraseLength = Integer.parseInt(value);
            if (params.maxDescPhraseLength < 1 || params.maxDescPhraseLength > 10)
            {
                throw new RuntimeException("Illegal value range.");
            }
        }

        value = (String) map.get(STCConstants.SINGLE_TERM_BOOST);
        if (value != null)
        {
            params.singleTermBoost = Float.parseFloat(value);
            if (params.singleTermBoost < 0.0d || params.singleTermBoost > 1.0d)
            {
                throw new RuntimeException("Illegal value range.");
            }
        }

        value = (String) map.get(STCConstants.OPTIMAL_PHRASE_LENGTH);
        if (value != null)
        {
            params.optimalPhraseLength = Integer.parseInt(value);
            if (params.optimalPhraseLength < 0)
            {
                throw new RuntimeException("Illegal value range.");
            }
        }

        value = (String) map.get(STCConstants.OPTIMAL_PHRASE_LENGTH_DEV);
        if (value != null)
        {
            params.optimalPhraseLengthDev = Double.parseDouble(value);
            if (params.optimalPhraseLengthDev < 0)
            {
                throw new RuntimeException("Illegal value range.");
            }
        }

        value = (String) map.get(STCConstants.DOCUMENT_COUNT_BOOST);
        if (value != null)
        {
            params.documentCountBoost = Double.parseDouble(value);
            if (params.documentCountBoost < 0.0d)
            {
                throw new RuntimeException("Illegal value range.");
            }
        }

        return params;
    }

    public Map toMap()
    {
        final HashMap map = new HashMap();
        map.put(STCConstants.IGNORED_WORD_IF_IN_FEWER_DOCS, Integer
            .toString(getIgnoreWordIfInFewerDocs()));
        map.put(STCConstants.IGNORED_WORD_IF_IN_MORE_DOCS, Double
            .toString(getIgnoreWordIfInHigherDocsPercent()));
        map.put(STCConstants.MAX_BASE_CLUSTERS_COUNT, Integer
            .toString(getMaxBaseClusters()));
        map.put(STCConstants.MAX_CLUSTERS, Integer.toString(getMaxClusters()));
        map.put(STCConstants.MERGE_THRESHOLD, Double.toString(getMergeThreshold()));
        map.put(STCConstants.MIN_BASE_CLUSTER_SCORE, Double
            .toString(getMinBaseClusterScore()));
        map.put(STCConstants.MIN_BASE_CLUSTER_SIZE, Integer
            .toString(getMinBaseClusterSize()));
        map.put(STCConstants.MAX_PHRASE_OVERLAP, Double.toString(getMaxPhraseOverlap()));
        map.put(STCConstants.MOST_GENERAL_PHRASE_COVERAGE, Double
            .toString(getMostGeneralPhraseCoverage()));
        map.put(STCConstants.MAX_PHRASE_LENGTH, Integer
            .toString(getMaxDescPhraseLength()));
        map.put(STCConstants.SINGLE_TERM_BOOST, Double.toString(getTermBoost()));
        map.put(STCConstants.OPTIMAL_PHRASE_LENGTH, Integer
            .toString(getOptimalPhraseLength()));
        map.put(STCConstants.OPTIMAL_PHRASE_LENGTH_DEV, Double
            .toString(getOptimalPhraseLengthDev()));
        map.put(STCConstants.DOCUMENT_COUNT_BOOST, Double
            .toString(getDocumentCountBoost()));
        return map;
    }

    public float getMinBaseClusterScore()
    {
        return (float) minBaseClusterScore;
    }

    public int getIgnoreWordIfInFewerDocs()
    {
        return ignoreWordIfInFewerDocs;
    }

    public float getIgnoreWordIfInHigherDocsPercent()
    {
        return (float) ignoreWordIfInHigherDocsPercent;
    }

    public int getMaxBaseClusters()
    {
        return maxBaseClusters;
    }

    public int getMinBaseClusterSize()
    {
        return minBaseClusterSize;
    }

    public int getMaxClusters()
    {
        return maxClusters;
    }

    public float getMergeThreshold()
    {
        return (float) mergeThreshold;
    }

    public float getMaxPhraseOverlap()
    {
        return (float) maxPhraseOverlap;
    }

    public float getMostGeneralPhraseCoverage()
    {
        return (float) mostGeneralPhraseCoverage;
    }

    public int getMaxDescPhraseLength()
    {
        return maxDescPhraseLength;
    }

    public float getTermBoost()
    {
        return singleTermBoost;
    }

    public int getOptimalPhraseLength()
    {
        return optimalPhraseLength;
    }

    public double getOptimalPhraseLengthDev()
    {
        return optimalPhraseLengthDev;
    }

    public double getDocumentCountBoost()
    {
        return documentCountBoost;
    }
}
