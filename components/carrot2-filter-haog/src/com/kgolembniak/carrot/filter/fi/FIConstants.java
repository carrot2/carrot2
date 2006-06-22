
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

package com.kgolembniak.carrot.filter.fi;

/**
 * This class contains default FI algorithm parameters and names for them.
 *  
 * @author Karol Gołembniak
 */
public class FIConstants {
	
	/**
	 * This class shouldn't be used as an object.
	 */
	private FIConstants(){
	}
	
	/**
	 * Minimum word support (word occurences in documents count)
	 */
	public final static String MIN_SUPPORT = "fi.minSupport";

	/**
	 * Default value for {@link #MIN_SUPPORT} 
	 */
	public final static double DEFAULT_MIN_SUPPORT = 0.10d;

	/**
	 * Maximal time for itemsets generation.
	 */
	public final static String MAX_ITEMSETS_GENERATION_TIME = "fi.maxItemSetsGenerationTime";

	/**
	 * Default value for {@link #MAX_ITEMSETS_GENERATION_TIME} 
	 */
	public static final int DEFAULT_MAX_ITEMSETS_GENERATION_TIME = 5;

	/**
	 * Too frequent words are not interesting, so they are ignored if they 
	 * occure in more than some percent of documents.
	 */
	public final static String IGNORED_WORD_IF_IN_MORE_DOCS = "fi.ignoreWordIfInHigherDocsPercent";
	
	/**
	 * Default value for {@link #IGNORED_WORD_IF_IN_MORE_DOCS}
	 */
	public final static double DEFAULT_IGNORED_WORD_IF_IN_MORE_DOCS = 0.98d;
	
	/**
	 * If number of common documents in two clusters is grater than this factor,
	 * they are merged.
	 */
	public final static String LINK_TRESHOLD = "fi.linkTreshold";

	/**
	 * Default value for {@link #LINK_TRESHOLD} 
	 */
	public final static double DEFAULT_LINK_TRESHOLD = 0.7d;

	/**
	 * Cluster label length is limited to number of words given by this parameter.  
	 */
	public final static String MAX_PHRASE_LENGTH = "fi.maxPhraseLength";

	/**
	 * Default value for {@link #MAX_PHRASE_LENGTH} 
	 */
	public final static int DEFAULT_MAX_PHRASE_LENGTH = 5;

	/**
	 * Maximum number of cluster generated/presented to user
	 */
	public final static String MAX_PRESENTED_CLUSTERS = "fi.maxPresentedCluster";

	/**
	 * Default value for {@link #MAX_PRESENTED_CLUSTERS}
	 */
	public final static int DEFAULT_MAX_PRESENTED_CLUSTERS = 100;

	/**
	 * Minimum number of documents in cluster. Smaller clusters are ignored.
	 */
	public final static String MIN_CLUSTER_SIZE = "fi.minClusterSize";
	
	/**
	 * Default value for {@link #MIN_CLUSTER_SIZE}
	 */
	public final static int DEFAULT_MIN_CLUSTER_SIZE = 2;

}
