
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2012, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.examples.clustering.japanese;

import java.util.List;
import java.util.Map;

import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.clustering.stc.STCClusteringAlgorithm;
import org.carrot2.core.Cluster;
import org.carrot2.core.Controller;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.Document;
import org.carrot2.core.IClusteringAlgorithm;
import org.carrot2.core.IDocumentSource;
import org.carrot2.core.LanguageCode;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.attribute.CommonAttributesDescriptor;
import org.carrot2.examples.ConsoleFormatter;
import org.carrot2.text.clustering.MultilingualClusteringDescriptor;

import com.google.common.collect.Maps;

/**
 * This example shows how to cluster documents in Japanese. 
 */
public class ClusteringJapaneseDocuments
{
    @SuppressWarnings("unchecked")
    public static void main(String [] args)
        throws Exception
    {
        final Controller controller = ControllerFactory.createCachingPooling(IDocumentSource.class);

        Map<String, Object> attrs = Maps.newHashMap();

        // Setup Japanese preprocessing pipeline.
        MultilingualClusteringDescriptor.attributeBuilder(attrs)
            .defaultLanguage(LanguageCode.JAPANESE);

        controller.init(attrs);

        // Cluster sample document sets with Lingo and STC.
        for (String input : new String [] {
            "datamining.ja.xml",
            "sushi.ja.xml",
            "tokyo.ja.xml"
        }) {
            ProcessingResult pr = ProcessingResult.deserialize(
                ClusteringJapaneseDocuments.class.getResourceAsStream(input));

            final List<Document> documents = pr.getDocuments();
            final String query = pr.getAttribute(AttributeNames.QUERY);

            for (Class<? extends IClusteringAlgorithm> algorithmClass : new Class [] {
                LingoClusteringAlgorithm.class,
                STCClusteringAlgorithm.class
            })
            {
                System.out.println("\n\n ==> " + input + " clustered with: " + algorithmClass.getSimpleName());
                clusterAndDisplayClusters(documents, query, controller, algorithmClass);
            }
        }
    }

    /**
     * Clusters results for query "data mining" and displays the clusters.
     */
    private static void clusterAndDisplayClusters(
        final List<Document> documents,
        final String query,
        final Controller controller,
        final Class<? extends IClusteringAlgorithm> clusteringAlgorithm)
    {
        final Map<String, Object> processingAttributes = Maps.newHashMap();

        CommonAttributesDescriptor.attributeBuilder(processingAttributes)
            .documents(documents)
            .query(query);

        final ProcessingResult result = controller.process(processingAttributes, 
            clusteringAlgorithm);
        ConsoleFormatter.displayClusters(result.getClusters(), 0, new ConsoleFormatter.ClusterDetailsFormatter() {
            @Override
            public String formatClusterDetails(Cluster cluster)
            {
                return "";
            }
        });
    }
}
