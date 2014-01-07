
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
import org.carrot2.clustering.lingo.LingoClusteringAlgorithmDescriptor;
import org.carrot2.core.Cluster;
import org.carrot2.core.Controller;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.Document;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.CommonAttributesDescriptor;
import org.carrot2.examples.ConsoleFormatter;
import org.carrot2.examples.SampleDocumentData;
import org.carrot2.matrix.factorization.IterationNumberGuesser.FactorizationQuality;
import org.carrot2.source.microsoft.Bing3WebDocumentSource;
import org.carrot2.source.microsoft.Bing3WebDocumentSourceDescriptor;


/**
 * This example shows how to customize the behaviour of clustering algorithms and 
 * document sources by setting attributes. For a complete summary of the available
 * attributes, please see Carrot2 manual.
 */
public class UsingAttributes
{
    @SuppressWarnings("unused")
    public static void main(String [] args)
    {
        /* [[[start:using-attributes-raw-map-intro]]]
         * <div>
         * <p>
         * You can change the default behaviour of clustering algorithms and document sources
         * by changing their <em>attributes</em>. For a complete list of available attributes,
         * their identifiers, types and allowed values, please see Carrot2 manual.
         * </p>
         * <p>
         * To pass attributes to Carrot2, put them into a {@link java.util.Map},
         * along with query or documents being clustered. The code shown below searches the
         * web using {@link org.carrot2.source.microsoft.Bing3WebDocumentSource} 
         * and clusters the results using {@link org.carrot2.clustering.lingo.LingoClusteringAlgorithm}
         * customized to create fewer clusters than by default.
         * </p> 
         * </div>
         * [[[end:using-attributes-raw-map-intro]]]
         */
        {
            // [[[start:using-attributes-raw-map]]]
            /* A controller to manage the processing pipeline. */
            final Controller controller = ControllerFactory.createSimple();
            
            /* Prepare attribute map */
            final Map<String, Object> attributes = new HashMap<String, Object>();

            /* Put attribute values using direct keys. */
            attributes.put(CommonAttributesDescriptor.Keys.QUERY, "data mining");
            attributes.put(CommonAttributesDescriptor.Keys.RESULTS, 100);
            attributes.put("LingoClusteringAlgorithm.desiredClusterCountBase", 15);

            /* Put your own API key here! */
            attributes.put(Bing3WebDocumentSourceDescriptor.Keys.APPID, BingKeyAccess.getKey()); 
            
            /* Perform processing */
            final ProcessingResult result = controller.process(attributes,
                Bing3WebDocumentSource.class, LingoClusteringAlgorithm.class);
    
            /* Documents fetched from the document source, clusters created by Carrot2. */
            final List<Document> documents = result.getDocuments();
            final List<Cluster> clusters = result.getClusters();
            // [[[end:using-attributes-raw-map]]]
            
            ConsoleFormatter.displayResults(result);
        }
        
        /* [[[start:using-attributes-builders-intro]]]
         * 
         * <div>
         * <p>
         * As an alternative to the raw attribute map used in the previous example, you
         * can use attribute map builders. Attribute map builders have a number of advantages:
         * </p>
         * 
         * <ul>
         * <li>Type-safety: the correct type of the value will be enforced at compile time</li>
         * <li>Error prevention: unexpected results caused by typos in attribute name strings are avoided</li>
         * <li>Early error detection: in case an attribute's key changes, your compiler will detect that</li>
         * <li>IDE support: your IDE will suggest the right method names and parameters</li>
         * </ul>
         * 
         * <p>
         * A possible disadvantage of attribute builders is that one algorithm's attributes can
         * be divided into a number of builders and hence not readily available in your IDE's auto
         * complete window. Please consult attribute documentation in Carrot2 manual for pointers to 
         * the appropriate builder classes and methods.
         * </p>
         * 
         * <p>
         * The code shown below fetches 100 results for query <em>data mining</em> from 
         * {@link org.carrot2.source.microsoft.Bing3WebDocumentSource} and clusters them using 
         * the {@link org.carrot2.clustering.lingo.LingoClusteringAlgorithm} tuned to create slightly 
         * fewer clusters than by default. Please note how the API key is passed and use your own 
         * key in production deployments.
         * </p>
         * </div> 
         * 
         * [[[end:using-attributes-builders-intro]]]
         */
        {
            /// [[[start:using-attributes-builders]]]
            /* A controller to manage the processing pipeline. */
            final Controller controller = ControllerFactory.createSimple();
            
            /* Prepare attribute map */
            final Map<String, Object> attributes = new HashMap<String, Object>();

            /* Put values using attribute builders */
            CommonAttributesDescriptor
                .attributeBuilder(attributes)
                    .query("data mining")
                    .results(100);
            LingoClusteringAlgorithmDescriptor
                .attributeBuilder(attributes)
                    .desiredClusterCountBase(15)
                    .matrixReducer()
                        .factorizationQuality(FactorizationQuality.HIGH);
                        
            Bing3WebDocumentSourceDescriptor
                .attributeBuilder(attributes)
                    .appid(BingKeyAccess.getKey()); // use your own key here
            
            /* Perform processing */
            final ProcessingResult result = controller.process(attributes,
                Bing3WebDocumentSource.class, LingoClusteringAlgorithm.class);
    
            /* Documents fetched from the document source, clusters created by Carrot2. */
            final List<Document> documents = result.getDocuments();
            final List<Cluster> clusters = result.getClusters();
            /// [[[end:using-attributes-builders]]] 
            
            ConsoleFormatter.displayResults(result);
        }
        
        /* [[[start:using-attributes-output-intro]]]
         * <div>
         * <p>
         * Some algorithms apart from clusters can produce additional, usually
         * diagnostic, output. The output is present in the attributes map contained
         * in the {@link org.carrot2.core.ProcessingResult}. You can read the contents 
         * of that map directly or through the attribute map builders. Carrot2 manual
         * lists and describes in detail the output attributes of each component.
         * </p>
         * <p>
         * The code shown below clusters clusters an example collection of 
         * {@link org.carrot2.core.Document}s using the Lingo algorithm. Lingo can
         * optionally use native platform-specific matrix computation libraries. The
         * example code reads an attribute to find out whether such libraries were
         * successfully loaded and used. 
         * </p> 
         * </div>
         * [[[end:using-attributes-output-intro]]]
         */
        {
            /// [[[start:using-attributes-output]]]
            /* A controller to manage the processing pipeline. */
            final Controller controller = ControllerFactory.createSimple();
            
            /* Prepare attribute map */
            final Map<String, Object> attributes = new HashMap<String, Object>();
            CommonAttributesDescriptor
                .attributeBuilder(attributes)
                    .documents(SampleDocumentData.DOCUMENTS_DATA_MINING);
            LingoClusteringAlgorithmDescriptor
                .attributeBuilder(attributes)
                    .desiredClusterCountBase(15)
                    .matrixReducer()
                        .factorizationQuality(FactorizationQuality.HIGH);

            /* Perform processing */
            final ProcessingResult result = controller.process(attributes,
                LingoClusteringAlgorithm.class);
            
            /* Clusters created by Carrot2, read processing time */
            final List<Cluster> clusters = result.getClusters();
            final Long clusteringTime = CommonAttributesDescriptor.attributeBuilder(
                result.getAttributes()).processingTimeAlgorithm();
            /// [[[end:using-attributes-output]]]
            
            ConsoleFormatter.displayResults(result);
        }
    }
}
