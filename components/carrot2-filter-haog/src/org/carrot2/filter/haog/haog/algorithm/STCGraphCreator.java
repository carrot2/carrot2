
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

import java.util.ArrayList;
import java.util.List;

import org.carrot2.filter.stc.algorithm.BaseCluster;
import org.carrot2.filter.haog.haog.measure.Statistics;

/**
 * This class contains algorithm for creation of graph from clusters generated
 * by {@link org.carrot2.filter.stc.algorithm.STCEngine} class.
 * @see org.carrot2.filter.haog.haog.algorithm.GraphCreator
 * @author Karol Gołembniak
 */
public class STCGraphCreator implements GraphCreator {
	
	/**
	 * This method creates graph from a list of {@link BaseCluster}.
	 * @param baseClusters - BaseCluster list.
	 * @return list containing list of graph's vertices
	 * @see org.carrot2.filter.haog.haog.algorithm.GraphCreator#createGraph(java.util.List)
	 */
	public List createGraph(List baseClusters){
		List graph = new ArrayList();
		BaseCluster baseCluster;
		Vertex vertex;
		
		//First we must create all vertices to make arcs between them.
		for (int i1=0; i1<baseClusters.size(); i1++){
			baseCluster = (BaseCluster) baseClusters.get(i1);
			vertex = new Vertex(String.valueOf(baseCluster.getId()));
			vertex.setRepresentedCluster(baseCluster);
	 		graph.add(vertex);
		}

		//Now create arcs
		BaseCluster neighbour;
		Vertex neighbourVertex;
		int arcsCount = 0;
		for (int i1=0; i1<baseClusters.size(); i1++){
			baseCluster = (BaseCluster) baseClusters.get(i1);
			List neighbours = baseCluster.getNeighborsList();
			vertex = (Vertex) graph.get(i1);
			
			if (neighbours==null) {
				continue;
			}
			
			for (int i2=0; i2<neighbours.size(); i2++){
				neighbour = (BaseCluster) neighbours.get(i2);
				neighbourVertex = (Vertex) graph.get(neighbour.getId());
				vertex.addSuccessor(neighbourVertex);
				neighbourVertex.addPredecessor(vertex);
				arcsCount ++;
			}
		}
		
		Statistics.getInstance().setValue("Arcs count", new Integer(arcsCount));
		return graph;
	}

}
