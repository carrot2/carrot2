
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

import java.util.*;

import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.clustering.stc.STCClusteringAlgorithm;
import org.carrot2.clustering.synthetic.ByUrlClusteringAlgorithm;
import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.examples.ConsoleFormatter;
import org.carrot2.examples.SampleDocumentData;
import org.carrot2.text.vsm.LinearTfIdfTermWeighting;
import org.carrot2.text.vsm.TermDocumentMatrixBuilder;
import org.carrot2.util.attribute.AttributeUtils;

/**
 * This example shows how to cluster a set of documents available as an {@link ArrayList}.
 * This setting is particularly useful for quick experiments with custom data for which
 * there is no corresponding {@link IDocumentSource} implementation. For production use,
 * it's better to implement a {@link IDocumentSource} for the custom document source, so
 * that e.g., the {@link Controller} can cache its results, if needed.
 * 
 * @see ClusteringDataFromDocumentSources
 * @see UsingCachingController
 */
public class ClusteringDocumentList
{
    public static void main(String [] args)
    {
        /*
         * Prepare a Collection of {@link Document} instances. Every document SHOULD have
         * a unique URL (identifier), a title and a snippet (document content), but none
         * of these are obligatory.
         */
        final Collection<Document> documents = SampleDocumentData.DOCUMENTS_DATA_MINING;

        /*
         * We are clustering using a simple controller (no caching, one-time shot).
         */
        final Controller controller = ControllerFactory.createSimple();

        /*
         * All data for components (and between them) is passed using a Map. Place the
         * required attributes and tuning options in the map below before you start
         * processing. Each document source and algorithm comes with a set of attributes
         * that can be tweaked at runtime (during component initialization or processing
         * of every query). Refer to each component's documentation for details.
         */
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(AttributeNames.DOCUMENTS, documents);

        /*
         * We will cluster by URL components first. The algorithm that does this is called
         * ByUrlClusteringAlgorithm. It has no parameters.
         */
        ProcessingResult result = controller.process(attributes,
            ByUrlClusteringAlgorithm.class);
        ConsoleFormatter.displayResults(result);

        /*
         * Now we will cluster the same documents using a more complex text clustering
         * algorithm: Lingo. Note that the process is essentially the same, but we will
         * set an algorithm parameter for term weighting to a non-default value to show
         * how it is done.
         */
        Class<?> algorithm = LingoClusteringAlgorithm.class;
        attributes.clear();
        attributes.put(AttributeNames.DOCUMENTS, documents);
        attributes.put(AttributeUtils.getKey(TermDocumentMatrixBuilder.class,
            "termWeighting"), LinearTfIdfTermWeighting.class);

        /*
         * If you know what query generated the documents you're about to cluster, pass
         * the query to the algorithm, which will usually increase clustering quality.
         */
        attributes.put(AttributeNames.QUERY, "data mining");
        result = controller.process(attributes, algorithm);
        ConsoleFormatter.displayResults(result);

        /*
         * The ProcessingResult object contains everything that has been contributed to
         * the output Map of values. We can, for example, check if native libraries have
         * been used (Lingo uses native matrix libraries on supported platforms and
         * defaults to Java equivalents on all others).
         */
        Boolean nativeUsed = (Boolean) result.getAttributes().get(
            AttributeUtils.getKey(algorithm, "nativeMatrixUsed"));
        System.out.println("Native libraries used: " + nativeUsed);
        
        /*
         * Finally, we'll cluster the same documents with another text clustering 
         * algorithm: Suffix Tree Clustering (STC).
         */
        algorithm = STCClusteringAlgorithm.class;
        attributes.clear();
        attributes.put(AttributeNames.QUERY, "data mining");
        attributes.put(AttributeNames.DOCUMENTS, documents);
        result = controller.process(attributes, algorithm);
        ConsoleFormatter.displayResults(result);
    }
}
