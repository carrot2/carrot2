
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

import java.util.ArrayList;
import java.util.List;

import org.carrot2.filter.haog.fi.algorithm.Cluster;
import org.carrot2.filter.haog.haog.measure.Statistics;

/**
 * This class contains algorithm for creation of graph from clusters generated
 * by {@link org.carrot2.filter.haog.fi.algorithm.AprioriEngine} class.
 * @see org.carrot2.filter.haog.haog.algorithm.GraphCreator
 * @author Karol Gołembniak
 */
public class FIGraphCreator implements GraphCreator {
	
	/**
	 * This method creates graph from a list of 
	 * {@link org.carrot2.filter.haog.fi.algorithm.Cluster}.
	 * @param clusters - BaseCluster list.
	 * @return list containing list of graph's vertices
	 */
	public List createGraph(List clusters){
		ArrayList graph = new ArrayList();
		
		Cluster cluster;
		Vertex vertex;
		for (int i1=0; i1<clusters.size(); i1++){
			cluster = (Cluster) clusters.get(i1);
			vertex = new Vertex(String.valueOf(i1));
			vertex.setRepresentedCluster(cluster);
			graph.add(vertex);
		}
		
		List neighbours;
		Cluster neighbour;
		Vertex successor;
		int arcsCount = 0;
		for (int i1=0; i1<graph.size(); i1++){
			vertex = (Vertex) graph.get(i1);
			cluster = (Cluster) vertex.getRepresentedCluster();
			neighbours = cluster.getNeighbours();
			
			for (int i2=0; i2<neighbours.size(); i2++){
				neighbour = (Cluster) neighbours.get(i2);
				successor = (Vertex) graph.get(neighbour.getId().intValue());
				vertex.addSuccessor(successor);
				successor.addPredecessor(vertex);
				arcsCount ++;
			}
		}
		
		Statistics.getInstance().setValue("Arcs count", new Integer(arcsCount));
		return graph;
	}

}
