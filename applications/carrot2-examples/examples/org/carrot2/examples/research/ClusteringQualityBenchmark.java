
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

package org.carrot2.examples.research;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.clustering.stc.STCClusteringAlgorithm;
import org.carrot2.core.*;
import org.carrot2.output.metrics.ClusteringMetricsCalculator;
import org.carrot2.output.metrics.ContaminationMetricDescriptor;
import org.carrot2.output.metrics.NormalizedMutualInformationMetricDescriptor;
import org.carrot2.output.metrics.PrecisionRecallMetricDescriptor;
import org.carrot2.source.ambient.AmbientDocumentSource;
import org.carrot2.source.ambient.AmbientDocumentSource.AmbientTopic;
import org.carrot2.source.ambient.AmbientDocumentSourceDescriptor;
import org.carrot2.text.util.TabularOutput;

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

        TabularOutput t = new TabularOutput(new PrintWriter(System.out));
        t.columnSeparator(" | ");
        t.defaultFormat(Double.class).format("%.3f");
        t.addColumn("Topic").alignLeft().format("%-18s");
        t.addColumn("Algorithm").alignLeft().format("%-15s");

        for (AmbientTopic topic : topics)
        {
            for (Class<? extends IProcessingComponent> algorithm : algorithms)
            {
                final Map<String, Object> attributes = Maps.newHashMap();
                AmbientDocumentSourceDescriptor.attributeBuilder(attributes).topic(topic);

                ProcessingResult result = controller.process(
                    attributes, AmbientDocumentSource.class, algorithm, ClusteringMetricsCalculator.class);

                t.rowData("Topic", topic.name());
                t.rowData("Algorithm", algorithm.getSimpleName());

                Map<String, Object> attrs = result.getAttributes();

                t.rowData(
                    "Contamination", 
                    attrs.get(ContaminationMetricDescriptor.Keys.WEIGHTED_AVERAGE_CONTAMINATION));

                t.rowData(
                    "F-Score", 
                    attrs.get(PrecisionRecallMetricDescriptor.Keys.WEIGHTED_AVERAGE_F_MEASURE));

                t.rowData(
                    "Precision", 
                    attrs.get(PrecisionRecallMetricDescriptor.Keys.WEIGHTED_AVERAGE_PRECISION));

                t.rowData(
                    "Recall", 
                    attrs.get(PrecisionRecallMetricDescriptor.Keys.WEIGHTED_AVERAGE_RECALL));

                t.rowData(
                    "NMI", 
                    attrs.get(NormalizedMutualInformationMetricDescriptor.Keys.NORMALIZED_MUTUAL_INFORMATION));

                t.nextRow();
            }
        }

    }
}
