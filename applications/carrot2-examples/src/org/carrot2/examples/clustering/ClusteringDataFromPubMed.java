
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

package org.carrot2.examples.clustering;

import java.util.HashMap;
import java.util.Map;

import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.examples.ExampleUtils;
import org.carrot2.source.pubmed.PubMedDocumentSource;

/**
 * This example shows how to cluster {@link Document}s retrieved from
 * {@link PubMedDocumentSource}
 *  
 * @see ClusteringDocumentList
 * @see UsingCachingController
 */
public class ClusteringDataFromPubMed
{
    public static void main(String [] args)
    {
        /*
         * EXAMPLE 1: fetching documents from Microsoft Live, clustering with Lingo.
         * Attributes for the first query: query, number of results to fetch from the
         * source. 
         */
        SimpleController controller = new SimpleController();

        /*
         * As the simple controller discards component instances after processing, the
         * @Init attributes can be provided at the same time as the @Processing ones. For
         * the same reason, you don't need to initialize the simple controller. Please
         * check CachingController for more advanced handling of component life cycle.
         */
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(AttributeNames.QUERY, "heart");
        attributes.put(AttributeNames.RESULTS, 100);

        ProcessingResult result = controller.process(attributes,
            PubMedDocumentSource.class, LingoClusteringAlgorithm.class);
        
        ExampleUtils.displayResults(result);
    }
}
