
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

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.apache.lucene.store.FSDirectory;
import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.examples.ConsoleFormatter;
import org.carrot2.examples.CreateLuceneIndex;
import org.carrot2.source.lucene.LuceneDocumentSource;
import org.carrot2.source.lucene.SimpleFieldMapper;
import org.carrot2.util.attribute.AttributeUtils;

/**
 * This example shows how to cluster {@link Document}s retrieved from a Lucene index using
 * the {@link LuceneDocumentSource}.
 * <p>
 * It is assumed that you are familiar with {@link ClusteringDocumentList} and
 * {@link UsingCachingController} examples.
 * 
 * @see CreateLuceneIndex
 * @see ClusteringDataFromLuceneWithCustomFields
 * @see ClusteringDocumentList
 * @see UsingCachingController
 */
public class ClusteringDataFromLucene
{
    public static void main(String [] args) throws IOException
    {
        /*
         * We will use the CachingController for this example. Running
         * LuceneDocumentSource within the CachingController will let us open the index
         * once per component initialization and not once per query, which would be the
         * case with SimpleController. We will also use this opportunity to show how
         * component-specific attribute values can be passed during CachingComponent
         * initialization.
         */

        /*
         * Create a caching controller that will reuse processing component instances, but
         * will not perform any caching of results produced by components. We will leave
         * caching of documents from Lucene index to Lucene and the operating system
         * caches.
         */
        final Controller controller = ControllerFactory.createPooling();

        /*
         * Prepare a map with component-specific attributes. Here, this map will contain
         * the index location and names of fields to be used to fetch document title and
         * summary.
         */
        final Map<String, Object> luceneGlobalAttributes = new HashMap<String, Object>();

        String indexPath = "put your index path here or pass as the first argument";
        if (args.length == 1)
        {
            indexPath = args[0];
        }

        final String titleFieldName = "title";
        final String contentFieldName = "snippet";

        luceneGlobalAttributes.put(AttributeUtils.getKey(LuceneDocumentSource.class,
            "directory"), FSDirectory.open(new File(indexPath)));
        luceneGlobalAttributes.put(AttributeUtils.getKey(SimpleFieldMapper.class,
            "titleField"), titleFieldName);
        luceneGlobalAttributes.put(AttributeUtils.getKey(SimpleFieldMapper.class,
            "contentField"), contentFieldName);

        luceneGlobalAttributes.put(AttributeUtils.getKey(SimpleFieldMapper.class,
            "searchFields"), Arrays.asList(new String [] {"titleField", "fullContent"}));

        /*
         * Initialize the controller passing the above attributes as component-specific
         * for Lucene. The global attributes map will be empty. Note that we've provided
         * an identifier for our specially-configured Lucene component, we'll need to use
         * this identifier when performing processing.
         */
        controller.init(new HashMap<String, Object>(),
            new ProcessingComponentConfiguration(LuceneDocumentSource.class, "lucene",
                luceneGlobalAttributes));

        /*
         * Perform processing.
         */
        final Map<String, Object> processingAttributes = new HashMap<String, Object>();
        
        String query = "mining";
        processingAttributes.put(AttributeNames.QUERY, query);

        /*
         * We need to refer to the Lucene component by its identifier we set during
         * initialization. As we've not assigned any identifier to the
         * LingoClusteringAlgorithm we want to use, we can its fully qualified class name.
         */
        ProcessingResult process = controller.process(processingAttributes, "lucene",
            LingoClusteringAlgorithm.class.getName());
        
        ConsoleFormatter.displayResults(process);
    }
}
