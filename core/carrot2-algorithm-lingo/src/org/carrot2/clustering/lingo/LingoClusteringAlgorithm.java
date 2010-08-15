
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

package org.carrot2.clustering.lingo;

import java.util.Collections;
import java.util.List;

import org.carrot2.core.*;
import org.carrot2.core.attribute.*;
import org.carrot2.matrix.NNIInterface;
import org.carrot2.text.clustering.IMonolingualClusteringAlgorithm;
import org.carrot2.text.clustering.MultilingualClustering;
import org.carrot2.text.clustering.MultilingualClustering.LanguageAggregationStrategy;
import org.carrot2.text.preprocessing.LabelFormatter;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.text.preprocessing.pipeline.CompletePreprocessingPipeline;
import org.carrot2.text.vsm.TermDocumentMatrixBuilder;
import org.carrot2.text.vsm.VectorSpaceModelContext;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.DoubleRange;
import org.slf4j.Logger;

import com.carrotsearch.hppc.BitSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

/**
 * Lingo clustering algorithm. Implementation as described in: <i>
 * Stanisław Osiński, Dawid Weiss: A Concept-Driven Algorithm for Clustering 
 * Search Results. IEEE Intelligent Systems, May/June, 3 (vol. 20), 2005, 
 * pp. 48—54.</i>.
 */
@Bindable(prefix = "LingoClusteringAlgorithm", inherit = AttributeNames.class)
public class LingoClusteringAlgorithm extends ProcessingComponentBase implements
    IClusteringAlgorithm
{
    private static final Logger log = org.slf4j.LoggerFactory
        .getLogger(LingoClusteringAlgorithm.class);

    /**
     * Report the warning about native libraries only once.
     */
    private static boolean nativeLibrariesReported;

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
     * Indicates whether Lingo used fast native matrix computation routines. Value of this
     * attribute is equal to {@link NNIInterface#isNativeBlasAvailable()} at the time of
     * running the algorithm.
     * 
     * @group Matrix model
     * @label Native matrix operations used
     */
    @Processing
    @Output
    @Attribute
    public boolean nativeMatrixUsed;

    /**
     * Balance between cluster score and size during cluster sorting. Value equal to 0.0
     * will cause Lingo to sort clusters based only on cluster size. Value equal to 1.0
     * will cause Lingo to sort clusters based only on cluster score.
     * 
     * @label Size-Score sorting ratio
     * @level Medium
     * @group Clusters
     */
    @Input
    @Processing
    @Attribute
    @DoubleRange(min = 0.0, max = 1.0)
    public double scoreWeight = 0.0;

    /**
     * Common preprocessing tasks handler.
     */
    public CompletePreprocessingPipeline preprocessingPipeline = new CompletePreprocessingPipeline();

    /**
     * Term-document matrix builder for the algorithm, contains bindable attributes.
     */
    public TermDocumentMatrixBuilder matrixBuilder = new TermDocumentMatrixBuilder();

    /**
     * Term-document matrix reducer for the algorithm, contains bindable attributes.
     */
    public TermDocumentMatrixReducer matrixReducer = new TermDocumentMatrixReducer();

    /**
     * Cluster label builder, contains bindable attributes.
     */
    public ClusterBuilder clusterBuilder = new ClusterBuilder();

    /**
     * Cluster label formatter, contains bindable attributes.
     */
    public LabelFormatter labelFormatter = new LabelFormatter();

    /**
     * A helper for performing multilingual clustering.
     */
    public MultilingualClustering multilingualClustering = new MultilingualClustering();

    @Override
    public void init(IControllerContext context)
    {
        synchronized (LingoClusteringAlgorithm.class)
        {
            if (!nativeLibrariesReported)
            {
                if (NNIInterface.isNativeBlasAvailable())
                {
                    log.info("Native BLAS routines available");
                }
                nativeLibrariesReported = true;
            }
        }
    }

    /**
     * Performs Lingo clustering of {@link #documents}.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void process() throws ProcessingException
    {
        nativeMatrixUsed = NNIInterface.isNativeBlasAvailable();

        // There is a tiny trick here to support multilingual clustering without
        // refactoring the whole component: we remember the original list of documents
        // and invoke clustering for each language separately within the
        // IMonolingualClusteringAlgorithm implementation below. This is safe because
        // processing components are not thread-safe by definition and
        // IMonolingualClusteringAlgorithm forbids concurrent execution by contract.
        final List<Document> originalDocuments = documents;
        clusters = multilingualClustering.process(documents,
            new IMonolingualClusteringAlgorithm()
            {
                public List<Cluster> process(List<Document> documents,
                    LanguageCode language)
                {
                    LingoClusteringAlgorithm.this.documents = documents;
                    LingoClusteringAlgorithm.this.cluster(language);
                    return LingoClusteringAlgorithm.this.clusters;
                }
            });
        documents = originalDocuments;

        if (multilingualClustering.languageAggregationStrategy == LanguageAggregationStrategy.FLATTEN_ALL)
        {
            Collections.sort(clusters, Ordering.compound(Lists.newArrayList(
                Cluster.OTHER_TOPICS_AT_THE_END, Cluster
                    .byReversedWeightedScoreAndSizeComparator(scoreWeight))));
        }
    }

    /**
     * Performs the actual clustering with an assumption that all documents are written in
     * one <code>language</code>.
     */
    private void cluster(LanguageCode language)
    {
        // Preprocessing of documents
        final PreprocessingContext context = preprocessingPipeline.preprocess(documents,
            query, language);

        // Further processing only if there are words to process
        clusters = Lists.newArrayList();
        if (context.hasLabels())
        {
            // Term-document matrix building and reduction
            final VectorSpaceModelContext vsmContext = new VectorSpaceModelContext(
                context);
            matrixBuilder.buildTermDocumentMatrix(vsmContext);
            matrixBuilder.buildTermPhraseMatrix(vsmContext);

            LingoProcessingContext lingoContext = new LingoProcessingContext(vsmContext);
            matrixReducer.reduce(lingoContext);

            // Cluster label building
            clusterBuilder.buildLabels(lingoContext, matrixBuilder.termWeighting);

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

            Collections.sort(clusters, Cluster
                .byReversedWeightedScoreAndSizeComparator(scoreWeight));
        }

        Cluster.appendOtherTopics(documents, clusters);
    }
}
