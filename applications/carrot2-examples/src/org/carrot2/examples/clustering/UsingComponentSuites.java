
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

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.examples.ConsoleFormatter;
import org.carrot2.util.CloseableUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * This example shows how to use <a href="http://download.carrot2.org/head/manual/#section.customizing.component-suites-and-attributes.component-suites"
 * >Carrot<sup>2</sup> component suites</a>. Component suites come handy when you need to
 * manage a number of document sources or clustering algorithms, along with their
 * attributes and some metadata. such as human readable labels.
 */
public class UsingComponentSuites
{
    public static void main(String [] args) throws Exception
    {
        @SuppressWarnings("unchecked")
        final Controller controller = ControllerFactory.createCachingPooling(IDocumentSource.class);

        // Initialization-time attributes that will apply to all components.
        final Map<String, Object> initAttributes = Maps.newHashMap();

        // We'll read the component suite definition from an XML stream.
        InputStream suiteXml = null;
        try
        {
            // We're reading from the classpath, but any other stream will do too.
            suiteXml = UsingComponentSuites.class
                .getResourceAsStream("/suites/suite-examples.xml");

            // Deserialize the component suite definition
            final ProcessingComponentSuite suite = ProcessingComponentSuite
                .deserialize(suiteXml);
            
            // Initialize the controller with the suite. All components from the suite
            // will be available for processing within this controller.
            controller.init(initAttributes, suite.getComponentConfigurations());

            // From the suite definition, you can get the document sources and clustering
            // algorithm descriptors.
            final List<DocumentSourceDescriptor> sources = suite.getSources();
            final List<String> sourceIds = Lists.transform(sources,
                ProcessingComponentDescriptor.ProcessingComponentDescriptorToId.INSTANCE);
            System.out.println("Found " + sourceIds.size() + " document sources: "
                + sourceIds);

            final List<ProcessingComponentDescriptor> algorithms = suite.getAlgorithms();
            final List<String> algorithmIds = Lists.transform(algorithms,
                ProcessingComponentDescriptor.ProcessingComponentDescriptorToId.INSTANCE);
            System.out.println("Found " + algorithmIds.size() + " clutering algorithms: "
                + algorithmIds + "\n\n");

            // Run not more than two algorithms on not more than two sources
            for (int s = 0; s < Math.min(sourceIds.size(), 2); s++)
            {
                for (int a = 0; a < Math.min(algorithmIds.size(), 2); a++)
                {
                    // You can retrieve some metadata about the components, such as
                    // human-readable label, from their descriptors.
                    System.out.println("Querying " + sources.get(s).getLabel()
                        + ", clustering with " + algorithms.get(a).getLabel());

                    // As usual, we pass attributes for processing
                    final Map<String, Object> attributes = Maps.newHashMap();
                    attributes.put(AttributeNames.QUERY, "data mining");

                    // Pass component ids to the controller to perform processing
                    final ProcessingResult result = controller.process(attributes,
                        sourceIds.get(s), algorithmIds.get(a));
                    ConsoleFormatter.displayClusters(result.getClusters());
                    System.out.println();
                }
            }
        }
        finally
        {
            CloseableUtils.close(suiteXml);
        }
    }
}
