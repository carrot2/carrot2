package com.kgolembniak.carrot.filter.haog.algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Class representing vertex, which can consist other vertex. This is usefull
 * when replacing a cycle in graph with one vertex.
 * @author Karol Gołembniak
 */
public class CombinedVertex extends Vertex{
	/**
	 * Vertices combined by this vertex.
	 */
	private List vertices;
	
	public CombinedVertex(String label){
		super(label);
		vertices = new ArrayList();
	}

	/**
	 * This constructor creates a CombinedVertex based on Vertex object
	 * @param vertex
	 */
	public CombinedVertex(Vertex vertex){
		super(vertex.label);
		this.vertices = new ArrayList();
		this.vertices.add(vertex);
		if (vertex instanceof CombinedVertex) {
			this.vertices.addAll(((CombinedVertex)vertex).getVertices());
		}
		this.succList.addAll(vertex.getSuccList());
		this.predList.addAll(vertex.getPredList());
	}

	public List getVertices() {
		return vertices;
	}

	/**
	 * Sets vertices list to the given list, sets also successors 
	 * and presecessors list 
	 * @param vertices - List of new vertices for combined vertex
	 */
	public void setVertices(List vertices) {
		Vertex vertex = null;
		
		//Sets remove duplicates from list
		HashSet succSet = new HashSet();
		HashSet predSet = new HashSet();
		
		for (int i1=0; i1<vertices.size(); i1++){
			vertex = (Vertex) vertices.get(i1);
			if (vertex instanceof CombinedVertex){
				this.vertices.addAll(((CombinedVertex)vertex).getVertices());
				this.vertices.add(vertex);
			} else {
				this.vertices.add(vertex);
			}
			succSet.addAll(vertex.getSuccList());
			predSet.addAll(vertex.getPredList());
		}

		this.succList.addAll(succSet);
		this.predList.addAll(predSet);
		this.succList.removeAll(vertices);
		this.predList.removeAll(vertices);
	}
	
	/**
	 * Adds new vertex to combined vertex and updates successors and 
	 * predecessors lists.
	 * @param vertex - Vertex to add.
	 */
	public void addVertex(Vertex vertex){
		if (vertex instanceof CombinedVertex){
			this.vertices.addAll(((CombinedVertex)vertex).getVertices());
			this.vertices.add(vertex);
		} else {
			this.vertices.add(vertex);
		}

		//Prevent duplicates
		this.succList.removeAll(vertex.getSuccList());
		this.predList.removeAll(vertex.getPredList());
		
		this.succList.addAll(vertex.getSuccList());
		this.predList.addAll(vertex.getPredList());

		this.succList.remove(vertex);
		this.predList.remove(vertex);
	}
	
	/**
	 * Adds list of vertices to combined vertex and updates successors
	 * and predecessors lists.
	 * @param vertices - List of vertices to add.
	 */	
	public void addVertices(List vertices){
		
		//Sets remove duplicates from list
		HashSet succSet = new HashSet();
		HashSet predSet = new HashSet();
		
		Vertex vertex = null;
		for (int i1=0; i1<vertices.size(); i1++){
			vertex = (Vertex) vertices.get(i1);
			if (vertex instanceof CombinedVertex){
				this.vertices.addAll(((CombinedVertex)vertex).getVertices());
				this.vertices.add(vertex);
			} else {
				this.vertices.add(vertex);
			}
			succSet.addAll(vertex.getSuccList());
			predSet.addAll(vertex.getPredList());
		}

		this.succList.addAll(succSet);
		this.predList.addAll(predSet);
		this.succList.removeAll(vertices);
		this.predList.removeAll(vertices);
	}
	
	/**
	 * After combining some vertices into combined vertex, we have to update
	 * references in other vertices to have new graph structure. This method
	 * updates successors list.
	 * @param vertex
	 */
	public void updateSuccList(CombinedVertex vertex){
		if (this.succList.removeAll(vertex.getVertices())){
			this.succList.remove(vertex);
			this.succList.add(vertex);
		}
	}

	/**
	 * After combining some vertices into combined vertex, we have to update
	 * references in other vertices to have new graph structure. This method
	 * updates predecessors list.
	 * @param vertex
	 */
	public void updatePredList(CombinedVertex vertex){
		if (this.predList.removeAll(vertex.getVertices())){
			this.predList.remove(vertex);
			this.predList.add(vertex);
		}
	}
	
	/**
	 * This method removes combined vertices, which could be added during 
	 * cycle removing, from included vertices. 
	 */
	public void removeCombinedVertices(){
		Vertex includedVertex;
		int i1=0;
		while (i1<vertices.size()){
			includedVertex = (Vertex) vertices.get(i1);
			if (includedVertex instanceof CombinedVertex){
				vertices.remove(includedVertex);
			} else {
				i1++;
			}
		}
	}
	
	/**
	 * While processing cycles, we modify vertexes successors and predecessors
	 * lists. This method restores them from base, unmodifiable lists.
	 */
	public void restoreBaseLists(){
		this.predList.clear();
		this.succList.clear();
		
		//Sets remove duplicates from list
		HashSet succSet = new HashSet();
		HashSet predSet = new HashSet();
		
		Vertex vertex = null;
		for (int i1=0; i1<vertices.size(); i1++){
			vertex = (Vertex) vertices.get(i1);
			succSet.addAll(vertex.getBaseSuccList());
			predSet.addAll(vertex.getBasePredList());
		}
		
		this.succList.addAll(succSet);
		this.predList.addAll(predSet);
	}
	
//Method used for tests only
	public void printVertex(){
		System.out.print(label);
		if (vertices.size()>0){
			System.out.print(" -> [");
			for(int i1=0; i1<vertices.size(); i1++){
				System.out.print(((Vertex)vertices.get(i1)).getLabel());
				if (i1!=vertices.size()-1){
					System.out.print(",");
				}
			}
			System.out.println("]");
		} else {
			System.out.println(" [single]");
		}
	}
	
	public void printSuccessors(){
		System.out.print("Successors: [");
		for(int i1=0; i1<succList.size(); i1++){
			System.out.print(((Vertex)succList.get(i1)).getLabel());
			if (i1!=succList.size()-1){
				System.out.print(",");
			}
		}		
		System.out.println("]");
	}
	
	public void printPredecessors(){
		System.out.print("Predecessors: [");
		for(int i1=0; i1<predList.size(); i1++){
			System.out.print(((Vertex)predList.get(i1)).getLabel());
			if (i1!=predList.size()-1){
				System.out.print(",");
			}
		}		
		System.out.println("]");
	}

}
