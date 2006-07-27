
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

package org.carrot2.filter.haog.fi;


import java.util.HashMap;
import java.util.Map;

/**
 * Class for storing FI algorithm parameters.
 * 
 * @author Karol Gołembniak
 */
public class FIParameters {

	/**
	 * Minimal single word occurence count in all documents.
	 */
	private double minSupport;
	
	/**
	 * Maximal time for itemsets generation in seconds.
	 */
	private int maxItemSetsGenerationTime;
	
	/**
	 * A word is ignored if it occures in more documents than this ratio.
	 */
	private double ignoreWordIfInHigherDocsPercent;
	
    /**
     * Clusters containing at least this number of common documents are merged.
     */
	private double linkThreshold;
    
	/**
	 * Labels containing more words than this ratio ate trimmed.
	 */
	private int maxDescPhraseLength;
	
	/**
	 * Clusters with less documents count than this ratio are ignored.
	 */
	private int minClusterSize;
	
	/**
	 * Maximum number of created/presented clusters.
	 */
	private int maxPresentedClusters;
	
	/**
	 * Holds way of creation final hierarchy 0-simple, 1-full
	 */
	private int hierarchyCreationWay;

	/**
	 * Constructor for this class. Sets default values for parameters.
	 */
	public FIParameters(){
		this.minSupport = FIConstants.DEFAULT_MIN_SUPPORT; 
		this.maxItemSetsGenerationTime = FIConstants.DEFAULT_MAX_ITEMSETS_GENERATION_TIME; 
		this.ignoreWordIfInHigherDocsPercent = FIConstants.DEFAULT_IGNORED_WORD_IF_IN_MORE_DOCS; 
		this.linkThreshold = FIConstants.DEFAULT_LINK_TRESHOLD;
		this.maxDescPhraseLength = FIConstants.DEFAULT_MAX_PHRASE_LENGTH;
		this.maxPresentedClusters = FIConstants.DEFAULT_MAX_PRESENTED_CLUSTERS;
		this.minClusterSize = FIConstants.DEFAULT_MIN_CLUSTER_SIZE;
		this.hierarchyCreationWay = FIConstants.DEFAULT_HIERATCHY_CREATION_WAY;
	}
	
	/**
	 * Gets FIParameters from given map.
	 * @param map
	 * @return parameters
	 */
	public static FIParameters fromMap(Map map) {
        final FIParameters params = new FIParameters();
        
        String value;
        value = (String) map.get(FIConstants.MIN_SUPPORT);
        if (value != null) {
            params.minSupport = Double.parseDouble(value);
            if (params.minSupport < 0.0d || params.minSupport > 1.0d) {
                throw new RuntimeException("Illegal value range.");
            }
        }

        value = (String) map.get(FIConstants.MAX_ITEMSETS_GENERATION_TIME);
        if (value != null) {
            params.maxItemSetsGenerationTime = Integer.parseInt(value);
            if (params.maxItemSetsGenerationTime < 0 || 
            	params.maxItemSetsGenerationTime > 60) {
                throw new RuntimeException("Illegal value range.");
            }
        }

        value = (String) map.get(FIConstants.IGNORED_WORD_IF_IN_MORE_DOCS);
        if (value != null) {
            params.ignoreWordIfInHigherDocsPercent = Double.parseDouble(value);
            if (params.ignoreWordIfInHigherDocsPercent < 0.0d || 
            	params.ignoreWordIfInHigherDocsPercent > 1.0d) {
                throw new RuntimeException("Illegal value range.");
            }
        }
        
        value = (String) map.get(FIConstants.LINK_TRESHOLD);
        if (value != null) {
            params.linkThreshold = Double.parseDouble(value);
            if (params.linkThreshold < 0 || params.linkThreshold > 1) {
                throw new RuntimeException("Illegal value range.");
            }
        }      
        
        value = (String) map.get(FIConstants.MAX_PHRASE_LENGTH);
        if (value != null) {
            params.maxDescPhraseLength = Integer.parseInt(value);
            if (params.maxDescPhraseLength < 1 || params.maxDescPhraseLength > 10) {
                throw new RuntimeException("Illegal value range.");
            }
        }      
        
        value = (String) map.get(FIConstants.MAX_PRESENTED_CLUSTERS);
        if (value != null) {
            params.maxPresentedClusters = Integer.parseInt(value);
            if (params.maxPresentedClusters < 1 || params.maxPresentedClusters > 500) {
                throw new RuntimeException("Illegal value range.");
            }
        }      
        
        value = (String) map.get(FIConstants.MIN_CLUSTER_SIZE);
        if (value != null) {
            params.minClusterSize = Integer.parseInt(value);
            if (params.minClusterSize < 1 || params.minClusterSize > 30) {
                throw new RuntimeException("Illegal value range.");
            }
        }      
        
        value = (String) map.get(FIConstants.HIERATCHY_CREATION_WAY);
        if (value != null) {
            params.hierarchyCreationWay = Integer.parseInt(value);
            if (params.hierarchyCreationWay < 0 || params.hierarchyCreationWay > 1) {
                throw new RuntimeException("Illegal value range.");
            }
        }      

        return params;
    }
	
	/**
	 * Creates map <String, String> from this parameters.
	 * @return map containing parameters as strings.
	 */
	public Map toMap(){
		Map map = new HashMap();
		map.put(FIConstants.MIN_SUPPORT, Double.toString(minSupport));
		map.put(FIConstants.MAX_ITEMSETS_GENERATION_TIME, Integer.toString(maxItemSetsGenerationTime));
		map.put(FIConstants.IGNORED_WORD_IF_IN_MORE_DOCS, Double.toString(ignoreWordIfInHigherDocsPercent));
		map.put(FIConstants.LINK_TRESHOLD, Double.toString(linkThreshold));
		map.put(FIConstants.MAX_PHRASE_LENGTH, Integer.toString(maxDescPhraseLength));
		map.put(FIConstants.MAX_PRESENTED_CLUSTERS, Integer.toString(maxPresentedClusters));
		map.put(FIConstants.MIN_CLUSTER_SIZE, Integer.toString(minClusterSize));
		map.put(FIConstants.HIERATCHY_CREATION_WAY, Integer.toString(hierarchyCreationWay));
		return map;
	}

	/**
	 * Getter for {@link #minSupport} parameter.
	 * @return minimum support
	 */
	public double getMinSupport() {
		return minSupport;
	}
	
	/**
	 * Getter for {@link #maxItemSetsGenerationTime} parameter.
	 * @return maximal itemset generation time
	 */
	public int getMaxItemSetsGenerationTime(){
		return maxItemSetsGenerationTime;
	}

	/**
	 * Getter for {@link #ignoreWordIfInHigherDocsPercent} parameter.
	 * @return parameter for ignoring too frequent words
	 */
	public double getIgnoreWordIfInHigherDocsPercent() {
		return ignoreWordIfInHigherDocsPercent;
	}

	/**
	 * Getter for {@link #linkThreshold} parameter.
	 * @return link treshold
	 */
	public double getLinkThreshold() {
		return linkThreshold;
	}

	/**
	 * Getter for {@link #maxDescPhraseLength} parameter.
	 * @return maximum cluster description length 
	 */
	public int getMaxDescPhraseLength() {
		return maxDescPhraseLength;
	}

	/**
	 * Getter for {@link #maxPresentedClusters} parameter.
	 * @return maximum number of created/presented clusters 
	 */
	public int getMaxPresentedClusters() {
		return maxPresentedClusters;
	}

	/**
	 * Getter for {@link #minClusterSize} parameter.
	 * @return minimum number of documents in cluster 
	 */
	public int getMinClusterSize() {
		return minClusterSize;
	}

	/**
	 * Getter for {@link #hierarchyCreationWay} parameter.
	 * @return value defining hierarchy creation way 
	 */
	public int getHierarchyCreationWay() {
		return hierarchyCreationWay;
	}
}
