
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

import java.util.List;

import com.dawidweiss.carrot.core.local.ProcessingException;
import com.dawidweiss.carrot.core.local.clustering.RawClustersConsumer;
import com.kgolembniak.carrot.filter.haog.measure.Statistics;

public abstract class Groupper {
	
	/**
	 * Object containing HAOG algorithm logic
	 */
	protected GraphProcessor processor;
	/**
	 * Object containing algorithm specyfic final view construction algorithm. 
	 */
	protected GraphRenderer renderer;
	/**
	 * Object containing algorithm specyfic graph creation algorithm. 
	 */
	protected GraphCreator creator;
	
	/**
	 * Clusters to process
	 */
	protected List clusters;
	/**
	 * Algorithm specific parameters
	 */
	protected Object parameters;
	/**
	 * Documents to process
	 */
	protected List documents;
	/**
	 * Result consumer object
	 */
	protected RawClustersConsumer consumer;
	
	/**
	 * Default constructor.
	 */
	public Groupper(){
		this.processor = new GraphProcessor();
	}
	
	/**
	 * Setter for documents field
	 * @param documents
	 */
	public void setDocument(List documents){
		this.documents = documents;
	}
	
	/**
	 * Setter for parameters field
	 * @param parameters
	 */
	public void setParameters(Object parameters){
		this.parameters = parameters;
	}
	
	/**
	 * Setter for consumer field
	 * @param consumer
	 */
	public void setConsumer(RawClustersConsumer consumer){
		this.consumer = consumer;
	}
	
	/**
	 * This method runs cluster processing
	 * @throws ProcessingException
	 */
	public void process() throws ProcessingException{

		Statistics.getInstance().setValue("Base clusters number", new Integer(clusters.size()));
		Statistics.getInstance().startTimer();

		Statistics.getInstance().startTimer();
		List graph = creator.createGraph(clusters);
		Statistics.getInstance().endTimer("Graph Creation Time");

		Statistics.getInstance().startTimer();
		List cleanGraph = processor.removeCycle(graph);
		Statistics.getInstance().endTimer("Remove Cycle Time");

		Statistics.getInstance().startTimer();
		List kernel  = processor.findKernel(cleanGraph);
		Statistics.getInstance().endTimer("Finding Kernel Time");
		Statistics.getInstance().setValue("Kernel size", new Integer(kernel.size()));

		Statistics.getInstance().endTimer("HAOG Time");
		renderer.renderRawClusters(kernel, documents, parameters, consumer);
	}
}
