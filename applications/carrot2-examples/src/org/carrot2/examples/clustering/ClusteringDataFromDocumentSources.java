
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
import org.carrot2.core.Controller;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.Document;
import org.carrot2.core.IDocumentSource;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.SharedAttributesDescriptor;
import org.carrot2.examples.ConsoleFormatter;
import org.carrot2.source.boss.BossDocumentSource;
import org.carrot2.source.boss.BossDocumentSourceDescriptor;
import org.carrot2.source.boss.BossNewsSearchService;
import org.carrot2.source.boss.BossSearchService;
import org.carrot2.source.boss.BossSearchServiceDescriptor;
import org.carrot2.source.microsoft.BingDocumentSource;
import org.carrot2.source.microsoft.BingDocumentSourceDescriptor;

/**
 * This example shows how to cluster {@link Document}s retrieved from
 * {@link IDocumentSource}s. There are a number of implementations of this interface in the
 * Carrot2 project, in this example we will cluster results from Microsoft Live (Web
 * search) and Yahoo Boss (news search).
 * <p>
 * It is assumed that you are familiar with {@link ClusteringDocumentList} example.
 * </p>
 *  
 * @see ClusteringDocumentList
 * @see UsingCachingController
 */
public class ClusteringDataFromDocumentSources
{
    public static void main(String [] args)
    {
        /*
         * EXAMPLE 1: fetching documents from Microsoft Live, clustering with Lingo.
         * Attributes for the first query: query, number of results to fetch from the
         * source. Note that the API key defaults to the one assigned for the Carrot2
         * project. Please use your own key for production use.
         */
        final Controller controller = ControllerFactory.createSimple();

        /*
         * As the simple controller discards component instances after processing, the
         * @Init attributes can be provided at the same time as the @Processing ones. For
         * the same reason, you don't need to initialize the simple controller. Please
         * check CachingController for more advanced handling of component life cycle.
         */

        Map<String, Object> attributes = new HashMap<String, Object>();

        BingDocumentSourceDescriptor
            .attributeBuilder(attributes)
                .appid(BingDocumentSource.CARROTSEARCH_APPID);

        SharedAttributesDescriptor
            .attributeBuilder(attributes)
                .query("data mining")
                .results(100);

        ProcessingResult result = controller.process(attributes,
            BingDocumentSource.class, LingoClusteringAlgorithm.class);

        ConsoleFormatter.displayResults(result);

        /*
         * EXAMPLE 2: fetching from Yahoo BOSS (news search), clustering with Lingo.
         * Attributes for the first query: query, number of results to fetch from the
         * source. Again, note the API key. Please use your own key for production use.
         */

        /*
         * Boss document source is generic and can retrieve Web search, news and image
         * results. Pick the service to use by passing the right service implementation.
         */
        attributes.clear();

        BossDocumentSourceDescriptor
            .attributeBuilder(attributes)
            .service(new BossNewsSearchService());

        BossSearchServiceDescriptor
            .attributeBuilder(attributes)
            .appid(BossSearchService.CARROTSEARCH_APPID);

        SharedAttributesDescriptor
            .attributeBuilder(attributes)
            .query("war")
            .results(50);

        result = controller.process(attributes, BossDocumentSource.class,
            LingoClusteringAlgorithm.class);

        ConsoleFormatter.displayResults(result);
    }
}
