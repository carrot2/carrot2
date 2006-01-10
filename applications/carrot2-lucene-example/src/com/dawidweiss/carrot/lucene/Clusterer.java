
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

package com.dawidweiss.carrot.lucene;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Iterator;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.clustering.RawCluster;
import com.dawidweiss.carrot.core.local.impl.ClustersConsumerOutputComponent;
import com.dawidweiss.carrot.util.tokenizer.SnippetTokenizerLocalFilterComponent;
import com.stachoodev.carrot.filter.lingo.local.LingoLocalFilterComponent;
import com.dawidweiss.carrot.core.local.impl.*;

import com.dawidweiss.carrot.util.tokenizer.languages.dutch.Dutch;
import com.dawidweiss.carrot.util.tokenizer.languages.english.English;
import com.dawidweiss.carrot.util.tokenizer.languages.french.French;
import com.dawidweiss.carrot.util.tokenizer.languages.german.German;
import com.dawidweiss.carrot.util.tokenizer.languages.italian.Italian;
import com.dawidweiss.carrot.util.tokenizer.languages.spanish.Spanish;
import com.dawidweiss.carrot.core.local.linguistic.Language;


/**
 * A sample "process" for clustering Lucene hits. We put
 * together a couple of Carrot2 components that get the
 * input from Lucene and cluster it using Lingo clustering
 * algorithm.
 *
 * <p>Based on the code of clusterer plugin for Nutch.
 *
 * @author Dawid Weiss
 * @version $Id$
 */
public class Clusterer {
    private final LocalController controller;

	/**
	 * An empty public constructor for making new instances
	 * of the clusterer.
	 */
	public Clusterer() {
		controller = new LocalControllerBase();
		// add factories of Carrot2 components to the controller. 
		addComponentFactories();

		// add processes to the controller.
		addProcesses();
	}

	/** Adds the required component factories to a local Carrot2 controller. */
	private void addComponentFactories() {
		// Local nutch input component
		LocalComponentFactory genericInputFactory = new LocalComponentFactoryBase() {
			public LocalComponent getInstance() {
				return new RawDocumentsProducerLocalInputComponent();
			}
		};
		controller.addLocalComponentFactory("input.generic", genericInputFactory);

		// Cluster consumer output component
		LocalComponentFactory clusterConsumerOutputFactory = new LocalComponentFactoryBase() {
			public LocalComponent getInstance() {
				return new ClustersConsumerOutputComponent();
			}
		};
		controller.addLocalComponentFactory("output.cluster-consumer", 
			clusterConsumerOutputFactory);
		
		// Clustering component here.
		LocalComponentFactory lingoFactory = new LocalComponentFactoryBase() {
			public LocalComponent getInstance()
			{
				HashMap defaults = new HashMap();
				
				// These are adjustments settings for the clustering algorithm...
				// You can play with them, but the values below are our 'best guess'
				// settings that we acquired experimentally.
				defaults.put("lsi.threshold.clusterAssignment", "0.150");
				defaults.put("lsi.threshold.candidateCluster",  "0.775");

				// Lingo uses stemmers and stop words from the languages
				// below.
				return new LingoLocalFilterComponent(
					new Language[]
					{ 
						new English(), 
						new Dutch(), 
						new French(), 
						new German(),
						new Italian(), 
						new Spanish() 
					}, defaults);
			}
		};
		controller.addLocalComponentFactory("filter.lingo-old", lingoFactory);
	}

	/** Adds a clustering process to the local controller */	
	private void addProcesses() {
		LocalProcessBase luceneLingoExample 
			= new LocalProcessBase(
				"input.generic",
				"output.cluster-consumer",
				new String [] {"filter.lingo-old"},
				"Example the Lingo clustering algorithm.",
				"");

		try {
			controller.addProcess("lucene-lingo-example", luceneLingoExample);
		} catch (Exception e) {
			throw new RuntimeException("Could not assemble clustering process.", e);
		}
	}
	
	/**
	 * This method invokes the clustering process on the provided
	 * data.
     * @param documents A list of (ordered first-to-last) {@link DocumentAdapter} objects.
	 */
	public List clusterHits(List documents) {
		Map requestParams = new HashMap();

		requestParams.put(
			RawDocumentsProducerLocalInputComponent.PARAM_SOURCE_RAW_DOCUMENTS,
			documents);

		try {
			// the query does not matter because the input component
			// ignores it and simply returns Lucene's documents. But we could
			// rewrite the input component so that it uses Lucene and actually
			// produces result in response to the query written below.
			ProcessingResult result = 
				controller.query("lucene-lingo-example", "pseudo-query", requestParams);

			ClustersConsumerOutputComponent.Result output =
				(ClustersConsumerOutputComponent.Result) result.getQueryResult();

			List outputClusters = output.clusters;

			return outputClusters;
		} catch (MissingProcessException e) {
			throw new RuntimeException("Missing clustering process.", e);
		} catch (Exception e) {
			throw new RuntimeException("Unidentified problems with the clustering: "
                + e.toString(), e);
		}
	}
}
