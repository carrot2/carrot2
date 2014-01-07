
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

import java.io.File;
import java.util.Map;

import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.clustering.stc.STCClusteringAlgorithm;
import org.carrot2.core.Controller;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.IClusteringAlgorithm;
import org.carrot2.core.IDocumentSource;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.CommonAttributesDescriptor;
import org.carrot2.examples.ConsoleFormatter;
import org.carrot2.examples.SampleDocumentData;
import org.carrot2.text.linguistic.DefaultLexicalDataFactoryDescriptor;
import org.carrot2.text.linguistic.LexicalDataLoaderDescriptor;
import org.carrot2.util.resource.DirLocator;
import org.carrot2.util.resource.ResourceLookup;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * This example shows how to configure the location of lexical resources to be something
 * else than the default (by default lexical resources are read using the context class
 * loader).
 */
public class UsingCustomLexicalResources
{
    public static void main(String [] args)
    {
        @SuppressWarnings("unchecked")
        final Controller controller = ControllerFactory.createCachingPooling(IDocumentSource.class);

        // We will pass our custom resource locator at initialization time. There is a
        // variety of implementations of IResourceLocator interface, we will use
        // an explicit filesystem folder in the current working directory.
        File resourcesDir = new File("resources");
        ResourceLookup resourceLookup = new ResourceLookup(new DirLocator(resourcesDir));

        Map<String, Object> attrs = Maps.newHashMap();

        // Note that we tell the linguistic component to merge all lexical resources,
        // this is the default setting and it usually helps with multi-lingual content.
        DefaultLexicalDataFactoryDescriptor.attributeBuilder(attrs)
            .mergeResources(true);
        LexicalDataLoaderDescriptor.attributeBuilder(attrs)
            .resourceLookup(resourceLookup);

        controller.init(attrs);

        // Cluster some data with Lingo and STC.
        clusterAndDisplayClusters(controller, LingoClusteringAlgorithm.class);
        clusterAndDisplayClusters(controller, STCClusteringAlgorithm.class);
    }

    /**
     * Clusters results for query "data mining" and displays the clusters.
     */
    private static void clusterAndDisplayClusters(final Controller controller,
        final Class<? extends IClusteringAlgorithm> clusteringAlgorithm)
    {
        final Map<String, Object> processingAttributes = Maps.newHashMap();

        CommonAttributesDescriptor.attributeBuilder(processingAttributes)
            .documents(Lists.newArrayList(SampleDocumentData.DOCUMENTS_DATA_MINING))
            .query("data mining");

        final ProcessingResult result = controller.process(processingAttributes, 
            clusteringAlgorithm);
        ConsoleFormatter.displayClusters(result.getClusters(), 0);
    }
}
