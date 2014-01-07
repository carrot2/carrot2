
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
import org.carrot2.core.*;
import org.carrot2.core.attribute.CommonAttributesDescriptor;
import org.carrot2.examples.ConsoleFormatter;
import org.carrot2.matrix.factorization.IterationNumberGuesser.FactorizationQuality;
import org.carrot2.source.microsoft.Bing3WebDocumentSource;
import org.carrot2.text.preprocessing.pipeline.CompletePreprocessingPipelineDescriptor;

import com.google.common.collect.Maps;

/**
 * It is possible to initialize a {@link Controller} to host a number of different
 * configurations of the same {@link IDocumentSource} or {@link IClusteringAlgorithm} and
 * invoke them as appropriate. This is achieved by assigning a string identifier to each
 * configuration and then passing the identifier to the
 * {@link Controller#process(Map, String...)} method.
 * <p>
 * One example where this setting may be useful is when your application serves multiple
 * customers, each of which need a different configuration of a document source or a
 * clustering algorithm.
 * </p>
 */
public class MoreConfigurationsOfOneAlgorithmInCachingController
{
    @SuppressWarnings(
    {
        "unchecked"
    })
    public static void main(String [] args)
    {
        /*
         * Create a controller that caches all documents.
         */
        final Controller controller = ControllerFactory.createCachingPooling(IDocumentSource.class);

        /*
         * You can define global values for some attributes. These will apply to all
         * configurations we will define below, unless the specific configuration
         * overrides the global attributes.
         */
        final Map<String, Object> globalAttributes = new HashMap<String, Object>();

        CompletePreprocessingPipelineDescriptor.attributeBuilder(globalAttributes)
                .documentAssigner()
                    .exactPhraseAssignment(false);

        /*
         * Now we will define two different configurations of the Lingo algorithm. One
         * will be optimized for speed of clustering, while the other will optimize the
         * quality of clusters.
         */
        final Map<String, Object> fastAttributes = Maps.newHashMap();
        LingoClusteringAlgorithmDescriptor.attributeBuilder(fastAttributes)
            .desiredClusterCountBase(20)
            .matrixReducer()
                .factorizationQuality(FactorizationQuality.LOW);

        CompletePreprocessingPipelineDescriptor.attributeBuilder(fastAttributes)
                .caseNormalizer()
                    .dfThreshold(2);

        final Map<String, Object> accurateAttributes = Maps.newHashMap();
        LingoClusteringAlgorithmDescriptor.attributeBuilder(accurateAttributes)
            .desiredClusterCountBase(40)
            .matrixReducer()
                .factorizationQuality(FactorizationQuality.HIGH);

        CompletePreprocessingPipelineDescriptor.attributeBuilder(accurateAttributes)
                .documentAssigner()
                    .exactPhraseAssignment(true);

        CompletePreprocessingPipelineDescriptor.attributeBuilder(fastAttributes)
                .caseNormalizer()
                    .dfThreshold(1);

        /*
         * We initialize the controller passing the global attributes and the two 
         * configurations. Notice that a configuration consists of the component
         * class (can be a document source as well as a clustering algorithm), its 
         * string identifier and attributes.
         */
        controller.init(globalAttributes, 
            new ProcessingComponentConfiguration(LingoClusteringAlgorithm.class, 
                "lingo-fast", fastAttributes),
            new ProcessingComponentConfiguration(LingoClusteringAlgorithm.class, 
                "lingo-accurate", accurateAttributes)
        );
        
        /*
         * Now we can call the two different clustering configurations. Notice that 
         * because we now use string identifiers instead of classes, we pass the document
         * source class name rather than the class itself.
         */
        final Map<String, Object> attributes = new HashMap<String, Object>();
        CommonAttributesDescriptor.attributeBuilder(attributes)
            .query("data mining");

        final ProcessingResult fastResult = controller.process(attributes,
            Bing3WebDocumentSource.class.getName(), "lingo-fast");
        ConsoleFormatter.displayClusters(fastResult.getClusters());
        
        final ProcessingResult accurateResult = controller.process(attributes,
            Bing3WebDocumentSource.class.getName(), "lingo-accurate");
        ConsoleFormatter.displayClusters(accurateResult.getClusters());
    }
}
