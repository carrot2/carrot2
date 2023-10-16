/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2023, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.clustering.kmeans;

import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntIntHashMap;
import com.carrotsearch.hppc.IntIntMap;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.cursors.IntIntCursor;
import com.carrotsearch.hppc.sorting.IndirectSort;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.carrot2.attrs.AttrBoolean;
import org.carrot2.attrs.AttrComposite;
import org.carrot2.attrs.AttrInteger;
import org.carrot2.attrs.AttrObject;
import org.carrot2.attrs.AttrString;
import org.carrot2.clustering.Cluster;
import org.carrot2.clustering.ClusteringAlgorithm;
import org.carrot2.clustering.Document;
import org.carrot2.clustering.SharedInfrastructure;
import org.carrot2.internal.clustering.ClusteringAlgorithmUtilities;
import org.carrot2.language.EphemeralDictionaries;
import org.carrot2.language.LabelFilter;
import org.carrot2.language.LanguageComponents;
import org.carrot2.language.Stemmer;
import org.carrot2.language.StopwordFilter;
import org.carrot2.language.Tokenizer;
import org.carrot2.math.mahout.function.Functions;
import org.carrot2.math.mahout.matrix.DoubleMatrix1D;
import org.carrot2.math.mahout.matrix.DoubleMatrix2D;
import org.carrot2.math.mahout.matrix.impl.DenseDoubleMatrix1D;
import org.carrot2.math.mahout.matrix.impl.DenseDoubleMatrix2D;
import org.carrot2.text.preprocessing.BasicPreprocessingPipeline;
import org.carrot2.text.preprocessing.LabelFormatter;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.text.vsm.ReducedVectorSpaceModelContext;
import org.carrot2.text.vsm.TermDocumentMatrixBuilder;
import org.carrot2.text.vsm.TermDocumentMatrixReducer;
import org.carrot2.text.vsm.VectorSpaceModelContext;

/**
 * A very simple implementation of bisecting k-means clustering. Unlike other algorithms in Carrot2,
 * this one creates hard clustering (one document belongs only to one cluster). On the other hand,
 * the clusters are labeled only with individual words that may not always fully correspond to all
 * documents in the cluster.
 */
public class BisectingKMeansClusteringAlgorithm extends AttrComposite
    implements ClusteringAlgorithm {
  private static final Set<Class<?>> REQUIRED_LANGUAGE_COMPONENTS =
      new HashSet<>(
          Arrays.asList(
              Stemmer.class,
              Tokenizer.class,
              StopwordFilter.class,
              LabelFilter.class,
              LabelFormatter.class));

  public static final String NAME = "Bisecting K-Means";

  /**
   * Number of clusters to create. The algorithm will create at most the specified number of
   * clusters.
   */
  public final AttrInteger clusterCount =
      attributes.register(
          "clusterCount", AttrInteger.builder().label("Cluster count").min(2).defaultValue(25));

  /** Maximum number of k-means iterations to perform. */
  public final AttrInteger maxIterations =
      attributes.register(
          "maxIterations",
          AttrInteger.builder().label("Maximum iterations").min(1).defaultValue(15));

  /** Number of partitions to create at each k-means clustering iteration. */
  public final AttrInteger partitionCount =
      attributes.register(
          "partitionCount",
          AttrInteger.builder().label("Partition count").min(2).max(10).defaultValue(2));

  /** Minimum number of labels to return for each cluster. */
  public final AttrInteger labelCount =
      attributes.register(
          "labelCount", AttrInteger.builder().label("Label count").min(1).max(10).defaultValue(3));

  /**
   * Query terms used to retrieve documents. The query is used as a hint to avoid trivial clusters.
   */
  public final AttrString queryHint =
      attributes.register("queryHint", SharedInfrastructure.queryHintAttribute());

  /**
   * If enabled, k-means will be applied on the dimensionality-reduced term-document matrix. The
   * number of dimensions will be equal to twice the number of requested clusters. If the number of
   * dimensions is lower than the number of input documents, reduction will not be performed. If
   * disabled, the k-means will be performed directly on the original term-document matrix.
   */
  public final AttrBoolean useDimensionalityReduction =
      attributes.register(
          "useDimensionalityReduction",
          AttrBoolean.builder().label("Use dimensionality reduction").defaultValue(true));

  /** Configuration of the size and contents of the term-document matrix. */
  public TermDocumentMatrixBuilder matrixBuilder;

  {
    attributes.register(
        "matrixBuilder",
        AttrObject.builder(TermDocumentMatrixBuilder.class)
            .label("Term-document matrix builder")
            .getset(() -> matrixBuilder, (v) -> matrixBuilder = v)
            .defaultValue(TermDocumentMatrixBuilder::new));
  }

  /** Configuration of the matrix decomposition method to use for clustering. */
  public TermDocumentMatrixReducer matrixReducer;

  {
    attributes.register(
        "matrixReducer",
        AttrObject.builder(TermDocumentMatrixReducer.class)
            .label("Term-document matrix reducer")
            .getset(() -> matrixReducer, (v) -> matrixReducer = v)
            .defaultValue(TermDocumentMatrixReducer::new));
  }

  /** Configuration of the text preprocessing stage. */
  public BasicPreprocessingPipeline preprocessing;

  {
    attributes.register(
        "preprocessing",
        AttrObject.builder(BasicPreprocessingPipeline.class)
            .label("Input preprocessing components")
            .getset(() -> preprocessing, (v) -> preprocessing = v)
            .defaultValue(BasicPreprocessingPipeline::new));
  }

  /**
   * Per-request overrides of language components (dictionaries).
   *
   * @since 4.1.0
   */
  public EphemeralDictionaries dictionaries;

  {
    ClusteringAlgorithmUtilities.registerDictionaries(
        attributes, () -> dictionaries, (v) -> dictionaries = v);
  }

  @Override
  public Set<Class<?>> requiredLanguageComponents() {
    return REQUIRED_LANGUAGE_COMPONENTS;
  }

  @Override
  public <T extends Document> List<Cluster<T>> cluster(
      Stream<? extends T> docStream, LanguageComponents languageComponents) {
    List<T> documents = docStream.collect(Collectors.toList());

    // Apply ephemeral dictionaries.
    if (this.dictionaries != null) {
      languageComponents = this.dictionaries.override(languageComponents);
    }

    // Preprocessing of documents
    final PreprocessingContext preprocessingContext =
        preprocessing.preprocess(documents.stream(), queryHint.get(), languageComponents);

    // Add trivial AllLabels so that we can reuse the common TD matrix builder
    final int[] stemsMfow = preprocessingContext.allStems.mostFrequentOriginalWordIndex;
    final short[] wordsType = preprocessingContext.allWords.type;
    final IntArrayList featureIndices = new IntArrayList(stemsMfow.length);
    for (int i = 0; i < stemsMfow.length; i++) {
      final short flag = wordsType[stemsMfow[i]];
      if ((flag & (Tokenizer.TF_COMMON_WORD | Tokenizer.TF_QUERY_WORD | Tokenizer.TT_NUMERIC))
          == 0) {
        featureIndices.add(stemsMfow[i]);
      }
    }
    preprocessingContext.allLabels.featureIndex = featureIndices.toArray();
    preprocessingContext.allLabels.firstPhraseIndex = -1;

    // Further processing only if there are words to process
    ArrayList<Cluster<T>> clusters = new ArrayList<>();
    if (preprocessingContext.hasLabels()) {
      // Term-document matrix building and reduction
      final VectorSpaceModelContext vsmContext = new VectorSpaceModelContext(preprocessingContext);
      final ReducedVectorSpaceModelContext reducedVsmContext =
          new ReducedVectorSpaceModelContext(vsmContext);

      matrixBuilder.buildTermDocumentMatrix(vsmContext);
      matrixBuilder.buildTermPhraseMatrix(vsmContext);

      // Prepare rowIndex -> stemIndex mapping for labeling
      final IntIntHashMap rowToStemIndex = new IntIntHashMap();
      for (IntIntCursor c : vsmContext.stemToRowIndex) {
        rowToStemIndex.put(c.value, c.key);
      }

      final DoubleMatrix2D tdMatrix;
      if (useDimensionalityReduction.get()
          && clusterCount.get() * 2 < preprocessingContext.documentCount) {
        matrixReducer.reduce(reducedVsmContext, clusterCount.get() * 2);
        tdMatrix = reducedVsmContext.coefficientMatrix.viewDice();
      } else {
        tdMatrix = vsmContext.termDocumentMatrix;
      }

      // Initial selection containing all columns, initial clustering
      final IntArrayList columns = new IntArrayList(tdMatrix.columns());
      for (int c = 0; c < tdMatrix.columns(); c++) {
        columns.add(c);
      }
      final List<IntArrayList> rawClusters = new ArrayList<>();
      rawClusters.addAll(split(partitionCount.get(), tdMatrix, columns, maxIterations.get()));
      Collections.sort(rawClusters, BY_SIZE_DESCENDING);

      int largestIndex = 0;
      while (rawClusters.size() < clusterCount.get() && largestIndex < rawClusters.size()) {
        // Find largest cluster to split
        IntArrayList largest = rawClusters.get(largestIndex);
        if (largest.size() <= partitionCount.get() * 2) {
          // No cluster is large enough to produce a meaningful
          // split (i.e. a split into subclusters with more than
          // 1 member).
          break;
        }

        final List<IntArrayList> split =
            split(partitionCount.get(), tdMatrix, largest, maxIterations.get());
        if (split.size() > 1) {
          rawClusters.remove(largestIndex);
          rawClusters.addAll(split);
          Collections.sort(rawClusters, BY_SIZE_DESCENDING);
          largestIndex = 0;
        } else {
          largestIndex++;
        }
      }

      LabelFormatter labelFormatter = languageComponents.get(LabelFormatter.class);
      for (IntArrayList rawCluster : rawClusters) {
        final Cluster<T> cluster = new Cluster<>();
        if (rawCluster.size() > 1) {
          getLabels(
              cluster,
              rawCluster,
              vsmContext.termDocumentMatrix,
              rowToStemIndex,
              preprocessingContext.allStems.mostFrequentOriginalWordIndex,
              preprocessingContext.allWords.image,
              labelFormatter);
          for (int j = 0; j < rawCluster.size(); j++) {
            cluster.addDocument(documents.get(rawCluster.get(j)));
          }
          clusters.add(cluster);
        }
      }
    }

    return SharedInfrastructure.reorderByDescendingSizeAndLabel(clusters);
  }

  private static final Comparator<IntArrayList> BY_SIZE_DESCENDING =
      (o1, o2) -> o2.size() - o1.size();

  private void getLabels(
      Cluster<?> cluster,
      IntArrayList documents,
      DoubleMatrix2D termDocumentMatrix,
      IntIntHashMap rowToStemIndex,
      int[] mostFrequentOriginalWordIndex,
      char[][] wordImage,
      LabelFormatter labelFormatter) {
    // Prepare a centroid. If dimensionality reduction was used,
    // the centroid from k-means will not be based on real terms,
    // so we need to calculate the centroid here once again based
    // on the cluster's documents.
    final DoubleMatrix1D centroid = new DenseDoubleMatrix1D(termDocumentMatrix.rows());
    for (IntCursor d : documents) {
      centroid.assign(termDocumentMatrix.viewColumn(d.value), Functions.PLUS);
    }

    final int[] order =
        IndirectSort.mergesort(
            0, centroid.size(), (a, b) -> Double.compare(centroid.get(a), centroid.get(b)));

    final double minValueForLabel =
        centroid.get(order[order.length - Math.min(labelCount.get(), order.length)]);

    for (int i = 0; i < centroid.size(); i++) {
      if (centroid.getQuick(i) >= minValueForLabel) {
        cluster.addLabel(
            labelFormatter.format(
                new char[][] {wordImage[mostFrequentOriginalWordIndex[rowToStemIndex.get(i)]]},
                new boolean[] {false}));
      }
    }
  }

  /**
   * Splits the input documents into the specified number of partitions using the standard k-means
   * routine.
   */
  private List<IntArrayList> split(
      int partitions, DoubleMatrix2D input, IntArrayList columns, int iterations) {
    // Prepare selected matrix
    final DoubleMatrix2D selected = input.viewSelection(null, columns.toArray()).copy();
    final IntIntMap selectedToInput = new IntIntHashMap(selected.columns());
    for (int i = 0; i < columns.size(); i++) {
      selectedToInput.put(i, columns.get(i));
    }

    // Prepare results holders
    List<IntArrayList> result = new ArrayList<>();
    List<IntArrayList> previousResult = null;
    for (int i = 0; i < partitions; i++) {
      result.add(new IntArrayList(selected.columns()));
    }
    for (int i = 0; i < selected.columns(); i++) {
      result.get(i % partitions).add(i);
    }

    // Matrices for centroids and document-centroid similarities
    final DoubleMatrix2D centroids =
        new DenseDoubleMatrix2D(selected.rows(), partitions)
            .assign(selected.viewPart(0, 0, selected.rows(), partitions));
    final DoubleMatrix2D similarities = new DenseDoubleMatrix2D(partitions, selected.columns());

    // Run a fixed number of K-means iterations
    for (int it = 0; it < iterations; it++) {
      // Update centroids
      for (int i = 0; i < result.size(); i++) {
        final IntArrayList cluster = result.get(i);
        for (int k = 0; k < selected.rows(); k++) {
          double sum = 0;
          for (int j = 0; j < cluster.size(); j++) {
            sum += selected.get(k, cluster.get(j));
          }
          centroids.setQuick(k, i, sum / cluster.size());
        }
      }

      previousResult = result;
      result = new ArrayList<>();
      for (int i = 0; i < partitions; i++) {
        result.add(new IntArrayList(selected.columns()));
      }

      // Calculate similarity to centroids
      centroids.zMult(selected, similarities, 1, 0, true, false);

      // Assign documents to the nearest centroid
      for (int c = 0; c < similarities.columns(); c++) {
        int maxRow = 0;
        double max = similarities.get(0, c);
        for (int r = 1; r < similarities.rows(); r++) {
          if (max < similarities.get(r, c)) {
            max = similarities.get(r, c);
            maxRow = r;
          }
        }

        result.get(maxRow).add(c);
      }

      if (Objects.equals(previousResult, result)) {
        // Unchanged result
        break;
      }
    }

    // Map the results back to the global indices
    for (Iterator<IntArrayList> it = result.iterator(); it.hasNext(); ) {
      final IntArrayList cluster = it.next();
      if (cluster.isEmpty()) {
        it.remove();
      } else {
        for (int j = 0; j < cluster.size(); j++) {
          cluster.set(j, selectedToInput.get(cluster.get(j)));
        }
      }
    }

    return result;
  }
}
