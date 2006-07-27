
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
	 * Cluster represented by this vertex.
	 */
	private Object representedCluster;
	
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

	public Object getRepresentedCluster() {
		return representedCluster;
	}

	public void setRepresentedCluster(Object representedCluster) {
		this.representedCluster = representedCluster;
	}

	protected List getBasePredList() {
		return basePredList;
	}

	protected List getBaseSuccList() {
		return baseSuccList;
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("Vertex=[");
		buffer.append("Label:" + this.label);
		buffer.append(",BaseSuccessors:" + this.baseSuccList.size());
		buffer.append(",BasePredeccessors:" + this.basePredList.size());
		buffer.append(",Successors:" + this.succList.size());
		buffer.append(",Predeccessors:" + this.predList.size());
		buffer.append("]");
		return buffer.toString();
	}	
}
