
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

package com.dawidweiss.carrot.filter.stc;

import java.util.HashMap;
import java.util.Map;

/**
 * A class for handling STC parameters and 
 * overriding them with mapped values.
 * 
 * @author Dawid Weiss
 */
public class StcParameters {
    /**
     * Minimal score of a potential base cluster in order to be added to
     * base clusters.
     */
    private double minBaseClusterScore;
    
    /**
     * Ignore word if it exists in less documents (number, not percent!) than specified.
     */
    private int ignoreWordIfInFewerDocs;
    
    /**
     * A number between 0 and 1, if a word exists in more snippets than this ratio, it is ignored. 
     */
    private double ignoreWordIfInHigherDocsPercent;

    /**
     * Trims the base cluster array after N-th position for the merging phase.
     */
    private int maxBaseClusters;
    
    /**
     * Minimal number of documents in a base cluster, if fewer, then the base cluster is removed
     * before merging.
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
     * Creates a new objects with default settings.
     */
    public StcParameters() {
        this.minBaseClusterScore = StcConstants.DEFAULT_MIN_BASE_CLUSTER_SIZE;
        this.ignoreWordIfInFewerDocs = StcConstants.DEFAULT_IGNORED_WORD_IF_IN_FEWER_DOCS;
        this.ignoreWordIfInHigherDocsPercent = StcConstants.DEFAULT_IGNORED_WORD_IF_IN_MORE_DOCS;
        this.maxBaseClusters = StcConstants.DEFAULT_MAX_BASE_CLUSTERS_COUNT;
        this.minBaseClusterSize = StcConstants.DEFAULT_MIN_BASE_CLUSTER_SIZE;
        this.maxClusters = StcConstants.DEFAULT_MAX_CLUSTERS;
        this.mergeThreshold = StcConstants.DEFAULT_MERGE_THRESHOLD;
        this.maxPhraseOverlap = StcConstants.DEFAULT_MAX_PHRASE_OVERLAP;
        this.mostGeneralPhraseCoverage = StcConstants.DEFAULT_MOST_GENERAL_PHRASE_COVERAGE;
    }

    public static StcParameters fromMap(Map map) {
        final StcParameters params = new StcParameters();
        
        String value;

        value = (String) map.get(StcConstants.MERGE_THRESHOLD);
        if (value != null) {
            params.mergeThreshold = Double.parseDouble(value);
            if (params.mergeThreshold < 0.0d
                    || params.mergeThreshold > 1.0d) {
                throw new RuntimeException("Illegal value range.");
            }
        }

        value = (String) map.get(StcConstants.MAX_CLUSTERS);
        if (value != null) {
            params.maxClusters = Integer.parseInt(value);
            if (params.maxClusters < 1) {
                throw new RuntimeException("Illegal value range.");
            }
        }

        value = (String) map.get(StcConstants.MIN_BASE_CLUSTER_SIZE);
        if (value != null) {
            params.minBaseClusterSize = Integer.parseInt(value);
            if (params.minBaseClusterSize < 2) {
                throw new RuntimeException("Illegal value range.");
            }
        }

        value = (String) map.get(StcConstants.MAX_BASE_CLUSTERS_COUNT);
        if (value != null) {
            params.maxBaseClusters = Integer.parseInt(value);
            if (params.maxBaseClusters < 0) {
                throw new RuntimeException("Illegal value range.");
            }
        }
        
        value = (String) map.get(StcConstants.MIN_BASE_CLUSTER_SCORE);
        if (value != null) {
            params.minBaseClusterScore = Double.parseDouble(value);
            if (params.minBaseClusterScore < 0.0d) {
                throw new RuntimeException("Illegal value range.");
            }
        }

        value = (String) map.get(StcConstants.IGNORED_WORD_IF_IN_FEWER_DOCS);
        if (value != null) {
            params.ignoreWordIfInFewerDocs = Integer.parseInt(value);
            if (params.ignoreWordIfInFewerDocs < 2) {
                throw new RuntimeException("Illegal value range.");
            }
        }

        value = (String) map.get(StcConstants.IGNORED_WORD_IF_IN_MORE_DOCS);
        if (value != null) {
            params.ignoreWordIfInHigherDocsPercent = Double.parseDouble(value);
            if (params.ignoreWordIfInHigherDocsPercent < 0.0d
                    || params.ignoreWordIfInHigherDocsPercent > 1.0d) {
                throw new RuntimeException("Illegal value range.");
            }
        }
        
        value = (String) map.get(StcConstants.MAX_PHRASE_OVERLAP);
        if (value != null) {
            params.maxPhraseOverlap = Double.parseDouble(value);
            if (params.maxPhraseOverlap < 0.0d
                    || params.maxPhraseOverlap > 1.0d) {
                throw new RuntimeException("Illegal value range.");
            }
        }

        value = (String) map.get(StcConstants.MOST_GENERAL_PHRASE_COVERAGE);
        if (value != null) {
            params.mostGeneralPhraseCoverage = Double.parseDouble(value);
            if (params.mostGeneralPhraseCoverage < 0.0d
                    || params.mostGeneralPhraseCoverage > 1.0d) {
                throw new RuntimeException("Illegal value range.");
            }
        }

        return params;
    }
    
    public Map toMap() {
        final HashMap map = new HashMap();
        map.put(StcConstants.IGNORED_WORD_IF_IN_FEWER_DOCS, Integer.toString(getIgnoreWordIfInFewerDocs()));
        map.put(StcConstants.IGNORED_WORD_IF_IN_MORE_DOCS, Double.toString(getIgnoreWordIfInHigherDocsPercent()));
        map.put(StcConstants.MAX_BASE_CLUSTERS_COUNT, Integer.toString(getMaxBaseClusters()));
        map.put(StcConstants.MAX_CLUSTERS, Integer.toString(getMaxClusters()));
        map.put(StcConstants.MERGE_THRESHOLD, Double.toString(getMergeThreshold()));
        map.put(StcConstants.MIN_BASE_CLUSTER_SCORE, Double.toString(getMinBaseClusterScore()));
        map.put(StcConstants.MIN_BASE_CLUSTER_SIZE, Integer.toString(getMinBaseClusterSize()));
        map.put(StcConstants.MAX_PHRASE_OVERLAP, Double.toString(getMaxPhraseOverlap()));
        map.put(StcConstants.MOST_GENERAL_PHRASE_COVERAGE, Double.toString(getMostGeneralPhraseCoverage()));
        return map;
    }

    public float getMinBaseClusterScore() {
        return (float) minBaseClusterScore;
    }

    public int getIgnoreWordIfInFewerDocs() {
        return ignoreWordIfInFewerDocs;
    }

    public float getIgnoreWordIfInHigherDocsPercent() {
        return (float) ignoreWordIfInHigherDocsPercent;
    }

    public int getMaxBaseClusters() {
        return maxBaseClusters;
    }

    public int getMinBaseClusterSize() {
        return minBaseClusterSize;
    }

    public int getMaxClusters() {
        return maxClusters;
    }

    public float getMergeThreshold() {
        return (float) mergeThreshold;
    }

    public float getMaxPhraseOverlap() {
        return (float) maxPhraseOverlap;
    }

    public float getMostGeneralPhraseCoverage() {
        return (float) mostGeneralPhraseCoverage;
    }
}
