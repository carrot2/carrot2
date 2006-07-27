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
import java.util.Iterator;
import java.util.List;

import com.dawidweiss.carrot.core.local.ProcessingException;
import org.carrot2.filter.haog.haog.measure.Statistics;

/**
 * Main class containing HAOG Algorithm described in:
 * Irmina Masłowska's Doctoral Thesis <i>Hierarchical Clustering of Web Documents</i>.
 * 
 * @author Karol Gołembniak
 */
public class GraphProcessor {

	private List vertices;
	private List cycle;
	private int nextLabel;
	private int cyclesCount;
	
	/**
	 * Default constructor
	 */
	public GraphProcessor(){
		this.vertices = new ArrayList();
	}
	
	/**
	 * This method removes cycles from graph given as a list. 
	 * @param graph - list of vertices
	 * @return graph without cycles as a list of vertices
	 * @throws ProcessingException
	 */
	public List removeCycle(List graph) throws ProcessingException{
		this.cyclesCount = 0;
		nextLabel = graph.size()+1;
		int initialGraphSize = graph.size();
		int counter = 0;
		while (graph.size() > 0){
			counter++;
			removeLeaves(graph);
			removeRoots(graph);
			if (graph.size() > 0){
				processCycles(graph);
			}

			// safety lock - just in case
			if (counter > initialGraphSize) {
				throw new ProcessingException("Neverending loop detected: "
                        + graph);
			}
		}

		Statistics.getInstance().setValue("Number of cycles", new Integer(this.cyclesCount));
		//After removing cycle we have moddified arcs, 
		//now we have to restore all arcs
		repairArcs(vertices);
		return vertices;
	}
	
	/**
	 * This method removes one cycle from given graph.
	 * @param graph - list of vertices.
	 */
	private void processCycles(List graph) {
		cycle = new ArrayList();
		
		Vertex firstVertex = (Vertex) graph.get(0);
		Vertex cycleVertex = null;
		cycle.add(firstVertex);
		
		Vertex lastVertex = (Vertex) firstVertex.getSuccList().get(0);
		cycle.add(lastVertex);
		firstVertex.getSuccList().remove(lastVertex);
		
		while ((firstVertex!=lastVertex)&&
				(lastVertex.getSuccList().size()>0)){
			cycleVertex = lastVertex;
			lastVertex = (Vertex) cycleVertex.getSuccList().get(0);
			cycle.add(lastVertex);
			cycleVertex.getSuccList().remove(lastVertex);
		}
		
		while (firstVertex!=lastVertex){
			firstVertex = (Vertex) cycle.get(0);
			cycle.remove(firstVertex);
			lastVertex = (Vertex) cycle.get(0);
			firstVertex.addSuccessor(lastVertex);
			firstVertex = lastVertex;
			lastVertex = (Vertex) cycle.get(cycle.size()-1);
		}
		
		if (firstVertex==lastVertex){
			cycle.remove(cycle.size()-1);
		}

		if (cycle.size()>0){
			this.cyclesCount ++;
			CombinedVertex combinedCycle = new CombinedVertex(String.valueOf(nextLabel));
			nextLabel++;
			combinedCycle.setVertices(cycle);
			graph.removeAll(cycle);
			updateVerticesArcs(combinedCycle);
			if (graph.size()>0){
				graph.add(combinedCycle);
			} else {
				vertices.add(combinedCycle);
			}
		}
	}
	
	/**
	 * This method updates arcs in graph after combining vertices from cycle
	 * in one {@link CombinedVertex}. 
	 * @param cycleVertex - vertex containing found cycle
	 */
	private void updateVerticesArcs(CombinedVertex cycleVertex){
		List predecessors = cycleVertex.getPredList();
		List cycle = cycleVertex.getVertices(); 
		Vertex vertex; 
		for (int i1=0; i1<predecessors.size(); i1++){
			vertex = (Vertex) predecessors.get(i1);
			if (vertex.getSuccList().removeAll(cycle)){
				vertex.addSuccessor(cycleVertex);
			}
		}
		
		List successors = cycleVertex.getSuccList();
		for (int i1=0; i1<successors.size(); i1++){
			vertex = (Vertex) successors.get(i1);
			if (vertex.getPredList().removeAll(cycle)){
				vertex.addPredecessor(cycleVertex);
			}
		}
	}

	/**
	 * This method removes vertices, which don't have any successor
	 * and adds them to list of vertices without cycle.
	 * @param graph - list of vertices
	 */
	private void removeLeaves(List graph){
		Vertex checkedVertex;
		int i1=0;
		
		while (i1<graph.size()){
			checkedVertex = (Vertex) graph.get(i1);

			if (checkedVertex.getSuccList().size()==0){
				
				CombinedVertex combinedVertex = new CombinedVertex(checkedVertex);
				vertices.add(combinedVertex);
				graph.remove(i1);

				List predecessors = checkedVertex.getPredList();
				Vertex updatedVertex = null;
				
				//After removing vertex, we have to update graph arcs
				for (int i2=0; i2<predecessors.size(); i2++){
					updatedVertex = (Vertex) predecessors.get(i2);
					updatedVertex.removeSuccessor(checkedVertex);
					if (updatedVertex.getSuccList().size()==0){
						i1=0;
					}
				}

				checkedVertex.getPredList().clear();
			} else {
				i1++;
			}
		}
	}
	
	/**
	 * This method removes vertices, which don't have any predecessor
	 * and adds them to list of vertices without cycle.
	 * @param graph - list of vertices
	 */
	private void removeRoots(List graph){
		Vertex checkedVertex = null;
		int i1=0;
		
		while (i1<graph.size()){
			checkedVertex = (Vertex) graph.get(i1);

			if (checkedVertex.getPredList().size()==0){

				CombinedVertex combinedVertex = new CombinedVertex(checkedVertex);
				vertices.add(combinedVertex);
				graph.remove(i1);

				List successors = checkedVertex.getSuccList();
				Vertex updatedVertex = null;
				
				//After removing vertex, we have to update graph arcs
				for (int i2=0; i2<successors.size(); i2++){
					updatedVertex = (Vertex) successors.get(i2);
					updatedVertex.removePredecessor(checkedVertex);
					if (updatedVertex.getPredList().size()==0){
						i1=0;
					}
				}

				checkedVertex.getSuccList().clear();
			} else {
				i1++;
			}
		}
	}

	/**
	 * During cycle removing, we have moddified lists of successors and
	 * predecessors. This method restores these lists.
	 * @param graph - as list of vertices
	 * @return graph with restored lists(arcs)
	 */
	private List repairArcs(List graph){
		CombinedVertex firstVertex;

		for (int i1=0; i1<graph.size(); i1++){
			firstVertex = (CombinedVertex) graph.get(i1);
			firstVertex.removeCombinedVertices();
			firstVertex.restoreBaseLists();
		}
		
		CombinedVertex secondVertex;
		List successors;
		List predecessors;
		for (int i1=0; i1<graph.size(); i1++){
			firstVertex = (CombinedVertex) graph.get(i1);
			successors = firstVertex.getSuccList();
			predecessors = firstVertex.getPredList();
			
			for (int i2=0; i2<graph.size(); i2++){
				secondVertex = (CombinedVertex) graph.get(i2);
				if (successors.removeAll(secondVertex.getVertices())&&
					(i1!=i2)){
					successors.add(secondVertex);
				}
				
				if (predecessors.removeAll(secondVertex.getVertices())&&
					(i1!=i2)){
					predecessors.add(secondVertex);
				}
			}
		}
		
		return graph;
	}
	
	/**
	 * This method finds kernel in given graph.
	 * @param graph - list of vertices
	 * @return kernel of given graph as a list of vertices
	 * @throws ProcessingException
	 */
	public List findKernel(List graph) throws ProcessingException{
		List candidates = new ArrayList();
		candidates.addAll(graph);
		
		List kernel = new ArrayList();
		CombinedVertex candidate;
		
		int i1=0;
		while (candidates.size()>0){
			candidate = (CombinedVertex) candidates.get(i1);
			
			if (candidate.getPredList().size()==0){
				kernel.add(candidate);
				candidates.remove(candidate);
				List successors = candidate.getSuccList();
				candidates.removeAll(successors);
				
				CombinedVertex leftVertex;
				for (int i2=0; i2<candidates.size(); i2++){
					leftVertex = (CombinedVertex) candidates.get(i2);
					leftVertex.getPredList().removeAll(successors);
				}
				i1=0;
			} else {
				i1++;
			}
		}
		
		return kernel;
	}

	/**
	 * After finding kernel we could lost some predecessors, so we must restore 
	 * it from successors.
	 * @param subgraph
	 * @return same list with repaired predecessors list
	 */
	public List repairPredLists(List subgraph){
		for (Iterator it1 = subgraph.iterator(); it1.hasNext();) {
			CombinedVertex firstVertex = (CombinedVertex) it1.next();
			//We clear predecessors to fill it only with subgraph's vertices
			firstVertex.getPredList().clear();
			
			for (Iterator it2 = subgraph.iterator(); it2.hasNext();) {
				CombinedVertex secondVertex = (CombinedVertex) it2.next();
				
				if (secondVertex.getSuccList().contains(firstVertex)) {
					firstVertex.addPredecessor(secondVertex);					
				}
			}
		}
		return subgraph;
	}
	
}
