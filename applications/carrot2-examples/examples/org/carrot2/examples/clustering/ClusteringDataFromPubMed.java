
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2014, Dawid Weiss, Stanisław Osiński.
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
import org.carrot2.clustering.lingo.LingoClusteringAlgorithmDescriptor;
import org.carrot2.clustering.lingo.LingoClusteringAlgorithmDescriptor.AttributeBuilder;
import org.carrot2.core.*;
import org.carrot2.core.attribute.CommonAttributesDescriptor;
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
        CommonAttributesDescriptor
            .attributeBuilder(attributes)
            .query("heart")
            .results(100);

        /*
         * Optionally, you can also pass some attributes for the clustering algorithm. See
         * http://download.carrot2.org/head/manual/#section.component.lingo for a full
         * list.
         */
        AttributeBuilder builder = LingoClusteringAlgorithmDescriptor.attributeBuilder(attributes);
        builder.matrixReducer().factorizationFactory(LocalNonnegativeMatrixFactorizationFactory.class);
        builder.matrixBuilder().titleWordsBoost(7);

        ProcessingResult result = controller.process(attributes,
            PubMedDocumentSource.class, LingoClusteringAlgorithm.class);

        ConsoleFormatter.displayResults(result);
    }
}
