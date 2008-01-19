
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.haog.haog.algorithm;

import java.util.List;

import org.carrot2.core.ProcessingException;
import org.carrot2.core.clustering.RawCluster;
import org.carrot2.core.clustering.RawClustersConsumer;

/**
 * This class contains algorithm which creates 
 * {@link RawCluster}s from list of kernel vertices.
 * 
 * @author Karol Gołembniak
 */
public interface GraphRenderer {

	/**
	 * Creates {@link org.carrot2.core.clustering.RawCluster}
	 * from given list of kernel vertices and adds them to {@link 
	 * RawClustersConsumer}
	 * @param kernel - Kernel of graph as a list of vetices.
	 * @param documents - Documents returned by user query.
	 * @param params - Parameters for clusters rendering.
	 * @param consumer - Consumer of created clusters.
	 * @throws ProcessingException
	 */
	public abstract void renderRawClusters(List kernel, List documents, Object params,
    	   	RawClustersConsumer consumer) throws ProcessingException;

}
