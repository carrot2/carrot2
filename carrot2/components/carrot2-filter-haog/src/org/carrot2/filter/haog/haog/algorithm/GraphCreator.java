
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

package org.carrot2.filter.haog.haog.algorithm;

import java.util.List;

/**
 * Interface containing algorithm for creation graph from list of clusters
 * created by different clusterer algorithms. Graph created by this algorithm
 * is suitable to use in {@link org.carrot2.filter.haog.haog.algorithm.
 * GraphProcessor} 
 * @author Karol Gołembniak
 */
public interface GraphCreator {

	/**
	 * This method creates graph from a list of clusters. Clusters
	 * can have different types, so for each type You probably will have
	 * different creation algoithm.
	 * @param clusters - list of cluster objects
	 * @return List of graph vertices.
	 */
	public abstract List createGraph(List clusters);

}