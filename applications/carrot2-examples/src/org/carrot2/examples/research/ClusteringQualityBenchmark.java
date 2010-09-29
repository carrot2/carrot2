
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

package org.carrot2.examples.research;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Map;

import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.clustering.stc.STCClusteringAlgorithm;
import org.carrot2.core.Controller;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.IProcessingComponent;
import org.carrot2.output.metrics.ClusteringMetricsCalculator;
import org.carrot2.output.metrics.ContaminationMetricDescriptor;
import org.carrot2.output.metrics.NormalizedMutualInformationMetricDescriptor;
import org.carrot2.output.metrics.PrecisionRecallMetricDescriptor;
import org.carrot2.source.ambient.AmbientDocumentSource;
import org.carrot2.source.ambient.AmbientDocumentSource.AmbientTopic;
import org.carrot2.source.ambient.AmbientDocumentSourceDescriptor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Runs a clustering quality benchmark based on the data set embedded in
 * {@link AmbientDocumentSource}.
 */
public class ClusteringQualityBenchmark
{
    public static void main(String [] args)
    {
        // Disable excessive logging
        final AmbientTopic [] topics = AmbientDocumentSource.AmbientTopic.values();
        final Controller controller = ControllerFactory.createSimple();

        // List of algorithms to test
        final ArrayList<Class<? extends IProcessingComponent>> algorithms = Lists
            .newArrayList();
        algorithms.add(LingoClusteringAlgorithm.class);
        algorithms.add(STCClusteringAlgorithm.class);

        // List of metrics to output
        final ArrayList<String> metrics = Lists.newArrayList(
            ContaminationMetricDescriptor.Keys.WEIGHTED_AVERAGE_CONTAMINATION,
            PrecisionRecallMetricDescriptor.Keys.WEIGHTED_AVERAGE_F_MEASURE,
            PrecisionRecallMetricDescriptor.Keys.WEIGHTED_AVERAGE_PRECISION,
            PrecisionRecallMetricDescriptor.Keys.WEIGHTED_AVERAGE_RECALL,
            NormalizedMutualInformationMetricDescriptor.Keys.NORMALIZED_MUTUAL_INFORMATION);

        final Map<String, Object> attributes = Maps.newHashMap();

        System.out
            .println("Topic\tAlgorithm\tContamination\tF-Score\tPrecision\tRecall\tNMI");
        for (AmbientTopic topic : topics)
        {
            for (Class<? extends IProcessingComponent> algorithm : algorithms)
            {
                AmbientDocumentSourceDescriptor
                    .attributeBuilder(attributes).topic(topic);

                controller.process(attributes, AmbientDocumentSource.class, algorithm,
                    ClusteringMetricsCalculator.class);

                System.out.print(topic.name() + "\t" + algorithm.getSimpleName());
                for (String metricKey : metrics)
                {
                    System.out.print("\t"
                        + MessageFormat.format("{0,number,#.####}", attributes
                            .get(metricKey)));
                }
                System.out.println();
            }
        }

    }
}
