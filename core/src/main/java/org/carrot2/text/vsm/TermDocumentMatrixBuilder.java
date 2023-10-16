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
package org.carrot2.text.vsm;

import com.carrotsearch.hppc.BitSet;
import com.carrotsearch.hppc.IntIntHashMap;
import com.carrotsearch.hppc.sorting.IndirectSort;
import java.util.Arrays;
import java.util.function.IntToDoubleFunction;
import org.carrot2.attrs.AttrComposite;
import org.carrot2.attrs.AttrDouble;
import org.carrot2.attrs.AttrInteger;
import org.carrot2.attrs.AttrObject;
import org.carrot2.attrs.AttrStringArray;
import org.carrot2.language.TokenTypeUtils;
import org.carrot2.math.mahout.matrix.DoubleMatrix2D;
import org.carrot2.math.mahout.matrix.impl.DenseDoubleMatrix2D;
import org.carrot2.math.mahout.matrix.impl.SparseDoubleMatrix2D;
import org.carrot2.math.matrix.MatrixUtils;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.text.preprocessing.PreprocessingContext.AllFields;

/** Builds a term document matrix based on the provided {@link PreprocessingContext}. */
public class TermDocumentMatrixBuilder extends AttrComposite {
  /**
   * The extra weight to apply to words that appeared in boosted fields. The larger the value, the
   * stronger the boost.
   */
  public final AttrDouble boostedFieldWeight =
      attributes.register(
          "boostedFieldWeight",
          AttrDouble.builder().label("Boosted fields weight").min(0).max(10).defaultValue(2.));

  /**
   * A list fields for which to apply extra weight. Content of fields provided in this parameter can
   * be given more weight during clustering. You may want to boost, for example, the title field
   * with the assumption that it accurately summarizes the content of the whole document.
   */
  public AttrStringArray boostFields =
      attributes.register(
          "boostFields",
          AttrStringArray.builder().label("Boosted fields").defaultValue(new String[] {}));

  /**
   * Maximum number of elements the term-document matrix can have. The larger the allowed matrix
   * size, the more accurate, time- and memory-consuming clustering.
   */
  public final AttrInteger maximumMatrixSize =
      attributes.register(
          "maximumMatrixSize",
          AttrInteger.builder()
              .label("Maximum term-document matrix size")
              .min(50 * 100)
              .defaultValue(250 * 150));

  /**
   * Maximum document frequency allowed for words as a fraction of all documents. Words with
   * document frequency larger than {@link #maxWordDf} will be ignored. For example, when {@link
   * #maxWordDf} is 0.4, words appearing in more than 40% of documents will be be ignored. A value
   * of 1.0 means that all words will be taken into account, no matter in how many documents they
   * appear.
   *
   * <p>This parameter may be useful when certain words appear in most of the input documents (e.g.
   * company name from header or footer) and such words dominate the cluster labels. In such case,
   * setting it to a value lower than 1.0 (e.g. 0.9) may improve the clusters.
   *
   * <p>Another useful application of this parameter is when there is a need to generate only very
   * specific clusters, that is clusters containing small numbers of documents. This can be achieved
   * by setting {@link #maxWordDf} to extremely low values: 0.1 or 0.05.
   */
  public final AttrDouble maxWordDf =
      attributes.register(
          "maxWordDf",
          AttrDouble.builder()
              .label("Maximum word document frequency")
              .min(0)
              .max(1)
              .defaultValue(0.9));

  /** Method for calculating weights of words in the term-document matrices. */
  public TermWeighting termWeighting;

  {
    attributes.register(
        "termWeighting",
        AttrObject.builder(TermWeighting.class)
            .label("Term weighting for term-document matrix")
            .getset(() -> termWeighting, (v) -> termWeighting = v)
            .defaultValue(LogTfIdfTermWeighting::new));
  }

  /**
   * Builds a term-document matrix from data provided in the <code>context</code>, stores the result
   * in there.
   */
  public void buildTermDocumentMatrix(VectorSpaceModelContext vsmContext) {
    final PreprocessingContext preprocessingContext = vsmContext.preprocessingContext;

    final int documentCount = preprocessingContext.documentCount;
    final int[] stemsTf = preprocessingContext.allStems.tf;
    final int[][] stemsTfByDocument = preprocessingContext.allStems.tfByDocument;
    final byte[] stemsFieldIndices = preprocessingContext.allStems.fieldIndices;

    if (documentCount == 0) {
      vsmContext.termDocumentMatrix = new DenseDoubleMatrix2D(0, 0);
      vsmContext.stemToRowIndex = new IntIntHashMap();
      return;
    }

    // Determine boosts for
    IntToDoubleFunction fieldIndexToBoost;
    if (boostFields.get().length == 0) {
      fieldIndexToBoost = (fieldIndices -> 1d);
    } else {
      double[] boosts = new double[256];
      Arrays.fill(boosts, 1d);
      AllFields allFields = preprocessingContext.allFields;
      for (String fieldName : boostFields.get()) {
        int fieldIndex = allFields.fieldIndex(fieldName);
        if (fieldIndex >= 0) {
          int mask = 1 << fieldIndex;
          for (int i = 0; i < boosts.length; i++) {
            if ((i & mask) != 0) {
              boosts[i] = boostedFieldWeight.get();
            }
          }
        }
      }

      fieldIndexToBoost = (fieldIndices -> boosts[fieldIndices]);
    }

    // Determine the stems we, ideally, should include in the matrix
    int[] stemsToInclude = computeRequiredStemIndices(preprocessingContext);

    // Sort stems by weight, so that stems get included in the matrix in the order
    // of frequency
    final TermWeighting termWeighting = this.termWeighting;
    final double[] stemsWeight = new double[stemsToInclude.length];
    for (int i = 0; i < stemsToInclude.length; i++) {
      final int stemIndex = stemsToInclude[i];
      double weight =
          termWeighting.calculateTermWeight(
              stemsTf[stemIndex], stemsTfByDocument[stemIndex].length / 2, documentCount);
      stemsWeight[i] = weight * fieldIndexToBoost.applyAsDouble(stemsFieldIndices[stemIndex]);
    }
    final int[] stemWeightOrder =
        IndirectSort.mergesort(
            0, stemsWeight.length, (a, b) -> Double.compare(stemsWeight[b], stemsWeight[a]));

    // Calculate the number of terms we can include to fulfill the max matrix size
    final int maxRows = maximumMatrixSize.get() / documentCount;
    final DoubleMatrix2D tdMatrix =
        new DenseDoubleMatrix2D(Math.min(maxRows, stemsToInclude.length), documentCount);

    for (int i = 0; i < stemWeightOrder.length && i < maxRows; i++) {
      final int stemIndex = stemsToInclude[stemWeightOrder[i]];
      final int[] tfByDocument = stemsTfByDocument[stemIndex];
      final int df = tfByDocument.length / 2;
      final byte fieldIndices = stemsFieldIndices[stemIndex];

      double fieldWeight = fieldIndexToBoost.applyAsDouble(fieldIndices);
      for (int j = 0; j < df; j++) {
        double weight =
            termWeighting.calculateTermWeight(tfByDocument[j * 2 + 1], df, documentCount);

        weight *= fieldWeight;
        tdMatrix.set(i, tfByDocument[j * 2], weight);
      }
    }

    // Convert stemsToInclude into tdMatrixStemIndices
    final IntIntHashMap stemToRowIndex = contantOrderIntIntHashMap(0xdeadbeef);
    for (int i = 0; i < stemWeightOrder.length && i < tdMatrix.rows(); i++) {
      stemToRowIndex.put(stemsToInclude[stemWeightOrder[i]], i);
    }

    // Store the results
    vsmContext.termDocumentMatrix = tdMatrix;
    vsmContext.stemToRowIndex = stemToRowIndex;
  }

  public static final IntIntHashMap contantOrderIntIntHashMap(int seed) {
    return new IntIntHashMap() {
      {
        super.iterationSeed = seed;
      }
    };
  }

  /**
   * Builds a term-phrase matrix in the same space as the main term-document matrix. If the
   * processing context contains no phrases, {@link VectorSpaceModelContext#termPhraseMatrix} will
   * remain <code>null</code>.
   */
  public void buildTermPhraseMatrix(VectorSpaceModelContext context) {
    final PreprocessingContext preprocessingContext = context.preprocessingContext;
    final IntIntHashMap stemToRowIndex = context.stemToRowIndex;
    final int[] labelsFeatureIndex = preprocessingContext.allLabels.featureIndex;
    final int firstPhraseIndex = preprocessingContext.allLabels.firstPhraseIndex;

    if (firstPhraseIndex >= 0 && stemToRowIndex.size() > 0) {
      // Build phrase matrix
      int[] phraseFeatureIndices = new int[labelsFeatureIndex.length - firstPhraseIndex];
      for (int featureIndex = 0; featureIndex < phraseFeatureIndices.length; featureIndex++) {
        phraseFeatureIndices[featureIndex] = labelsFeatureIndex[featureIndex + firstPhraseIndex];
      }

      final DoubleMatrix2D phraseMatrix =
          TermDocumentMatrixBuilder.buildAlignedMatrix(
              context, phraseFeatureIndices, termWeighting);
      MatrixUtils.normalizeColumnL2(phraseMatrix, null);
      context.termPhraseMatrix = phraseMatrix.viewDice();
    }
  }

  /**
   * Computes stem indices of words that are one-word label candidates or are non-stop words from
   * phrase label candidates.
   */
  private int[] computeRequiredStemIndices(PreprocessingContext context) {
    final int[] labelsFeatureIndex = context.allLabels.featureIndex;
    final int[] wordsStemIndex = context.allWords.stemIndex;
    final short[] wordsTypes = context.allWords.type;
    final int[][] phrasesWordIndices = context.allPhrases.wordIndices;
    final int wordCount = wordsStemIndex.length;

    final int[][] stemsTfByDocument = context.allStems.tfByDocument;
    int documentCount = context.documentCount;
    final BitSet requiredStemIndices = new BitSet(labelsFeatureIndex.length);

    double maxWordDf = this.maxWordDf.get();
    for (int i = 0; i < labelsFeatureIndex.length; i++) {
      final int featureIndex = labelsFeatureIndex[i];
      if (featureIndex < wordCount) {
        addStemIndex(
            wordsStemIndex,
            documentCount,
            stemsTfByDocument,
            requiredStemIndices,
            featureIndex,
            maxWordDf);
      } else {
        final int[] wordIndices = phrasesWordIndices[featureIndex - wordCount];
        for (int j = 0; j < wordIndices.length; j++) {
          final int wordIndex = wordIndices[j];
          if (!TokenTypeUtils.isCommon(wordsTypes[wordIndex])) {
            addStemIndex(
                wordsStemIndex,
                documentCount,
                stemsTfByDocument,
                requiredStemIndices,
                wordIndex,
                maxWordDf);
          }
        }
      }
    }

    return requiredStemIndices.asIntLookupContainer().toArray();
  }

  /** Adds stem index to the set with a check on the stem's document frequency. */
  private void addStemIndex(
      final int[] wordsStemIndex,
      int documentCount,
      int[][] stemsTfByDocument,
      final BitSet requiredStemIndices,
      final int featureIndex,
      double maxWordDf) {
    final int stemIndex = wordsStemIndex[featureIndex];
    final int df = stemsTfByDocument[stemIndex].length / 2;
    if (((double) df / documentCount) <= maxWordDf) {
      requiredStemIndices.set(stemIndex);
    }
  }

  /**
   * Builds a sparse term-document-like matrix for the provided matrixWordIndices in the same term
   * space as the original term-document matrix.
   */
  static DoubleMatrix2D buildAlignedMatrix(
      VectorSpaceModelContext vsmContext, int[] featureIndex, TermWeighting termWeighting) {
    final IntIntHashMap stemToRowIndex = vsmContext.stemToRowIndex;
    if (featureIndex.length == 0) {
      return new DenseDoubleMatrix2D(stemToRowIndex.size(), 0);
    }

    final DoubleMatrix2D phraseMatrix =
        new SparseDoubleMatrix2D(stemToRowIndex.size(), featureIndex.length);

    final PreprocessingContext preprocessingContext = vsmContext.preprocessingContext;
    final int[] wordsStemIndex = preprocessingContext.allWords.stemIndex;
    final int[] stemsTf = preprocessingContext.allStems.tf;
    final int[][] stemsTfByDocument = preprocessingContext.allStems.tfByDocument;
    final int[][] phrasesWordIndices = preprocessingContext.allPhrases.wordIndices;
    final int documentCount = preprocessingContext.documentCount;
    final int wordCount = wordsStemIndex.length;

    for (int i = 0; i < featureIndex.length; i++) {
      final int feature = featureIndex[i];
      final int[] wordIndices;
      if (feature < wordCount) {
        wordIndices = new int[] {feature};
      } else {
        wordIndices = phrasesWordIndices[feature - wordCount];
      }

      for (int wordIndex = 0; wordIndex < wordIndices.length; wordIndex++) {
        final int stemIndex = wordsStemIndex[wordIndices[wordIndex]];
        final int index = stemToRowIndex.indexOf(stemIndex);
        if (stemToRowIndex.indexExists(index)) {
          final int rowIndex = stemToRowIndex.indexGet(index);

          double weight =
              termWeighting.calculateTermWeight(
                  stemsTf[stemIndex], stemsTfByDocument[stemIndex].length / 2, documentCount);

          phraseMatrix.setQuick(rowIndex, i, weight);
        }
      }
    }

    return phraseMatrix;
  }
}
