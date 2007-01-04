
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.stc;


/**
 * Names of properties used to drive the algorithm and
 * their default values.
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
public class StcConstants {
    
    /**
     * No instantiation of this class.
     */
    private StcConstants() {
    }
    
    /**
     * Minimum score threshold for base clusters (clusters
     * with lower score are discarded before merging).
     * 
     * <p>Allowed range: 0.0 -- inf., property type: <code>String</code>
     *    (a numeric value)</p>
     */
    public final static String MIN_BASE_CLUSTER_SCORE 
        = "stc.threshold.minBaseClusterScore";

    /** Default value of {@link #MIN_BASE_CLUSTER_SCORE}. */
    public final static double DEFAULT_MIN_BASE_CLUSTER_SCORE = 2.0f;

    /**
     * Maximum highest scoring base clusters promoted for merging.
     * 
     * <p>Allowed range: integer, property type <code>String</code>
     *    (a numeric value)</p>
     */
    public final static String MAX_BASE_CLUSTERS_COUNT
        = "stc.threshold.maxBaseClusters";

    /** Default value of {@link #MAX_BASE_CLUSTERS_COUNT}. */
    public final static int DEFAULT_MAX_BASE_CLUSTERS_COUNT = 300;

    /**
     * Minimum number of documents in a base cluster to be promoted
     * to merging phase. 
     * 
     * <p>Allowed range: 2 -- inf., property type <code>String</code>
     *    (a numeric value)</p>
     */
    public final static String MIN_BASE_CLUSTER_SIZE
        = "stc.threshold.minBaseClusterSize";

    /** Default value of {@link #MIN_BASE_CLUSTER_SIZE}. */
    public final static int DEFAULT_MIN_BASE_CLUSTER_SIZE = 2;

    /**
     * A word is marked as ignored (irrelevant) if it is in fewer
     * documents than this threshold.
     * 
     * <p>Allowed range: 2 -- inf., property type <code>String</code>
     *    (a numeric value)</p>
     */
    public final static String IGNORED_WORD_IF_IN_FEWER_DOCS
        = "stc.threshold.ignoreWordIfInFewerDocs";

    /** Default value of {@link #IGNORED_WORD_IF_IN_FEWER_DOCS}. */
    public final static int DEFAULT_IGNORED_WORD_IF_IN_FEWER_DOCS = 2;
    
    /**
     * A word is marked as ignored (irrelevant) if it is in a higher
     * ratio of documents than this.
     * 
     * <p>Allowed range: 0 -- 1, property type <code>String</code>
     *    (a numeric value)</p>
     */
    public final static String IGNORED_WORD_IF_IN_MORE_DOCS
        = "stc.threshold.ignoreWordIfInHigherDocsPercent";

    /** Default value of {@link #IGNORED_WORD_IF_IN_MORE_DOCS}. */
    public final static double DEFAULT_IGNORED_WORD_IF_IN_MORE_DOCS = 0.9f;

    /**
     * Two base clusters are merged if their documents overlap
     * at least in this ratio.
     * 
     * <p>Allowed range: 0 -- 1, property type <code>String</code>
     *    (a numeric value)</p>
     */
    public final static String MERGE_THRESHOLD
        = "stc.threshold.mergeThreshold";

    /** Default value of {@link #MERGE_THRESHOLD}. */
    public final static double DEFAULT_MERGE_THRESHOLD = 0.6f;
    
    /**
     * Maximum number of merged clusters returned.
     * 
     * <p>Allowed range: integer, 1 -- inf., property type <code>String</code>
     *    (a numeric value)</p>
     */
    public final static String MAX_CLUSTERS
        = "stc.threshold.maxClusters";

    /** Default value of {@link #MAX_CLUSTERS}. */
    public final static int DEFAULT_MAX_CLUSTERS = 15;

    /**
     * Maximum overlap between phrases selected for cluster labels. If the overlap
     * exceeds this level, one phrase is removed from the label.
     * 
     * <p>Allowed range: 0 -- 1, property type <code>String</code> (a numeric value)</p>
     */
    public final static String MAX_PHRASE_OVERLAP = "stc.max.phrase.overlap";

    /**
     * Default value of {@link #MAX_PHRASE_OVERLAP}.
     */
    public final static double DEFAULT_MAX_PHRASE_OVERLAP = 0.3d;

    /**
     * Minimum general phrase coverage to appear in cluster description.
     * 
     * <p>Allowed range: 0 -- 1, property type <code>String</code> (a numeric value)</p>
     */
    public static final String MOST_GENERAL_PHRASE_COVERAGE = "stc.most.general.phrase.coverage";
    
    /**
     * Default value of {@link #MOST_GENERAL_PHRASE_COVERAGE}.
     */
    public static final double DEFAULT_MOST_GENERAL_PHRASE_COVERAGE = 0.5d;
    
	/**
	 * Cluster label length is limited to number of words 
     * given by this parameter.
	 */
	public final static String MAX_PHRASE_LENGTH = "stc.maxPhraseLength";

	/**
	 * Default value for {@link #MAX_PHRASE_LENGTH}. 
	 */
	public final static int DEFAULT_MAX_PHRASE_LENGTH = 4;
    
}
