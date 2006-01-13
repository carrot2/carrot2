
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
 * A class for handling STC parameters and overriding them
 * with mapped values.
 * 
 * @author Dawid Weiss
 */
public class StcParameters {
    private double minBaseClusterScore;
    private int ignoreWordIfInFewerDocs;
    private double ignoreWordIfInHigherDocsPercent;
    private int maxBaseClusters;
    private int minBaseClusterSize;
    private int maxClusters;
    private double mergeThreshold;

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
}
