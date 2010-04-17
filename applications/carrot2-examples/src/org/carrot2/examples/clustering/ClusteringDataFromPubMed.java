
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
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
import org.carrot2.examples.ConsoleFormatter;
import org.carrot2.matrix.factorization.LocalNonnegativeMatrixFactorizationFactory;
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
         * For this example we use the SimpleController, which does not perform any
         * caching. If you need to cache documents, clusters or Carrot2 component
         * instances, please see the CachingController example.
         */
        final Controller controller = ControllerFactory.createSimple();
        Map<String, Object> attributes = new HashMap<String, Object>();

        /*
         * Search attributes.
         */
        attributes.put(AttributeNames.QUERY, "heart");
        attributes.put(AttributeNames.RESULTS, 100);

        /*
         * Optionally, you can also pass some attributes for the clustering algorithm. See
         * http://download.carrot2.org/head/manual/#section.component.lingo for a full
         * list.
         */
        attributes.put("LingoClusteringAlgorithm.factorizationFactory",
            LocalNonnegativeMatrixFactorizationFactory.class);
        attributes.put("LingoClusteringAlgorithm.titleWordsBoost", 7.0);

        ProcessingResult result = controller.process(attributes,
            PubMedDocumentSource.class, LingoClusteringAlgorithm.class);

        ConsoleFormatter.displayResults(result);
    }
}
