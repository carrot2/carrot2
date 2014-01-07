
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
import java.util.List;
import java.util.Map;

import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.clustering.stc.STCClusteringAlgorithm;
import org.carrot2.core.Cluster;
import org.carrot2.core.Controller;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.Document;
import org.carrot2.core.IDocumentSource;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.CommonAttributesDescriptor;
import org.carrot2.examples.ConsoleFormatter;
import org.carrot2.source.etools.EToolsDocumentSource;
import org.carrot2.source.microsoft.Bing3WebDocumentSource;
import org.carrot2.source.microsoft.Bing3WebDocumentSourceDescriptor;

/**
 * This example shows how to cluster {@link Document}s retrieved from
 * {@link IDocumentSource}s. There are a number of implementations of this interface in the
 * Carrot2 project, in this example we will cluster results from Microsoft Live (Web
 * search).
 * 
 * <p>
 * It is assumed that you are familiar with {@link ClusteringDocumentList} example.
 * </p>
 *  
 * @see ClusteringDocumentList
 * @see UsingCachingController
 */
public class ClusteringDataFromDocumentSources
{
    @SuppressWarnings("unused")
    public static void main(String [] args)
    {
        /* [[[start:clustering-data-from-document-sources-simple-intro]]]
         * 
         * <div>
         * One common way to use Carrot2 Java API is to fetch a number of documents 
         * from some {@link org.carrot2.core.IDocumentSource} and cluster them using some 
         * {@link org.carrot2.core.IClusteringAlgorithm}. The simplest yet least flexible
         * way to do it is to use the {@link org.carrot2.core.Controller#process(String, Integer, Class...)} 
         * method from the {@link org.carrot2.core.Controller}. The code shown below retrieves 
         * 100 search results for query <em>data mining</em> from 
         * {@link org.carrot2.source.etools.EToolsDocumentSource} and clusters them using 
         * the {@link org.carrot2.clustering.lingo.LingoClusteringAlgorithm}.
         * </div>
         * 
         * [[[end:clustering-data-from-document-sources-simple-intro]]]
         */
        {
            /// [[[start:clustering-data-from-document-sources-simple]]]
            /* A controller to manage the processing pipeline. */
            final Controller controller = ControllerFactory.createSimple();

            /* Perform processing */
            final ProcessingResult result = controller.process("data mining", 100,
                EToolsDocumentSource.class, LingoClusteringAlgorithm.class);
    
            /* Documents fetched from the document source, clusters created by Carrot2. */
            final List<Document> documents = result.getDocuments();
            final List<Cluster> clusters = result.getClusters();
            /// [[[end:clustering-data-from-document-sources-simple]]] 
            
            ConsoleFormatter.displayResults(result);
        }
        
        /* [[[start:clustering-data-from-document-sources-advanced-intro]]]
         * 
         * If your production code needs to fetch documents from popular search engines, 
         * it is very important that you generate and use your own API key rather than Carrot2's 
         * default one. You can pass the API key along with the query and the requested
         * number of results in an attribute map. Carrot2 manual lists all supported attributes
         * along with their keys, types and allowed values. The code shown below, fetches and clusters
         * 50 results from {@link org.carrot2.source.microsoft.Bing3WebDocumentSource}. 
         * 
         * [[[end:clustering-data-from-document-sources-advanced-intro]]]
         */
        {
            /// [[[start:clustering-data-from-document-sources-advanced]]]
            /* A controller to manage the processing pipeline. */
            final Controller controller = ControllerFactory.createSimple();
    
            /* Prepare attributes */
            final Map<String, Object> attributes = new HashMap<String, Object>();
            
            /* Put your own API key here! */
            Bing3WebDocumentSourceDescriptor.attributeBuilder(attributes)
                .appid(BingKeyAccess.getKey());

            /* Query an the required number of results */
            attributes.put(CommonAttributesDescriptor.Keys.QUERY, "clustering");
            attributes.put(CommonAttributesDescriptor.Keys.RESULTS, 50);
    
            /* Perform processing */
            final ProcessingResult result = controller.process(attributes, 
                Bing3WebDocumentSource.class, STCClusteringAlgorithm.class);

            /* Documents fetched from the document source, clusters created by Carrot2. */
            final List<Document> documents = result.getDocuments();
            final List<Cluster> clusters = result.getClusters();
            /// [[[end:clustering-data-from-document-sources-advanced]]]
    
            ConsoleFormatter.displayResults(result);
        }
    }
}
