
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

package com.stachoodev.carrot.filter.lingo.lsicluster;


/**
 * Names of properties used to drive the algorithm and
 * their default values.
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
public class LsiConstants {
    
    /**
     * No instantiation of this class.
     */
    private LsiConstants() {
    }
    
    /**
     * Determines the similarity threshold that must be exceeded in
     * order for a document to be added to a cluster. The larger the
     * value, the less documents
     * in a cluster and the larger assignment precission.
     * 
     * <P>Range: 0.0 - 1.0, Property type: <code>java.lang.String</code> 
     */
    public final static String CLUSTER_ASSIGNMENT_THRESHOLD
    	= "lsi.threshold.clusterAssignment";
    
    /** 
     * Default value of the {@link CLUSTER_ASSIGNMENT_THRESHOLD}.
     */
    public final static double DEFAULT_CLUSTER_ASSIGNMENT_THRESHOLD = 0.225;

    
    /**
     * Determines the maximum number of candidate clusters. The larger the value, the more
     * candidate clusters. 
     * 
     * <p>Range: 0.0 - 1.0, Property type: <code>java.lang.String</code> 
     */
    public final static String CANDIDATE_CLUSTER_THRESHOLD
    	= "lsi.threshold.candidateCluster";
    
    /**
     * Default value of the {@link  CANDIDATE_CLUSTER_THRESHOLD}.
     */
    public final static double DEFAULT_CANDIDATE_CLUSTER_THRESHOLD = 0.775;
    
}
