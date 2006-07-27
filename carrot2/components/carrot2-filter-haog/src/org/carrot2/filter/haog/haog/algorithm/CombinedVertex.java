
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
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.carrot2.filter.stc.algorithm.BaseCluster;
import org.carrot2.filter.stc.algorithm.Phrase;
import org.carrot2.filter.stc.algorithm.StemmedTerm;
import org.carrot2.filter.stc.suffixtree.ExtendedBitSet;
import org.carrot2.filter.haog.fi.FIParameters;
import org.carrot2.filter.haog.fi.algorithm.Cluster;
import org.carrot2.filter.haog.stc.STCParameters;

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
	
	/**
	 * Documents in this vertex.
	 */
	private ExtendedBitSet documents;
	
	/**
	 * Phrases describing this vertex.
	 */
	private List phrases;
	
	/**
	 * Description for this vertex.
	 */
	private String description;
	
	private boolean used;

	public CombinedVertex(String label){
		super(label);
		this.used = false;
		this.vertices = new ArrayList();
	}

	/**
	 * This constructor creates a CombinedVertex based on Vertex object
	 * @param vertex
	 */
	public CombinedVertex(Vertex vertex){
		super(vertex.label);
		this.used = false;
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
	
	/**
	 * This method returns description of vertex, which is displayed
	 * to user.
	 * @param params - Parameters used for description limiting
	 * @return description of this vertex as String.
	 */
	public String getVertexDescription(STCParameters params) {
		if (this.description!=null){
			return this.description;
		}
		
		phrases = getVertexPhrases(params);

		String description = "";
		int i2 = 0;
		for (int i1=0; i1<phrases.size(); i1++){
			final Phrase phrase = (Phrase) phrases.get(i1);
			final String str = phrase.userFriendlyTerms().trim();
			//remove duplicated phrases
			if (description.indexOf(str) == -1) {
				if (i2>0) {
					description += ", ";
				}
				description += str;
				i2++;
				if (i2>=params.getMaxDescPhraseLength()) break;
			} 				
		}
		
		this.description = description;
		return description;
	}	
	
	/**
	 * This method returns description of vertex, which is displayed
	 * to user.
	 * @param params - Parameters used for description limiting
	 * @return description of this vertex as String.
	 */
	public String getVertexDescription(FIParameters params) {
		if (this.description != null) {
			return this.description; 
		}
		
		ArrayList descriptions = new ArrayList();
		
		Vertex vertex;
		Cluster cluster;
		for (int i1=0; i1<vertices.size(); i1++){
			vertex = (Vertex) vertices.get(i1);
			cluster = (Cluster) vertex.getRepresentedCluster();
			descriptions.add(cluster.getDescription());
		}
		
		int i1=0;
		boolean remove = false;
		ArrayList firstDesc = null;
		ArrayList secondDesc = null;
		while (i1<descriptions.size()){
			firstDesc = (ArrayList) descriptions.get(i1);
			remove = false;
			for (int i2=0; i2<descriptions.size(); i2++){
				if (i1!=i2) {
					secondDesc = (ArrayList) descriptions.get(i2);
					if (secondDesc.containsAll(firstDesc)) {
						remove = true;
						break;
					}
				}
			}
			
			if (remove) {
				descriptions.remove(i1);
			} else {
				i1++;
			}
		}
		
		String description = "";
		ArrayList phrases = null;
		i1 = 0;
		for (Iterator it = descriptions.iterator(); it.hasNext();){
			if (i1>0) {
				description += ", ";
			}
			phrases = (ArrayList) it.next();
			for (int i2=0; i2<phrases.size(); i2++) {
				if (i2>0) {
					description += " ";
				}
				description += phrases.get(i2);
			}
			i1++;
			if (i1 >= params.getMaxDescPhraseLength()){
				break;
			}
		}
		
		this.description = description;
		return description;
	}	

	/**
	 * This method decides which phrase is more important and orders
	 * phrases. Phrases contained in other are removed. 
	 * @param params - Parameters needed to decide about phrase importance.
	 * @return List of phrases ordered descending by their importance.
	 */
	private List getVertexPhrases(STCParameters params) {
		HashSet phrases = new HashSet();
		if (documents==null){
			getDocumentsFromBaseClusters();
		}
		
		BaseCluster cluster;
		Phrase phrase;
		for (int i1=0; i1<vertices.size(); i1++){
			cluster = (BaseCluster) ((Vertex) vertices.get(i1)).getRepresentedCluster();
			phrase = cluster.getPhrase();
			phrase.setCoverage(
					(float) cluster.getNode().getSuffixedDocumentsCount() / 
					documents.numberOfSetBits());
			phrases.add(phrase);
		}
		
		Phrase firstPhrase;
		Phrase secondPhrase;
		HashSet removedPhrases = new HashSet();
		for (Iterator it1 = phrases.iterator(); it1.hasNext();){
			firstPhrase = (Phrase) it1.next();
			
			phraseLoop:
			for (Iterator it2 = phrases.iterator(); it2.hasNext();){
				secondPhrase = (Phrase) it2.next();
				if (firstPhrase!=secondPhrase){
					
					List terms = firstPhrase.getTerms();
					for (int i3=0; i3<terms.size(); i3++){
						final StemmedTerm term = (StemmedTerm) terms.get(i3);
						if (!secondPhrase.getTerms().contains(term)){
							continue phraseLoop;
						}
					}
					
					removedPhrases.add(firstPhrase);
				}
			}
		}
		
		phrases.removeAll(removedPhrases);
		
        final Object [] objects = phrases.toArray();
        Arrays.sort(
            objects, 0, objects.length,
            new Comparator() {
                public int compare(Object a, Object b) {
                    Phrase pa = (Phrase) a;
                    Phrase pb = (Phrase) b;

                    if (pa.getCoverage() > pb.getCoverage()) {
                        return -1;
                    } else if (pa.getCoverage() < pb.getCoverage()){
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }
        );
		
        return java.util.Arrays.asList(objects);
	}

	/**
	 * This method gets documents from all vertices combined by this vertex
	 * and puts them to documents set in this vertex. All duplicates are
	 * removed.
	 */
	private void getDocumentsFromBaseClusters() {
		documents = new ExtendedBitSet();
		BaseCluster cluster;
		for (Iterator it = vertices.iterator(); it.hasNext();){
			cluster = (BaseCluster) ((Vertex) it.next()).getRepresentedCluster();
			documents.or(cluster.getNode().getInternalDocumentsRepresentation());
		}
	}
	
	/**
	 * Returns documents contained in this vertex
	 * @return documents
	 */
	public ExtendedBitSet getDocuments(){
		if (documents==null){
			getDocumentsFromBaseClusters();
		}
		return documents;
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

	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("CombinedVertex=[");
		buffer.append("Label:" + this.label);
		buffer.append(",BaseSuccessors:" + this.baseSuccList.size());
		buffer.append(",BasePredeccessors:" + this.basePredList.size());
		buffer.append(",Successors:" + this.succList.size());
		buffer.append(",Predeccessors:" + this.predList.size());
		buffer.append("]");
		return buffer.toString();
	}

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}
}
