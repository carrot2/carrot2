package org.carrot2.clustering.lingo;

import com.carrotsearch.hppc.BitSet;
import org.carrot2.core.Cluster;
import org.carrot2.core.Document;
import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.text.linguistic.ILexicalData;
import org.carrot2.text.linguistic.IStemmer;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LingoClusteringAlgorithm2 {
  public final CompletePreprocessingPipeline preprocessingPipeline = new CompletePreprocessingPipeline();

  public final ILexicalData lexicalData;
  public final ITokenizer tokenizer;
  public final IStemmer stemmer;

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
   * Balance between cluster score and size during cluster sorting. Value equal to 0.0
   * will cause Lingo to sort clusters based only on cluster size. Value equal to 1.0
   * will cause Lingo to sort clusters based only on cluster score.
   */
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
  @Attribute
  @IntRange(min = 2, max = 100)
  @Label("Cluster count base")
  @Level(AttributeLevel.BASIC)
  @Group(DefaultGroups.CLUSTERS)
  public int desiredClusterCountBase = 30;

  public LingoClusteringAlgorithm2(ITokenizer tokenizer, IStemmer stemmer, ILexicalData lexicalData) {
    this.tokenizer = tokenizer;
    this.stemmer = stemmer;
    this.lexicalData = lexicalData;
  }

  public List<Cluster> cluster(Stream<Document> documents) {
    // TODO: not cache the list?
    List<Document> allDocuments = documents.collect(Collectors.toList());

    // Preprocessing of documents
    final PreprocessingContext context = preprocessingPipeline.preprocess(
        allDocuments.stream(),
        tokenizer,
        stemmer,
        lexicalData);

    // Further processing only if there are words to process
    ArrayList<Cluster> clusters = new ArrayList<>();
    if (context.hasLabels())
    {
      // Term-document matrix building and reduction
      final VectorSpaceModelContext vsmContext = new VectorSpaceModelContext();
      final ReducedVectorSpaceModelContext reducedVsmContext = new ReducedVectorSpaceModelContext(vsmContext);
      LingoProcessingContext lingoContext = new LingoProcessingContext(reducedVsmContext);

      matrixBuilder.buildTermDocumentMatrix(vsmContext, context);
      matrixBuilder.buildTermPhraseMatrix(vsmContext, context);

      matrixReducer.reduce(reducedVsmContext,
          computeClusterCount(desiredClusterCountBase, context.documents));

      // Cluster label building
      clusterBuilder.buildLabels(lingoContext, context, matrixBuilder.termWeighting);

      // Document assignment
      clusterBuilder.assignDocuments(lingoContext, context);

      // Cluster merging
      clusterBuilder.merge(lingoContext);

      // Format final clusters
      final int [] clusterLabelIndex = lingoContext.clusterLabelFeatureIndex;
      final BitSet[] clusterDocuments = lingoContext.clusterDocuments;
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
        boolean insertSpace = true; // TODO: language-dependent formatter should be aquired from outside?
        cluster.addPhrases(labelFormatter.format(context, labelFeature, insertSpace));
        cluster.setAttribute(Cluster.SCORE, clusterLabelScore[i]);

        // Add documents
        final BitSet bs = clusterDocuments[i];
        for (int bit = bs.nextSetBit(0); bit >= 0; bit = bs.nextSetBit(bit + 1))
        {
          cluster.addDocuments(allDocuments.get(bit));
        }

        // Add cluster
        clusters.add(cluster);
      }

      Collections.sort(clusters,
          Cluster.byReversedWeightedScoreAndSizeComparator(scoreWeight));
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
