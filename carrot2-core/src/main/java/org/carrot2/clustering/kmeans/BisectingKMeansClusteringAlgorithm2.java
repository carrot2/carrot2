
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

package org.carrot2.clustering.kmeans;

import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntIntHashMap;
import com.carrotsearch.hppc.IntIntMap;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.cursors.IntIntCursor;
import com.carrotsearch.hppc.sorting.IndirectComparator;
import com.carrotsearch.hppc.sorting.IndirectSort;
import org.carrot2.core.Cluster;
import org.carrot2.core.Document;
import org.carrot2.core.ProcessingException;
import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Internal;
import org.carrot2.mahout.math.function.Functions;
import org.carrot2.mahout.math.matrix.DoubleMatrix1D;
import org.carrot2.mahout.math.matrix.DoubleMatrix2D;
import org.carrot2.mahout.math.matrix.impl.DenseDoubleMatrix1D;
import org.carrot2.mahout.math.matrix.impl.DenseDoubleMatrix2D;
import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.text.linguistic.LanguageModel;
import org.carrot2.text.preprocessing.LabelFormatter;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.text.preprocessing.pipeline.BasicPreprocessingPipeline;
import org.carrot2.text.preprocessing.pipeline.IPreprocessingPipeline;
import org.carrot2.text.vsm.ReducedVectorSpaceModelContext;
import org.carrot2.text.vsm.TermDocumentMatrixBuilder;
import org.carrot2.text.vsm.TermDocumentMatrixReducer;
import org.carrot2.text.vsm.VectorSpaceModelContext;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.AttributeLevel;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Level;
import org.carrot2.util.attribute.constraint.ImplementingClasses;
import org.carrot2.util.attrs.*;

import java.util.*;

/**
 * A very simple implementation of bisecting k-means clustering. Unlike other algorithms
 * in Carrot2, this one creates hard clustering (one document belongs only to one
 * cluster). On the other hand, the clusters are labeled only with individual words that
 * may not always fully correspond to all documents in the cluster.
 */
public class BisectingKMeansClusteringAlgorithm2 implements AcceptingVisitor {
  private final AttrGroup group = new AttrGroup();

  /**
   * The number of clusters to create. The algorithm will create at most the specified
   * number of clusters.
   */
  public final AttrInteger clusterCount = group.register(
      "clusterCount", AttrInteger.builder()
          .label("Cluster count")
          .min(2)
          .defaultValue(25)
          .build());

  /**
   * The maximum number of k-means iterations to perform.
   */
  public final AttrInteger maxIterations = group.register(
      "maxIterations", AttrInteger.builder()
          .label("Maximum iterations")
          .min(1)
          .defaultValue(15)
          .build());

  /**
   * Partition count. The number of partitions to create at each k-means clustering
   * iteration.
   */
  public final AttrInteger partitionCount = group.register(
      "partitionCount", AttrInteger.builder()
          .label("Partition count")
          .min(2)
          .max(10)
          .defaultValue(2)
          .build());

  /**
   * Label count. The minimum number of labels to return for each cluster.
   */
  public final AttrInteger labelCount = group.register(
      "labelCount", AttrInteger.builder()
          .label("Label count")
          .min(1)
          .max(10)
          .defaultValue(3)
          .build());

  /**
   * Use dimensionality reduction. If <code>true</code>, k-means will be applied on the
   * dimensionality-reduced term-document matrix with the number of dimensions being
   * equal to twice the number of requested clusters. If the number of dimensions is
   * lower than the number of input documents, reduction will not be performed.
   * If <code>false</code>, the k-means will
   * be performed directly on the original term-document matrix.
   */
  public final AttrBoolean useDimensionalityReduction = group.register(
      "useDimensionalityReduction", AttrBoolean.builder()
          .label("Use dimensionality reduction")
          .defaultValue(true)
          .build());

  /**
   * Term-document matrix builder for the algorithm, contains bindable attributes.
   */
  public final AttrObject<TermDocumentMatrixBuilder> matrixBuilder =
      group.register("matrixBuilder", AttrObject.builder(TermDocumentMatrixBuilder.class)
          .defaultValue(new TermDocumentMatrixBuilder())
          .build());

  /**
   * Term-document matrix reducer for the algorithm, contains bindable attributes.
   */
  public final TermDocumentMatrixReducer matrixReducer = new TermDocumentMatrixReducer();

  /**
   * Common preprocessing tasks handler.
   */
  @Init
  @Input
  @Attribute
  @Internal
  @ImplementingClasses(classes = {}, strict = false)
  @Level(AttributeLevel.ADVANCED)
  public IPreprocessingPipeline preprocessingPipeline = new BasicPreprocessingPipeline();

  /**
   *
   */
  public void process(List<Document> documents, LanguageModel languageModel) throws ProcessingException {
    // Preprocessing of documents
    final PreprocessingContext preprocessingContext =
        preprocessingPipeline.preprocess(documents, null, languageModel);

    // Add trivial AllLabels so that we can reuse the common TD matrix builder
    final int[] stemsMfow = preprocessingContext.allStems.mostFrequentOriginalWordIndex;
    final short[] wordsType = preprocessingContext.allWords.type;
    final IntArrayList featureIndices = new IntArrayList(stemsMfow.length);
    for (int i = 0; i < stemsMfow.length; i++) {
      final short flag = wordsType[stemsMfow[i]];
      if ((flag & (ITokenizer.TF_COMMON_WORD | ITokenizer.TF_QUERY_WORD | ITokenizer.TT_NUMERIC)) == 0) {
        featureIndices.add(stemsMfow[i]);
      }
    }
    preprocessingContext.allLabels.featureIndex = featureIndices.toArray();
    preprocessingContext.allLabels.firstPhraseIndex = -1;

    // Further processing only if there are words to process
    ArrayList<Cluster> clusters = new ArrayList<>();
    if (preprocessingContext.hasLabels()) {
      // Term-document matrix building and reduction
      final VectorSpaceModelContext vsmContext = new VectorSpaceModelContext(
          preprocessingContext);
      final ReducedVectorSpaceModelContext reducedVsmContext = new ReducedVectorSpaceModelContext(
          vsmContext);

      TermDocumentMatrixBuilder matrixBuilder = this.matrixBuilder.get();
      matrixBuilder.buildTermDocumentMatrix(vsmContext);
      matrixBuilder.buildTermPhraseMatrix(vsmContext);

      // Prepare rowIndex -> stemIndex mapping for labeling
      final IntIntHashMap rowToStemIndex = new IntIntHashMap();
      for (IntIntCursor c : vsmContext.stemToRowIndex) {
        rowToStemIndex.put(c.value, c.key);
      }

      final DoubleMatrix2D tdMatrix;
      if (useDimensionalityReduction.get() && clusterCount.get() * 2 < preprocessingContext.documentCount) {
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

        final List<IntArrayList> split = split(partitionCount.get(), tdMatrix, largest, maxIterations.get());
        if (split.size() > 1) {
          rawClusters.remove(largestIndex);
          rawClusters.addAll(split);
          Collections.sort(rawClusters, BY_SIZE_DESCENDING);
          largestIndex = 0;
        } else {
          largestIndex++;
        }
      }

      for (int i = 0; i < rawClusters.size(); i++) {
        final Cluster cluster = new Cluster();

        final IntArrayList rawCluster = rawClusters.get(i);
        if (rawCluster.size() > 1) {
          cluster.addPhrases(getLabels(rawCluster,
              vsmContext.termDocumentMatrix, rowToStemIndex,
              preprocessingContext.allStems.mostFrequentOriginalWordIndex,
              preprocessingContext.allWords.image));
          for (int j = 0; j < rawCluster.size(); j++) {
            cluster.addDocuments(documents.get(rawCluster.get(j)));
          }
          clusters.add(cluster);
        }
      }
    }

    Collections.sort(clusters, Cluster.BY_REVERSED_SIZE_AND_LABEL_COMPARATOR);
    Cluster.appendOtherTopics(documents, clusters);
  }

  private static final Comparator<IntArrayList> BY_SIZE_DESCENDING = (o1, o2) -> o2.size() - o1.size();

  private List<String> getLabels(IntArrayList documents,
                                 DoubleMatrix2D termDocumentMatrix, IntIntHashMap rowToStemIndex,
                                 int[] mostFrequentOriginalWordIndex, char[][] wordImage) {
    // Prepare a centroid. If dimensionality reduction was used,
    // the centroid from k-means will not be based on real terms,
    // so we need to calculate the centroid here once again based
    // on the cluster's documents.
    final DoubleMatrix1D centroid = new DenseDoubleMatrix1D(termDocumentMatrix.rows());
    for (IntCursor d : documents) {
      centroid.assign(termDocumentMatrix.viewColumn(d.value), Functions.PLUS);
    }

    final List<String> labels = new ArrayList<>(labelCount.get());

    final int[] order = IndirectSort.mergesort(0, centroid.size(),
        new IndirectComparator() {
          @Override
          public int compare(int a, int b) {
            final double valueA = centroid.get(a);
            final double valueB = centroid.get(b);
            return valueA < valueB ? -1 : valueA > valueB ? 1 : 0;
          }
        });
    final double minValueForLabel = centroid.get(order[order.length - Math.min(labelCount.get(), order.length)]);

    for (int i = 0; i < centroid.size(); i++) {
      if (centroid.getQuick(i) >= minValueForLabel) {
        labels.add(LabelFormatter.format(new char[][]
            {
                wordImage[mostFrequentOriginalWordIndex[rowToStemIndex.get(i)]]
            }, new boolean[]
            {
                false
            }, false));
      }
    }
    return labels;
  }

  /**
   * Splits the input documents into the specified number of partitions using the
   * standard k-means routine.
   */
  private List<IntArrayList> split(int partitions, DoubleMatrix2D input,
                                   IntArrayList columns, int iterations) {
    // Prepare selected matrix
    final DoubleMatrix2D selected = input.viewSelection(null, columns.toArray())
        .copy();
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
    final DoubleMatrix2D centroids = new DenseDoubleMatrix2D(selected.rows(),
        partitions).assign(selected.viewPart(0, 0, selected.rows(), partitions));
    final DoubleMatrix2D similarities = new DenseDoubleMatrix2D(partitions,
        selected.columns());

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

  @Override
  public void accept(AttrVisitor visitor) {
    group.visit(visitor);
  }
}
