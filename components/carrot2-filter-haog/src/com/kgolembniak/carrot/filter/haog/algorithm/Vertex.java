package com.kgolembniak.carrot.filter.haog.algorithm;

import java.util.ArrayList;
import java.util.List;

import com.dawidweiss.carrot.filter.stc.algorithm.BaseCluster;

/**
 * Class representing vertex in graph.
 * @author Karol Gołembniak
 */
public class Vertex {
	/**
	 * Label for vertex.
	 */
	protected String label;
	/**
	 * List of successors.
	 */
	protected List succList;
	/**
	 * List of predecessors.
	 */
	protected List predList;
	/**
	 * Unmodifiable list of successors.
	 */
	protected List baseSuccList;
	/**
	 * Unmodifiable list of predecessors.
	 */
	protected List basePredList;
	/**
	 * Base cluster represented by this vertex.
	 */
	private BaseCluster representedCluster;
	
	public Vertex(String label){
		this.label = label;
		succList = new ArrayList();
		predList = new ArrayList();
		baseSuccList = new ArrayList();
		basePredList = new ArrayList();
	}

	public List getPredList() {
		return predList;
	}

	public List getSuccList() {
		return succList;
	}

	public void setPredList(List predList) {
		this.predList = predList;
	}

	public void setSuccList(List succList) {
		this.succList = succList;
	}

	public void addPredecessor(Vertex pred){
		predList.add(pred);
		if (!(pred instanceof CombinedVertex)){
			basePredList.add(pred);
		}
	}

	public void addSuccessor(Vertex succ){
		succList.add(succ);
		if (!(succ instanceof CombinedVertex)){
			baseSuccList.add(succ);
		}
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void removeSuccessor(Vertex successor){
		succList.remove(successor);
	}

	public void removePredecessor(Vertex predecessor){
		predList.remove(predecessor);
	}

	public BaseCluster getRepresentedCluster() {
		return representedCluster;
	}

	public void setRepresentedCluster(BaseCluster representedCluster) {
		this.representedCluster = representedCluster;
	}

	protected List getBasePredList() {
		return basePredList;
	}

	protected List getBaseSuccList() {
		return baseSuccList;
	}
}
