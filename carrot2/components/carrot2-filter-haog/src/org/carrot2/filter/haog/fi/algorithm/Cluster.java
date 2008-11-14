
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

package org.carrot2.filter.haog.fi.algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.carrot2.core.clustering.TokenizedDocument;

/**
 * Class representing documents cluster.
 * @author Karol Gołembniak
 */
public class Cluster {
	
	/**
	 * Cluster identifier.
	 */
	private Integer id;

	/**
	 * List of strings describing this cluster.
	 */
	private List description;

	/**
	 * Documents contained by this cluster.
	 */
	private Set documents;
	
	/**
	 * Links to cluster's naighbours.
	 */
	private List neighbours;
	
	/**
	 * Constructor for this class.
	 * @param description - list of strings describing this cluster.
	 */
	public Cluster(List description){
		this.documents = new HashSet();
		this.neighbours = new ArrayList();
		this.description = description;
	}

	/**
	 * Adds document to this cluster.
	 * @param document
	 * @return java.util.Set.add() operation result
	 */
	public boolean addDocument(TokenizedDocument document){
		return this.documents.add(document);
	}
	
	/**
	 * Getter for {@link #description} field. 
	 * @return list of elements describing this cluster
	 */
	public List getDescription() {
		return description;
	}

	/**
	 * Getter for {@link #documents} field. 
	 * @return set of documents contained by this cluster
	 */
	public Set getDocuments() {
		return documents;
	}
	
	/**
	 * This method gets number of common documents in this and cluster given as
	 * parameter. 
	 * @param cluster - Cluster to get documents intersection with.
	 * @return number of common documents
	 */
	public int getIntersectionSize(Cluster cluster){
		HashSet intersection = new HashSet();
		intersection.addAll(this.documents);
		intersection.retainAll(cluster.getDocuments());
		return intersection.size();
	}

	/**
	 * Getter for {@link #neighbours} field.
	 * @return list of cluster's neighbours
	 */
	public List getNeighbours() {
		return neighbours;
	}

	/**
	 * Setter for {@link #neighbours} field.
	 * @param neighbours - List of cluster's neighbours
	 */
	public void setNeighbours(List neighbours) {
		this.neighbours = neighbours;
	}
	
	/**
	 * Adds cluster to claster's neighbours list.
	 * @param cluster - Claster to add
	 * @return java.util.List.add() operation result
	 */
	public boolean addNeighbour(Cluster cluster){
		return this.neighbours.add(cluster);
	}

	/**
	 * Getter for {@link #id} field.
	 * @return Cluster's identifier
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * Setter for {@link #id} field.
	 * @param id - Cluster's identifier
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	
}
