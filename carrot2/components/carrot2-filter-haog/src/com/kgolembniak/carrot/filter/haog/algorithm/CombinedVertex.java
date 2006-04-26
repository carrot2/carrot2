
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
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.dawidweiss.carrot.filter.stc.StcParameters;
import com.dawidweiss.carrot.filter.stc.algorithm.BaseCluster;
import com.dawidweiss.carrot.filter.stc.algorithm.Phrase;
import com.dawidweiss.carrot.filter.stc.algorithm.StemmedTerm;
import com.dawidweiss.carrot.filter.stc.suffixtree.ExtendedBitSet;
import com.kgolembniak.carrot.filter.fi.FIParameters;
import com.kgolembniak.carrot.filter.fi.algorithm.Cluster;

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
	
	/**
	 * This method returns description of vertex, which is displayed
	 * to user.
	 * @param params - Parameters used for description limiting
	 * @return description of this vertex as String.
	 */
	public String getVertexDescription(StcParameters params) {
		phrases = getVertexPhrases(params);

		String description = "";
		for (int i1=0; i1<phrases.size(); i1++){
			final Phrase phrase = (Phrase) phrases.get(i1);
			description += phrase.userFriendlyTerms().trim() + " ";
			//TODO get this parameter from params
			if (i1>3) break;
		}
		
		return description;
	}	
	
	/**
	 * This method returns description of vertex, which is displayed
	 * to user.
	 * @param params - Parameters used for description limiting
	 * @return description of this vertex as String.
	 */
	public String getVertexDescription(FIParameters params) {
		String description = "";
		HashSet descriptions = new HashSet();
		
		Vertex vertex;
		Cluster cluster;
		for (int i1=0; i1<vertices.size(); i1++){
			vertex = (Vertex) vertices.get(i1);
			cluster = (Cluster) vertex.getRepresentedCluster();
			descriptions.addAll(cluster.getDescription());
		}
		
		int i1 = 0;
		for (Iterator it = descriptions.iterator(); it.hasNext();){
			description += (String) it.next();
			i1++;
			if (i1<params.getMaxDescPhraseLength()){
				description += " ";
			} else {
				break;
			}
		}
		
		return description;
	}	

	/**
	 * This method decides which phrase is more important and orders
	 * phrases. This method is taken from  
	 * {@link com.dawidweiss.carrot.filter.stc.algorithm.MergedCluster.createDescriptionPhrases()} 
	 * @param params - Parameters needed to decide about phrase importance.
	 * @return List of phrases ordered descending by their importance.
	 */
	private List getVertexPhrases(StcParameters params) {
		ArrayList phrases = new ArrayList();
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
		for (int i1=0; i1<phrases.size(); i1++){
			firstPhrase = (Phrase) phrases.get(i1);
			
			phraseLoop:
			for (int i2=0; i2<phrases.size(); i2++){
				if (i1!=i2){
					secondPhrase = (Phrase) phrases.get(i2);
					
					List terms = firstPhrase.getTerms();
					for (int i3=0; i3<terms.size(); i3++){
						final StemmedTerm term = (StemmedTerm) terms.get(i3);
						if (!secondPhrase.getTerms().contains(term)){
							continue phraseLoop;
						}
						
						firstPhrase.mostSpecific = false;
						secondPhrase.mostGeneral = false;
					}
				}
			}
		}
		
		for (int i1=0; i1<phrases.size(); i1++){
			firstPhrase = (Phrase) phrases.get(i1);
			if (firstPhrase.mostGeneral) {
				thisPhraseIsUseFull:
				for (int i2=0; i2<phrases.size(); i2++){
					secondPhrase = (Phrase) phrases.get(i2);
					if ((i1!=i2)&&(secondPhrase.mostSpecific)) {
						List terms = firstPhrase.getTerms();
						for (int i3=0; i3<terms.size(); i3++){
							final StemmedTerm term = (StemmedTerm) terms.get(i3);
							if ((!term.isStopWord())&&
								(!secondPhrase.getTerms().contains(term))){
								continue thisPhraseIsUseFull;
							}
							
		                    if ((firstPhrase.getCoverage() - secondPhrase.getCoverage()) < 
		                    	params.getMostGeneralPhraseCoverage()){
		                    	firstPhrase.setSelected(false);
		                        break;
		                    }
						}
					}
				}
			}
		}

		for (int i1=0; i1<phrases.size(); i1++){
			firstPhrase = (Phrase) phrases.get(i1);
			for (int i2=0; i2<phrases.size(); i2++){
				secondPhrase = (Phrase) phrases.get(i2);

				if ((i1!=i2) && firstPhrase.isSelected() && 
					secondPhrase.isSelected() && (firstPhrase.getCoverage() < 
					secondPhrase.getCoverage())){

                    float overlap = 0;
                    float total = 0;
					List terms = firstPhrase.getTerms();
					for (int i3=0; i3<terms.size(); i3++){
						final StemmedTerm term = (StemmedTerm) terms.get(i3); 
						
						if (!term.isStopWord()){
							total += 1.0;
							if (firstPhrase.getTerms().contains(term)) {
								overlap += 1.0;
							}
						}
					}

					if ((overlap / total) > params.getMaxPhraseOverlap()){
						firstPhrase.setSelected(false);
                    }					
				}
			}
		}

		for (int i = 0; i < phrases.size(); i++){
            Phrase current = (Phrase) phrases.get(i);
            if (!current.mostGeneral && !current.mostSpecific){
                current.setSelected(false);
            }
        }
		
        final Object [] objects = phrases.toArray();
        Arrays.sort(
            objects, 0, objects.length,
            new Comparator()
            {
                public int compare(Object a, Object b)
                {
                    Phrase pa = (Phrase) a;
                    Phrase pb = (Phrase) b;

                    if ((pa.isSelected() && pb.isSelected()) || (!pa.isSelected() && !pb.isSelected()))
                    {
                        if (pa.getCoverage() > pb.getCoverage())
                        {
                            return -1;
                        }
                        else if (pa.getCoverage() < pb.getCoverage())
                        {
                            return 1;
                        }
                        else
                        {
                            return 0;
                        }
                    }
                    else
                    {
                        if (pa.isSelected())
                        {
                            return -1;
                        }
                        else
                        {
                            return 1;
                        }
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

}
