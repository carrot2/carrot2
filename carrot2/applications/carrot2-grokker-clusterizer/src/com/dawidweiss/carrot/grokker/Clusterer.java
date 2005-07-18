package com.dawidweiss.carrot.grokker;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.impl.ClustersConsumerOutputComponent;
import com.stachoodev.carrot.filter.lingo.local.LingoLocalFilterComponent;
import com.dawidweiss.carrot.core.local.impl.*;

import com.dawidweiss.carrot.util.tokenizer.languages.english.English;
import com.dawidweiss.carrot.core.local.linguistic.Language;


/**
 * We put together a sample process and a local controller.
 * This will be used to cluster Grokker data later on.
 *
 * @author Dawid Weiss
 * @version $Id$
 */
public class Clusterer {
    /** A local controller that governs components and Carrot query processing */
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

				// Lingo uses stemmers and stop words from the languages below.
				return new LingoLocalFilterComponent(
					new Language[]
					{ 
						new English() 
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
			controller.addProcess("lingo-example", luceneLingoExample);
		} catch (Exception e) {
			throw new RuntimeException("Could not assemble clustering process.", e);
		}
	}
	
	/**
	 * This method invokes the clustering process on the provided data.
     * @param documents A list of (ordered first-to-last) {@link DocumentAdapter} objects.
	 */
	public List clusterHits(List documents, String query) {
		Map requestParams = new HashMap();

        if (query != null) {
            requestParams.put(
                LocalInputComponent.PARAM_QUERY,
                query);
        }
		requestParams.put(
			RawDocumentsProducerLocalInputComponent.PARAM_SOURCE_RAW_DOCUMENTS,
			documents);

		try {
			// the query does not matter because the input component
			// ignores it and simply returns Lucene's documents. But we could
			// rewrite the input component so that it uses Lucene and actually
			// produces result in response to the query written below.
			ProcessingResult result = 
				controller.query("lingo-example", query, requestParams);

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
