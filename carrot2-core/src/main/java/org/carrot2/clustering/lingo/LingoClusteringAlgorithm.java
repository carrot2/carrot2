
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
import org.carrot2.clustering.Cluster;
import org.carrot2.clustering.ClusteringAlgorithm;
import org.carrot2.clustering.Document;
import org.carrot2.language.LanguageComponents;
import org.carrot2.text.preprocessing.CompletePreprocessingPipeline;
import org.carrot2.text.preprocessing.LabelFormatter;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.text.vsm.ReducedVectorSpaceModelContext;
import org.carrot2.text.vsm.TermDocumentMatrixBuilder;
import org.carrot2.text.vsm.TermDocumentMatrixReducer;
import org.carrot2.text.vsm.VectorSpaceModelContext;
import org.carrot2.util.attrs.AttrComposite;
import org.carrot2.util.attrs.AttrDouble;
import org.carrot2.util.attrs.AttrInteger;
import org.carrot2.util.attrs.AttrObject;
import org.carrot2.util.attrs.AttrString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Lingo clustering algorithm. Implementation as described in: <i> "Stanisław Osiński,
 * Dawid Weiss: A Concept-Driven Algorithm for Clustering Search Results. IEEE Intelligent
 * Systems, May/June, 3 (vol. 20), 2005, pp. 48—54."</i>.
 */
public class LingoClusteringAlgorithm extends AttrComposite implements ClusteringAlgorithm {
    /**
     * Balance between cluster score and size during cluster sorting. Value equal to 0.0
     * will cause Lingo to sort clusters based only on cluster size. Value equal to 1.0
     * will cause Lingo to sort clusters based only on cluster score.
     */
    public AttrDouble scoreWeight = attributes.register("scoreWeight",
        AttrDouble.builder()
            .label("Size-score sorting ratio")
            .min(0)
            .max(1)
            .defaultValue(0)
            .build());

    /**
     * Desired cluster count base. Base factor used to calculate the number of clusters
     * based on the number of documents on input. The larger the value, the more clusters
     * will be created. The number of clusters created by the algorithm will be
     * proportional to the cluster count base, but not in a linear way.
     */
    public AttrInteger desiredClusterCountBase = attributes.register("desiredClusterCountBase",
        AttrInteger.builder()
            .label("Cluster count base")
            .min(2)
            .max(100)
            .defaultValue(30)
            .build());

    /**
     * Preprocessing pipeline.
     */
    public final AttrObject<CompletePreprocessingPipeline> preprocessing =
        attributes.register("preprocessing", AttrObject.builder(CompletePreprocessingPipeline.class)
            .defaultValue(new CompletePreprocessingPipeline())
            .build());

    /**
     * Term-document matrix builder for the algorithm.
     */
    public final AttrObject<TermDocumentMatrixBuilder> matrixBuilder =
        attributes.register("matrixBuilder", AttrObject.builder(TermDocumentMatrixBuilder.class)
            .defaultValue(new TermDocumentMatrixBuilder())
            .build());

    /**
     * Term-document matrix reducer for the algorithm.
     */
    public final AttrObject<TermDocumentMatrixReducer> matrixReducer =
        attributes.register("matrixReducer", AttrObject.builder(TermDocumentMatrixReducer.class)
            .defaultValue(new TermDocumentMatrixReducer())
            .build());

    /**
     * Cluster label builder, contains bindable attributes.
     */
    public final AttrObject<ClusterBuilder> clusterBuilder =
        attributes.register("clusterBuilder", AttrObject.builder(ClusterBuilder.class)
            .defaultValue(new ClusterBuilder())
            .build());

    /**
     *
     */
    public final AttrString queryHint = attributes.register(
        "queryHint", AttrString.builder()
            .label("Query hint")
            .build());

    /**
     * Cluster label formatter, contains bindable attributes.
     */
    public final LabelFormatter labelFormatter = new LabelFormatter();

    /**
     * Performs Lingo clustering of documents.
     */
    @Override
    public <T extends Document> List<Cluster<T>> cluster(Stream<? extends T> docStream, LanguageComponents languageComponents) {
        List<T> documents = docStream.collect(Collectors.toList());

        // Preprocessing of documents
        final PreprocessingContext context =
            preprocessing.get().preprocess(documents.stream(), queryHint.get(), languageComponents);

        // Further processing only if there are words to process
        List<Cluster<T>> clusters = new ArrayList<>();
        if (context.hasLabels())
        {
            // Term-document matrix building and reduction
            final VectorSpaceModelContext vsmContext = new VectorSpaceModelContext(
                context);
            final ReducedVectorSpaceModelContext reducedVsmContext = new ReducedVectorSpaceModelContext(
                vsmContext);
            LingoProcessingContext lingoContext = new LingoProcessingContext(
                reducedVsmContext);

            TermDocumentMatrixBuilder matrixBuilder = this.matrixBuilder.get();
            matrixBuilder.buildTermDocumentMatrix(vsmContext);
            matrixBuilder.buildTermPhraseMatrix(vsmContext);

            matrixReducer.get().reduce(reducedVsmContext,
                computeClusterCount(desiredClusterCountBase.get(), documents.size()));

            // Cluster label building
            ClusterBuilder clusterBuilder = this.clusterBuilder.get();
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
                final Cluster<T> cluster = new Cluster<>();

                final int labelFeature = clusterLabelIndex[i];
                if (labelFeature < 0)
                {
                    // Cluster removed during merging
                    continue;
                }

                // Add label and score
                cluster.addLabel(labelFormatter.format(context, labelFeature));
                cluster.setScore(clusterLabelScore[i]);

                // Add documents
                final BitSet bs = clusterDocuments[i];
                for (int bit = bs.nextSetBit(0); bit >= 0; bit = bs.nextSetBit(bit + 1))
                {
                    cluster.addDocument(documents.get(bit));
                }

                // Add cluster
                clusters.add(cluster);
            }

            // TODO: sort.
            // Collections.sort(clusters, Cluster.byReversedWeightedScoreAndSizeComparator(scoreWeight));
        }
        return clusters;
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
