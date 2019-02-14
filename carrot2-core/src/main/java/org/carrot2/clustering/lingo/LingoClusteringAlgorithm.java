
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.clustering.lingo;

import com.carrotsearch.hppc.BitSet;
import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.attribute.CommonAttributes;
import org.carrot2.core.attribute.Internal;
import org.carrot2.core.attribute.Processing;
import org.carrot2.text.linguistic.LanguageModel;
import org.carrot2.text.linguistic.LanguageModels;
import org.carrot2.text.preprocessing.LabelFormatter;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.text.preprocessing.pipeline.CompletePreprocessingPipeline;
import org.carrot2.text.vsm.ReducedVectorSpaceModelContext;
import org.carrot2.text.vsm.TermDocumentMatrixBuilder;
import org.carrot2.text.vsm.TermDocumentMatrixReducer;
import org.carrot2.text.vsm.VectorSpaceModelContext;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.DoubleRange;
import org.carrot2.util.attribute.constraint.IntRange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Lingo clustering algorithm. Implementation as described in: <i> "Stanisław Osiński,
 * Dawid Weiss: A Concept-Driven Algorithm for Clustering Search Results. IEEE Intelligent
 * Systems, May/June, 3 (vol. 20), 2005, pp. 48—54."</i>.
 */
@Bindable(prefix = "LingoClusteringAlgorithm", inherit = CommonAttributes.class)
@Label("Lingo Clustering")
public class LingoClusteringAlgorithm extends ProcessingComponentBase implements
    IClusteringAlgorithm
{
    /**
     * Query that produced the documents. The query will help the algorithm to create
     * better clusters. Therefore, providing the query is optional but desirable.
     */
    @Processing
    @Input
    @Internal
    @Attribute(key = AttributeNames.QUERY, inherit = true)
    public String query = null;

    /**
     * Documents to cluster.
     */
    @Processing
    @Input
    @Required
    @Internal
    @Attribute(key = AttributeNames.DOCUMENTS, inherit = true)
    public List<Document> documents;

    @Processing
    @Output
    @Internal
    @Attribute(key = AttributeNames.CLUSTERS, inherit = true)
    public List<Cluster> clusters = null;

    /**
     * Balance between cluster score and size during cluster sorting. Value equal to 0.0
     * will cause Lingo to sort clusters based only on cluster size. Value equal to 1.0
     * will cause Lingo to sort clusters based only on cluster score.
     */
    @Input
    @Processing
    @Attribute
    @DoubleRange(min = 0.0, max = 1.0)
    @Label("Size-Score sorting ratio")
    @Level(AttributeLevel.MEDIUM)
    @Group(DefaultGroups.CLUSTERS)
    public double scoreWeight = 0.0;

    /**
     * Desired cluster count base. Base factor used to calculate the number of clusters
     * based on the number of documents on input. The larger the value, the more clusters
     * will be created. The number of clusters created by the algorithm will be
     * proportional to the cluster count base, but not in a linear way.
     */
    @Input
    @Processing
    @Attribute
    @IntRange(min = 2, max = 100)
    @Label("Cluster count base")
    @Level(AttributeLevel.BASIC)
    @Group(DefaultGroups.CLUSTERS)
    public int desiredClusterCountBase = 30;

    public LanguageModel languageModel = LanguageModels.english();

    public final CompletePreprocessingPipeline preprocessingPipeline = new CompletePreprocessingPipeline();

    /**
     * Term-document matrix builder for the algorithm, contains bindable attributes.
     */
    public final TermDocumentMatrixBuilder matrixBuilder = new TermDocumentMatrixBuilder();

    /**
     * Term-document matrix reducer for the algorithm, contains bindable attributes.
     */
    public final TermDocumentMatrixReducer matrixReducer = new TermDocumentMatrixReducer();

    /**
     * Cluster label builder, contains bindable attributes.
     */
    public final ClusterBuilder clusterBuilder = new ClusterBuilder();

    /**
     * Cluster label formatter, contains bindable attributes.
     */
    public final LabelFormatter labelFormatter = new LabelFormatter();

    /**
     * Performs Lingo clustering of {@link #documents}.
     */
    @Override
    public void process() throws ProcessingException
    {
        // Preprocessing of documents
        final PreprocessingContext context = preprocessingPipeline.preprocess(
            documents, query, languageModel);

        // Further processing only if there are words to process
        clusters = new ArrayList<>();
        if (context.hasLabels())
        {
            // Term-document matrix building and reduction
            final VectorSpaceModelContext vsmContext = new VectorSpaceModelContext(
                context);
            final ReducedVectorSpaceModelContext reducedVsmContext = new ReducedVectorSpaceModelContext(
                vsmContext);
            LingoProcessingContext lingoContext = new LingoProcessingContext(
                reducedVsmContext);

            matrixBuilder.buildTermDocumentMatrix(vsmContext);
            matrixBuilder.buildTermPhraseMatrix(vsmContext);

            matrixReducer.reduce(reducedVsmContext,
                computeClusterCount(desiredClusterCountBase, documents.size()));

            // Cluster label building
            clusterBuilder.buildLabels(lingoContext, matrixBuilder.termWeighting.get());

            // Document assignment
            clusterBuilder.assignDocuments(lingoContext);

            // Cluster merging
            clusterBuilder.merge(lingoContext);

            // Format final clusters
            final int [] clusterLabelIndex = lingoContext.clusterLabelFeatureIndex;
            final BitSet [] clusterDocuments = lingoContext.clusterDocuments;
            final double [] clusterLabelScore = lingoContext.clusterLabelScore;
            for (int i = 0; i < clusterLabelIndex.length; i++)
            {
                final Cluster cluster = new Cluster();

                final int labelFeature = clusterLabelIndex[i];
                if (labelFeature < 0)
                {
                    // Cluster removed during merging
                    continue;
                }

                // Add label and score
                cluster.addPhrases(labelFormatter.format(context, labelFeature));
                cluster.setAttribute(Cluster.SCORE, clusterLabelScore[i]);

                // Add documents
                final BitSet bs = clusterDocuments[i];
                for (int bit = bs.nextSetBit(0); bit >= 0; bit = bs.nextSetBit(bit + 1))
                {
                    cluster.addDocuments(documents.get(bit));
                }

                // Add cluster
                clusters.add(cluster);
            }

            Collections.sort(clusters,
                Cluster.byReversedWeightedScoreAndSizeComparator(scoreWeight));
        }

        Cluster.appendOtherTopics(documents, clusters);
    }

    /**
     * Computes the number of clusters to create based on a very simple heuristic based on
     * the number of documents on input.
     */
    static int computeClusterCount(int desiredClusterCountBase, int documentCount)
    {
        return Math.min(
            (int) ((desiredClusterCountBase / 10.0) * Math.sqrt(documentCount)),
            documentCount);
    }
}
