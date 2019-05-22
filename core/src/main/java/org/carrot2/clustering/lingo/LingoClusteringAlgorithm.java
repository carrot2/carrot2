/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.clustering.lingo;

import com.carrotsearch.hppc.BitSet;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.carrot2.attrs.AttrComposite;
import org.carrot2.attrs.AttrDouble;
import org.carrot2.attrs.AttrInteger;
import org.carrot2.attrs.AttrObject;
import org.carrot2.attrs.AttrString;
import org.carrot2.clustering.Cluster;
import org.carrot2.clustering.ClusteringAlgorithm;
import org.carrot2.clustering.Document;
import org.carrot2.clustering.SharedInfrastructure;
import org.carrot2.language.LanguageComponents;
import org.carrot2.language.LexicalData;
import org.carrot2.language.Stemmer;
import org.carrot2.language.Tokenizer;
import org.carrot2.text.preprocessing.CompletePreprocessingPipeline;
import org.carrot2.text.preprocessing.LabelFormatter;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.text.vsm.ReducedVectorSpaceModelContext;
import org.carrot2.text.vsm.TermDocumentMatrixBuilder;
import org.carrot2.text.vsm.TermDocumentMatrixReducer;
import org.carrot2.text.vsm.VectorSpaceModelContext;

/**
 * Lingo clustering algorithm. Implementation as described in: <i> "Stanisław Osiński, Dawid Weiss:
 * A Concept-Driven Algorithm for Clustering Search Results. IEEE Intelligent Systems, May/June, 3
 * (vol. 20), 2005, pp. 48—54."</i>.
 */
public class LingoClusteringAlgorithm extends AttrComposite implements ClusteringAlgorithm {
  private static final Set<Class<?>> REQUIRED_LANGUAGE_COMPONENTS =
      new HashSet<>(
          Arrays.asList(Stemmer.class, Tokenizer.class, LexicalData.class, LabelFormatter.class));

  /**
   * Balance between cluster score and size during cluster sorting. Value equal to 0.0 will cause
   * Lingo to sort clusters based only on cluster size. Value equal to 1.0 will cause Lingo to sort
   * clusters based only on cluster score.
   */
  public AttrDouble scoreWeight =
      attributes.register(
          "scoreWeight",
          AttrDouble.builder().label("Size-score sorting ratio").min(0).max(1).defaultValue(0.));

  /**
   * Desired cluster count base. Base factor used to calculate the number of clusters based on the
   * number of documents on input. The larger the value, the more clusters will be created. The
   * number of clusters created by the algorithm will be proportionally adjusted to the cluster
   * count base, but may be different.
   */
  public AttrInteger desiredClusterCount =
      attributes.register(
          "desiredClusterCount",
          AttrInteger.builder().label("Cluster count base").min(2).max(100).defaultValue(30));

  /** Preprocessing pipeline. */
  public CompletePreprocessingPipeline preprocessing;

  {
    attributes.register(
        "preprocessing",
        AttrObject.builder(CompletePreprocessingPipeline.class)
            .label("Input preprocessing components")
            .getset(() -> preprocessing, (v) -> preprocessing = v)
            .defaultValue(CompletePreprocessingPipeline::new));
  }

  /** Term-document matrix builder for the algorithm. */
  public TermDocumentMatrixBuilder matrixBuilder;

  {
    attributes.register(
        "matrixBuilder",
        AttrObject.builder(TermDocumentMatrixBuilder.class)
            .label("Term-document matrix builder")
            .getset(() -> matrixBuilder, (v) -> matrixBuilder = v)
            .defaultValue(TermDocumentMatrixBuilder::new));
  }

  /** Term-document matrix reducer for the algorithm. */
  public TermDocumentMatrixReducer matrixReducer;

  {
    attributes.register(
        "matrixReducer",
        AttrObject.builder(TermDocumentMatrixReducer.class)
            .label("Term-document matrix reducer")
            .getset(() -> matrixReducer, (v) -> matrixReducer = v)
            .defaultValue(TermDocumentMatrixReducer::new));
  }

  /** Cluster label builder, contains bindable attributes. */
  public ClusterBuilder clusterBuilder;

  {
    attributes.register(
        "clusterBuilder",
        AttrObject.builder(ClusterBuilder.class)
            .label("Cluster label supplier")
            .getset(() -> clusterBuilder, (v) -> clusterBuilder = v)
            .defaultValue(ClusterBuilder::new));
  }

  /**
   * Query terms used to retrieve documents. The query is used as a hint to avoid trivial clusters.
   */
  public final AttrString queryHint =
      attributes.register("queryHint", SharedInfrastructure.queryHintAttribute());

  @Override
  public boolean supports(LanguageComponents languageComponents) {
    return languageComponents.components().containsAll(REQUIRED_LANGUAGE_COMPONENTS);
  }

  /** Performs Lingo clustering of documents. */
  @Override
  public <T extends Document> List<Cluster<T>> cluster(
      Stream<? extends T> docStream, LanguageComponents languageComponents) {
    List<T> documents = docStream.collect(Collectors.toList());

    // Preprocessing of documents
    final PreprocessingContext context =
        preprocessing.preprocess(documents.stream(), queryHint.get(), languageComponents);

    // Further processing only if there are words to process
    List<Cluster<T>> clusters = new ArrayList<>();
    if (context.hasLabels()) {
      // Term-document matrix building and reduction
      final VectorSpaceModelContext vsmContext = new VectorSpaceModelContext(context);
      final ReducedVectorSpaceModelContext reducedVsmContext =
          new ReducedVectorSpaceModelContext(vsmContext);
      LingoProcessingContext lingoContext = new LingoProcessingContext(reducedVsmContext);

      TermDocumentMatrixBuilder matrixBuilder = this.matrixBuilder;
      matrixBuilder.buildTermDocumentMatrix(vsmContext);
      matrixBuilder.buildTermPhraseMatrix(vsmContext);

      matrixReducer.reduce(
          reducedVsmContext, computeClusterCount(desiredClusterCount.get(), documents.size()));

      // Cluster label building
      clusterBuilder.buildLabels(lingoContext, matrixBuilder.termWeighting);

      // Document assignment
      clusterBuilder.assignDocuments(lingoContext);

      // Cluster merging
      clusterBuilder.merge(lingoContext);

      // Format final clusters
      final LabelFormatter labelFormatter =
          lingoContext.preprocessingContext.languageComponents.get(LabelFormatter.class);

      final int[] clusterLabelIndex = lingoContext.clusterLabelFeatureIndex;
      final BitSet[] clusterDocuments = lingoContext.clusterDocuments;
      final double[] clusterLabelScore = lingoContext.clusterLabelScore;
      for (int i = 0; i < clusterLabelIndex.length; i++) {
        final Cluster<T> cluster = new Cluster<>();

        final int labelFeature = clusterLabelIndex[i];
        if (labelFeature < 0) {
          // Cluster removed during merging
          continue;
        }

        // Add label and score
        cluster.addLabel(context.format(labelFormatter, labelFeature));
        cluster.setScore(clusterLabelScore[i]);

        // Add documents
        final BitSet bs = clusterDocuments[i];
        for (int bit = bs.nextSetBit(0); bit >= 0; bit = bs.nextSetBit(bit + 1)) {
          cluster.addDocument(documents.get(bit));
        }

        // Add cluster
        clusters.add(cluster);
      }
    }

    clusters = SharedInfrastructure.reorderByWeightedScoreAndSize(clusters, this.scoreWeight.get());
    return clusters;
  }

  /**
   * Computes the number of clusters to create based on a very simple heuristic based on the number
   * of documents on input.
   */
  static int computeClusterCount(int desiredClusterCountBase, int documentCount) {
    return Math.min(
        (int) ((desiredClusterCountBase / 10.0) * Math.sqrt(documentCount)), documentCount);
  }
}
