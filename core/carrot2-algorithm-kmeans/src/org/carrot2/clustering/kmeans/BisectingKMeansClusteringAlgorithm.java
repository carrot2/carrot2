
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2012, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.clustering.kmeans;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.mahout.math.function.Functions;
import org.apache.mahout.math.matrix.DoubleMatrix1D;
import org.apache.mahout.math.matrix.DoubleMatrix2D;
import org.apache.mahout.math.matrix.impl.DenseDoubleMatrix1D;
import org.apache.mahout.math.matrix.impl.DenseDoubleMatrix2D;
import org.carrot2.core.Cluster;
import org.carrot2.core.Document;
import org.carrot2.core.IClusteringAlgorithm;
import org.carrot2.core.LanguageCode;
import org.carrot2.core.ProcessingComponentBase;
import org.carrot2.core.ProcessingException;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.attribute.CommonAttributes;
import org.carrot2.core.attribute.Internal;
import org.carrot2.core.attribute.Processing;
import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.text.preprocessing.LabelFormatter;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.text.preprocessing.pipeline.BasicPreprocessingPipeline;
import org.carrot2.text.vsm.ReducedVectorSpaceModelContext;
import org.carrot2.text.vsm.TermDocumentMatrixBuilder;
import org.carrot2.text.vsm.TermDocumentMatrixReducer;
import org.carrot2.text.vsm.VectorSpaceModelContext;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.IntRange;

import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntIntMap;
import com.carrotsearch.hppc.IntIntOpenHashMap;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.cursors.IntIntCursor;
import com.carrotsearch.hppc.sorting.IndirectComparator;
import com.carrotsearch.hppc.sorting.IndirectSort;
import com.google.common.collect.Lists;

/**
 * A very simple implementation of bisecting k-means clustering. Unlike other algorithms
 * in Carrot2, this one creates hard clusterings (one document belongs only to one
 * cluster). On the other hand, the clusters are labeled only with individual words that
 * may not always fully correspond to all documents in the cluster.
 */
@SuppressWarnings("deprecation")
@Bindable(prefix = "BisectingKMeansClusteringAlgorithm", inherit = CommonAttributes.class)
public class BisectingKMeansClusteringAlgorithm extends ProcessingComponentBase implements
    IClusteringAlgorithm
{
    /** {@link Group} name. */
    private final static String GROUP_KMEANS = "K-means";
    
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
     * The number of clusters to create. The algorithm will create at most the specified
     * number of clusters.
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 2)
    @Group(DefaultGroups.CLUSTERS)
    @Level(AttributeLevel.BASIC)
    @Label("Cluster count")
    public int clusterCount = 25;

    /**
     * The maximum number of k-means iterations to perform.
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 1)
    @Group(GROUP_KMEANS)
    @Level(AttributeLevel.BASIC)
    @Label("Maximum iterations")
    public int maxIterations = 15;

    /**
     * Use dimensionality reduction. If <code>true</code>, k-means will be applied on the
     * dimensionality-reduced term-document matrix with the number of dimensions being
     * equal to the number of requested clusters. If <code>false</code>, the k-means will
     * be performed directly on the original term-document matrix.
     */
    @Processing
    @Input
    @Attribute
    @Group(GROUP_KMEANS)
    @Level(AttributeLevel.BASIC)
    @Label("Use dimensionality reduction")
    public boolean useDimensionalityReduction = true;

    /**
     * Partition count. The number of partitions to create at each k-means clustering
     * iteration.
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 2, max = 10)
    @Group(GROUP_KMEANS)
    @Level(AttributeLevel.BASIC)
    @Label("Partition count")
    public int partitionCount = 2;

    /**
     * Label count. The minimum number of labels to return for each cluster.
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 1, max = 10)
    @Group(DefaultGroups.CLUSTERS)
    @Level(AttributeLevel.BASIC)
    @Label("Label count")
    public int labelCount = 3;

    /**
     * Common preprocessing tasks handler, contains bindable attributes.
     */
    public final BasicPreprocessingPipeline preprocessingPipeline = new BasicPreprocessingPipeline();

    /**
     * Term-document matrix builder for the algorithm, contains bindable attributes.
     */
    public final TermDocumentMatrixBuilder matrixBuilder = new TermDocumentMatrixBuilder();

    /**
     * Term-document matrix reducer for the algorithm, contains bindable attributes.
     */
    public final TermDocumentMatrixReducer matrixReducer = new TermDocumentMatrixReducer();

    /**
     * Cluster label formatter, contains bindable attributes.
     */
    public final LabelFormatter labelFormatter = new LabelFormatter();

    @Override
    public void process() throws ProcessingException
    {
        // Preprocessing of documents
        final PreprocessingContext preprocessingContext = preprocessingPipeline
            .preprocess(documents, null, LanguageCode.ENGLISH);

        // Add trivial AllLabels so that we can reuse the common TD matrix builder
        final int [] stemsMfow = preprocessingContext.allStems.mostFrequentOriginalWordIndex;
        final short [] wordsType = preprocessingContext.allWords.type;
        final IntArrayList featureIndices = new IntArrayList(stemsMfow.length);
        for (int i = 0; i < stemsMfow.length; i++)
        {
            final short flag = wordsType[stemsMfow[i]];
            if ((flag & (ITokenizer.TF_COMMON_WORD | ITokenizer.TF_QUERY_WORD | ITokenizer.TT_NUMERIC)) == 0)
            {
                featureIndices.add(stemsMfow[i]);
            }
        }
        preprocessingContext.allLabels.featureIndex = featureIndices.toArray();
        preprocessingContext.allLabels.firstPhraseIndex = -1;

        // Further processing only if there are words to process
        clusters = Lists.newArrayList();
        if (preprocessingContext.hasLabels())
        {
            // Term-document matrix building and reduction
            final VectorSpaceModelContext vsmContext = new VectorSpaceModelContext(
                preprocessingContext);
            final ReducedVectorSpaceModelContext reducedVsmContext = new ReducedVectorSpaceModelContext(
                vsmContext);

            matrixBuilder.buildTermDocumentMatrix(vsmContext);
            matrixBuilder.buildTermPhraseMatrix(vsmContext);

            // Prepare rowIndex -> stemIndex mapping for labeling
            final IntIntOpenHashMap rowToStemIndex = new IntIntOpenHashMap();
            for (IntIntCursor c : vsmContext.stemToRowIndex)
            {
                rowToStemIndex.put(c.value, c.key);
            }

            final DoubleMatrix2D tdMatrix;
            if (useDimensionalityReduction)
            {
                matrixReducer.reduce(reducedVsmContext, clusterCount);
                tdMatrix = reducedVsmContext.coefficientMatrix.viewDice();
            }
            else
            {
                tdMatrix = vsmContext.termDocumentMatrix;
            }

            // Initial selection containing all columns, initial clustering
            final IntArrayList columns = new IntArrayList(tdMatrix.columns());
            for (int c = 0; c < tdMatrix.columns(); c++)
            {
                columns.add(c);
            }
            final List<IntArrayList> rawClusters = Lists.newArrayList();
            rawClusters.addAll(split(partitionCount, tdMatrix, columns, maxIterations));

            boolean finished = false;
            int emptySplits = 0;
            while (rawClusters.size() < clusterCount && !finished)
            {
                // Find largest cluster to split
                int largestIndex = 0;
                IntArrayList largest = rawClusters.get(0);
                finished = largest.size() <= partitionCount * 2;
                for (int i = 1; i < rawClusters.size(); i++)
                {
                    final int size = rawClusters.get(i).size();
                    if (size > largest.size() && size > partitionCount * 2)
                    {
                        largest = rawClusters.get(i);
                        largestIndex = i;
                        finished = false;
                    }
                }

                if (finished)
                {
                    // No more splittable clusters
                    break;
                }

                final List<IntArrayList> split = split(partitionCount, tdMatrix, largest,
                    maxIterations);
                if (split.size() > 1)
                {
                    rawClusters.remove(largestIndex);
                    rawClusters.addAll(split);
                    emptySplits = 0;
                }
                else
                {
                    if (++emptySplits >= rawClusters.size())
                    {
                        // For each cluster we tried to split, we got no subclusters.
                        // This means there's no more clusters we can create.
                        break;
                    }
                }
            }

            for (int i = 0; i < rawClusters.size(); i++)
            {
                final Cluster cluster = new Cluster();

                final IntArrayList rawCluster = rawClusters.get(i);
                if (rawCluster.size() > 1)
                {
                    cluster.addPhrases(getLabels(rawCluster,
                        vsmContext.termDocumentMatrix, rowToStemIndex,
                        preprocessingContext.allStems.mostFrequentOriginalWordIndex,
                        preprocessingContext.allWords.image));
                    for (int j = 0; j < rawCluster.size(); j++)
                    {
                        cluster.addDocuments(documents.get(rawCluster.get(j)));
                    }
                    clusters.add(cluster);
                }
            }
        }

        Collections.sort(clusters, Cluster.BY_REVERSED_SIZE_AND_LABEL_COMPARATOR);
        Cluster.appendOtherTopics(documents, clusters);
    }

    private List<String> getLabels(IntArrayList documents,
        DoubleMatrix2D termDocumentMatrix, IntIntOpenHashMap rowToStemIndex,
        int [] mostFrequentOriginalWordIndex, char [][] wordImage)
    {
        // Prepare a centroid. If dimensionality reduction was used,
        // the centroid from k-means will not be based on real terms,
        // so we need to calculate the centroid here once again based
        // on the cluster's documents.
        final DoubleMatrix1D centroid = new DenseDoubleMatrix1D(termDocumentMatrix.rows());
        for (IntCursor d : documents)
        {
            centroid.assign(termDocumentMatrix.viewColumn(d.value), Functions.PLUS);
        }

        final List<String> labels = Lists.newArrayListWithCapacity(labelCount);

        final int [] order = IndirectSort.mergesort(0, centroid.size(),
            new IndirectComparator()
            {
                @Override
                public int compare(int a, int b)
                {
                    final double valueA = centroid.get(a);
                    final double valueB = centroid.get(b);
                    return valueA < valueB ? -1 : valueA > valueB ? 1 : 0;
                }
            });
        final double minValueForLabel = centroid.get(order[order.length
            - Math.min(labelCount, order.length)]);

        for (int i = 0; i < centroid.size(); i++)
        {
            if (centroid.getQuick(i) >= minValueForLabel)
            {
                labels.add(LabelFormatter.format(new char [] []
                {
                    wordImage[mostFrequentOriginalWordIndex[rowToStemIndex.get(i)]]
                }, new boolean []
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
        IntArrayList columns, int iterations)
    {
        // Prepare selected matrix
        final DoubleMatrix2D selected = input.viewSelection(null, columns.toArray())
            .copy();
        final IntIntMap selectedToInput = new IntIntOpenHashMap(selected.columns());
        for (int i = 0; i < columns.size(); i++)
        {
            selectedToInput.put(i, columns.get(i));
        }

        // Prepare results holders
        List<IntArrayList> result = Lists.newArrayList();
        List<IntArrayList> previousResult = null;
        for (int i = 0; i < partitions; i++)
        {
            result.add(new IntArrayList(selected.columns()));
        }

        // Matrices for centroids and document-centroid similarities
        final DoubleMatrix2D centroids = new DenseDoubleMatrix2D(selected.rows(),
            partitions).assign(selected.viewPart(0, 0, selected.rows(), partitions));
        final DoubleMatrix2D similarities = new DenseDoubleMatrix2D(partitions,
            selected.columns());

        // Run a fixed number of K-means iterations
        for (int it = 0; it < iterations; it++)
        {
            // Calculate similarity to centroids
            centroids.zMult(selected, similarities, 1, 0, true, false);

            // Assign documents to the nearest centroid
            for (int c = 0; c < similarities.columns(); c++)
            {
                int maxRow = 0;
                double max = similarities.get(0, c);
                for (int r = 1; r < similarities.rows(); r++)
                {
                    if (max < similarities.get(r, c))
                    {
                        max = similarities.get(r, c);
                        maxRow = r;
                    }
                }

                result.get(maxRow).add(c);
            }

            if (ObjectUtils.equals(previousResult, result))
            {
                // Unchanged result
                break;
            }

            // Update centroids
            for (int i = 0; i < result.size(); i++)
            {
                final IntArrayList cluster = result.get(i);
                for (int k = 0; k < selected.rows(); k++)
                {
                    double sum = 0;
                    for (int j = 0; j < cluster.size(); j++)
                    {
                        sum += selected.get(k, cluster.get(j));
                    }
                    centroids.setQuick(k, i, sum / cluster.size());
                }
            }

            if (it < iterations - 1)
            {
                previousResult = result;
                result = Lists.newArrayList();
                for (int i = 0; i < partitions; i++)
                {
                    result.add(new IntArrayList(selected.columns()));
                }
            }
        }

        // Map the results back to the global indices
        for (Iterator<IntArrayList> it = result.iterator(); it.hasNext();)
        {
            final IntArrayList cluster = it.next();
            if (cluster.isEmpty())
            {
                it.remove();
            }
            else
            {
                for (int j = 0; j < cluster.size(); j++)
                {
                    cluster.set(j, selectedToInput.get(cluster.get(j)));
                }
            }
        }

        return result;
    }
}
