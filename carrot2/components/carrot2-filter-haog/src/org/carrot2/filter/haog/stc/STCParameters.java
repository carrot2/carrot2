
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

package org.carrot2.filter.haog.stc;

import java.util.HashMap;
import java.util.Map;

/**
 * A class for handling HAOG-STC parameters and overriding them with mapped 
 * values. Almoust identical to 
 * {@link org.carrot2.filter.stc.StcParameters}
 * 
 * @author Karol Gołembniak
 */
public class STCParameters {
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
	 * Labels containing more words than this ratio ate trimmed.
	 * @author Karol Gołembniak
	 */
	private int maxDescPhraseLength;

	/**
	 * Holds way of creation final hierarchy 0-simple, 1-full
	 */
	private int hierarchyCreationWay;

    /**
     * Creates a new objects with default settings.
     */
    public STCParameters() {
        this.minBaseClusterScore = STCConstants.DEFAULT_MIN_BASE_CLUSTER_SIZE;
        this.ignoreWordIfInFewerDocs = STCConstants.DEFAULT_IGNORED_WORD_IF_IN_FEWER_DOCS;
        this.ignoreWordIfInHigherDocsPercent = STCConstants.DEFAULT_IGNORED_WORD_IF_IN_MORE_DOCS;
        this.maxBaseClusters = STCConstants.DEFAULT_MAX_BASE_CLUSTERS_COUNT;
        this.minBaseClusterSize = STCConstants.DEFAULT_MIN_BASE_CLUSTER_SIZE;
        this.maxClusters = STCConstants.DEFAULT_MAX_CLUSTERS;
        this.mergeThreshold = STCConstants.DEFAULT_MERGE_THRESHOLD;
        this.maxDescPhraseLength = STCConstants.DEFAULT_MAX_PHRASE_LENGTH;
    }

    public static STCParameters fromMap(Map map) {
        final STCParameters params = new STCParameters();
        
        String value;

        value = (String) map.get(STCConstants.MERGE_THRESHOLD);
        if (value != null) {
            params.mergeThreshold = Double.parseDouble(value);
            if (params.mergeThreshold < 0.0d
                    || params.mergeThreshold > 1.0d) {
                throw new RuntimeException("Illegal value range.");
            }
        }

        value = (String) map.get(STCConstants.MAX_CLUSTERS);
        if (value != null) {
            params.maxClusters = Integer.parseInt(value);
            if (params.maxClusters < 1) {
                throw new RuntimeException("Illegal value range.");
            }
        }

        value = (String) map.get(STCConstants.MIN_BASE_CLUSTER_SIZE);
        if (value != null) {
            params.minBaseClusterSize = Integer.parseInt(value);
            if (params.minBaseClusterSize < 2) {
                throw new RuntimeException("Illegal value range.");
            }
        }

        value = (String) map.get(STCConstants.MAX_BASE_CLUSTERS_COUNT);
        if (value != null) {
            params.maxBaseClusters = Integer.parseInt(value);
            if (params.maxBaseClusters < 0) {
                throw new RuntimeException("Illegal value range.");
            }
        }
        
        value = (String) map.get(STCConstants.MIN_BASE_CLUSTER_SCORE);
        if (value != null) {
            params.minBaseClusterScore = Double.parseDouble(value);
            if (params.minBaseClusterScore < 0.0d) {
                throw new RuntimeException("Illegal value range.");
            }
        }

        value = (String) map.get(STCConstants.IGNORED_WORD_IF_IN_FEWER_DOCS);
        if (value != null) {
            params.ignoreWordIfInFewerDocs = Integer.parseInt(value);
            if (params.ignoreWordIfInFewerDocs < 2) {
                throw new RuntimeException("Illegal value range.");
            }
        }

        value = (String) map.get(STCConstants.IGNORED_WORD_IF_IN_MORE_DOCS);
        if (value != null) {
            params.ignoreWordIfInHigherDocsPercent = Double.parseDouble(value);
            if (params.ignoreWordIfInHigherDocsPercent < 0.0d
                    || params.ignoreWordIfInHigherDocsPercent > 1.0d) {
                throw new RuntimeException("Illegal value range.");
            }
        }
        
        value = (String) map.get(STCConstants.MAX_PHRASE_LENGTH);
        if (value != null) {
            params.maxDescPhraseLength = Integer.parseInt(value);
            if (params.maxDescPhraseLength < 1 || params.maxDescPhraseLength > 10) {
                throw new RuntimeException("Illegal value range.");
            }
        }      
        
        value = (String) map.get(STCConstants.HIERATCHY_CREATION_WAY);
        if (value != null) {
            params.hierarchyCreationWay = Integer.parseInt(value);
            if (params.hierarchyCreationWay < 0 || params.hierarchyCreationWay > 1) {
                throw new RuntimeException("Illegal value range.");
            }
        }      

        return params;
    }
    
    public Map toMap() {
        final HashMap map = new HashMap();
        map.put(STCConstants.IGNORED_WORD_IF_IN_FEWER_DOCS, Integer.toString(getIgnoreWordIfInFewerDocs()));
        map.put(STCConstants.IGNORED_WORD_IF_IN_MORE_DOCS, Double.toString(getIgnoreWordIfInHigherDocsPercent()));
        map.put(STCConstants.MAX_BASE_CLUSTERS_COUNT, Integer.toString(getMaxBaseClusters()));
        map.put(STCConstants.MAX_CLUSTERS, Integer.toString(getMaxClusters()));
        map.put(STCConstants.MERGE_THRESHOLD, Double.toString(getMergeThreshold()));
        map.put(STCConstants.MIN_BASE_CLUSTER_SCORE, Double.toString(getMinBaseClusterScore()));
        map.put(STCConstants.MIN_BASE_CLUSTER_SIZE, Integer.toString(getMinBaseClusterSize()));
        map.put(STCConstants.MAX_PHRASE_LENGTH, Integer.toString(getMaxDescPhraseLength()));
		map.put(STCConstants.HIERATCHY_CREATION_WAY, Integer.toString(hierarchyCreationWay));
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

	public int getMaxDescPhraseLength() {
		return maxDescPhraseLength;
	}
	
	/**
	 * Getter for {@link #hierarchyCreationWay} parameter.
	 * @return value defining hierarchy creation way 
	 */
	public int getHierarchyCreationWay() {
		return hierarchyCreationWay;
	}
}
