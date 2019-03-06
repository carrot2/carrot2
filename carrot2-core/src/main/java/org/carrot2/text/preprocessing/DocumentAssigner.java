
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

package org.carrot2.text.preprocessing;

import com.carrotsearch.hppc.BitSet;
import com.carrotsearch.hppc.IntArrayList;
import org.carrot2.language.TokenTypeUtils;
import org.carrot2.text.preprocessing.PreprocessingContext.AllLabels;
import org.carrot2.attrs.AttrBoolean;
import org.carrot2.attrs.AttrComposite;
import org.carrot2.attrs.AttrInteger;

import java.util.ArrayList;

/**
 * Assigns document to label candidates. For each label candidate from
 * {@link AllLabels#featureIndex} an {@link BitSet} with the assigned documents is
 * constructed. The assignment algorithm is rather simple: in order to be assigned to a
 * label, a document must contain at least one occurrence of each non-stop word from the
 * label.
 * <p>
 * This class saves the following results to the {@link PreprocessingContext} :
 * <ul>
 * <li>{@link AllLabels#documentIndices}</li>
 * </ul>
 * <p>
 * This class requires that {@link InputTokenizer}, {@link CaseNormalizer},
 * {@link StopListMarker}, {@link PhraseExtractor} and {@link LabelFilterProcessor} be
 * invoked first.
 */
public class DocumentAssigner extends AttrComposite {
  /**
   * Only exact phrase assignments. Assign only documents that contain the label in its
   * original form, including the order of words. Enabling this option will cause less
   * documents to be put in clusters, which result in higher precision of assignment,
   * but also a larger "Other Topics" group. Disabling this option will cause more
   * documents to be put in clusters, which will make the "Other Topics" cluster
   * smaller, but also lower the precision of cluster-document assignments.
   */
  public AttrBoolean exactPhraseAssignment = attributes.register("exactPhraseAssignment", AttrBoolean.builder()
    .label("Exact phrase assignment")
    .defaultValue(false));

    /**
     * Determines the minimum number of documents in each cluster.
     */
  public AttrInteger minClusterSize = attributes.register("minClusterSize", AttrInteger.builder()
    .label("Minimum cluster size")
    .min(1)
    .max(100)
    .defaultValue(2));

  /**
   * Assigns document to label candidates.
   */
  void assign(PreprocessingContext context) {
    final int[] labelsFeatureIndex = context.allLabels.featureIndex;
    final int[][] stemsTfByDocument = context.allStems.tfByDocument;
    final int[] wordsStemIndex = context.allWords.stemIndex;
    final short[] wordsTypes = context.allWords.type;
    final int[][] phrasesTfByDocument = context.allPhrases.tfByDocument;
    final int[][] phrasesWordIndices = context.allPhrases.wordIndices;
    final int wordCount = wordsStemIndex.length;
    final int documentCount = context.documentCount;

    final BitSet[] labelsDocumentIndices = new BitSet[labelsFeatureIndex.length];

    for (int i = 0; i < labelsFeatureIndex.length; i++) {
      final BitSet documentIndices = new BitSet(documentCount);

      final int featureIndex = labelsFeatureIndex[i];
      if (featureIndex < wordCount) {
        addTfByDocumentToBitSet(documentIndices, stemsTfByDocument[wordsStemIndex[featureIndex]]);
      } else {
        final int phraseIndex = featureIndex - wordCount;
        if (exactPhraseAssignment.get()) {
          addTfByDocumentToBitSet(documentIndices, phrasesTfByDocument[phraseIndex]);
        } else {
          final int[] wordIndices = phrasesWordIndices[phraseIndex];
          boolean firstAdded = false;

          for (int j = 0; j < wordIndices.length; j++) {
            final int wordIndex = wordIndices[j];
            if (!TokenTypeUtils.isCommon(wordsTypes[wordIndex])) {
              if (!firstAdded) {
                addTfByDocumentToBitSet(documentIndices, stemsTfByDocument[wordsStemIndex[wordIndex]]);
                firstAdded = true;
              } else {
                final BitSet temp = new BitSet(documentCount);
                addTfByDocumentToBitSet(temp, stemsTfByDocument[wordsStemIndex[wordIndex]]);
                documentIndices.and(temp);
              }
            }
          }
        }
      }

      labelsDocumentIndices[i] = documentIndices;
    }

    // Filter out labels that do not meet the minimum cluster size
    int minClusterSize = this.minClusterSize.get();
    if (minClusterSize > 1) {
      final IntArrayList newFeatureIndex = new IntArrayList(labelsFeatureIndex.length);
      final ArrayList<BitSet> newDocumentIndices = new ArrayList<>(labelsFeatureIndex.length);

      for (int i = 0; i < labelsFeatureIndex.length; i++) {
        if (labelsDocumentIndices[i].cardinality() >= minClusterSize) {
          newFeatureIndex.add(labelsFeatureIndex[i]);
          newDocumentIndices.add(labelsDocumentIndices[i]);
        }
      }
      context.allLabels.documentIndices = newDocumentIndices.toArray(new BitSet[0]);
      context.allLabels.featureIndex = newFeatureIndex.toArray();
      LabelFilterProcessor.updateFirstPhraseIndex(context);
    } else {
      context.allLabels.documentIndices = labelsDocumentIndices;
    }
  }

  private static void addTfByDocumentToBitSet(final BitSet documentIndices, final int[] tfByDocument) {
    for (int j = 0; j < tfByDocument.length / 2; j++) {
      documentIndices.set(tfByDocument[j * 2]);
    }
  }
}
