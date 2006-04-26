
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

package com.kgolembniak.carrot.filter.haog.algorithm;

import java.util.ArrayList;
import java.util.List;

import com.kgolembniak.carrot.filter.fi.algorithm.Cluster;

/**
 * This class contains algorithm for creation of graph from clusters generated
 * by {@link com.kgolembniak.carrot.filter.fi.algorithm.AprioriEngine} class.
 * @see com.kgolembniak.carrot.filter.haog.algorithm.GraphCreator
 * @author Karol Gołembniak
 */
public class FIGraphCreator implements GraphCreator {
	
	/**
	 * This method creates graph from a list of 
	 * {@link com.kgolembniak.carrot.filter.fi.algorithm.Cluster}.
	 * @param baseClusters - BaseCluster list.
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
		for (int i1=0; i1<graph.size(); i1++){
			vertex = (Vertex) graph.get(i1);
			cluster = (Cluster) vertex.getRepresentedCluster();
			neighbours = cluster.getNeighbours();
			
			for (int i2=0; i2<neighbours.size(); i2++){
				neighbour = (Cluster) neighbours.get(i2);
				successor = (Vertex) graph.get(neighbour.getId().intValue());
				vertex.addSuccessor(successor);
				successor.addPredecessor(vertex);
			}
		}
		
		return graph;
	}

}
