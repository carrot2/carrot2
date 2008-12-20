
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.preprocessing;

import org.carrot2.core.attribute.Processing;
import org.carrot2.text.preprocessing.PreprocessingContext.AllLabels;
import org.carrot2.util.attribute.*;

import bak.pcj.set.IntBitSet;
import bak.pcj.set.IntSet;

/**
 * Assigns document to label candidates. For each label candidate from
 * {@link AllLabels#featureIndex} an {@link IntSet} with the assigned documents is
 * constructed. The assignment algorithm is rather simple: in order to be assigned to a
 * label, a document must contain at least one occurrence of each non-stop word from the
 * label.
 * <p>
 * This class saves the following results to the {@link PreprocessingContext} :
 * <ul>
 * <li>{@link AllLabels#documentIndices}</li>
 * </ul>
 * <p>
 * This class requires that {@link Tokenizer}, {@link CaseNormalizer},
 * {@link StopListMarker}, {@link PhraseExtractor} and {@link LabelFilterProcessor} be
 * invoked first.
 */
@Bindable(prefix = "DocumentAssigner")
public class DocumentAssigner
{
    /**
     * Only exact phrase assignments. Assign only documents that contain the label in its
     * original form, including the order of words. Enabling this option will cause less
     * documents to be put in clusters, which result in higher precision of assignment,
     * but also a larger "Other Topics" group. Disabling this option will cause more
     * documents to be put in clusters, which will make the "Other Topics" cluster
     * smaller, but also lower the precision of cluster-document assignments.
     * 
     * @level Medium
     * @group Preprocessing
     * @label Exact phrase assignment
     */
    @Input
    @Processing
    @Attribute
    public boolean exactPhraseAssignment = false;

    /**
     * Assigns document to label candidates.
     */
    public void assign(PreprocessingContext context)
    {
        final int [] labelsFeatureIndex = context.allLabels.featureIndex;
        final int [][] wordsTfByDocument = context.allWords.tfByDocument;
        final boolean [] wordsCommonTerm = context.allWords.commonTermFlag;
        final int [][] phrasesTfByDocument = context.allPhrases.tfByDocument;
        final int [][] phrasesWordIndices = context.allPhrases.wordIndices;
        final int wordCount = wordsTfByDocument.length;
        final int documentCount = context.documents.size();

        final IntSet [] labelsDocumentIndices = new IntSet [labelsFeatureIndex.length];

        for (int i = 0; i < labelsFeatureIndex.length; i++)
        {
            final IntBitSet documentIndices = new IntBitSet(documentCount);
            final int [] tfByDocument;

            final int featureIndex = labelsFeatureIndex[i];
            if (featureIndex < wordCount)
            {
                tfByDocument = wordsTfByDocument[featureIndex];
            }
            else
            {
                final int phraseIndex = featureIndex - wordCount;
                tfByDocument = phrasesTfByDocument[phraseIndex];
                if (!exactPhraseAssignment)
                {
                    final int [] wordIndices = phrasesWordIndices[phraseIndex];
                    boolean firstAdded = false;

                    for (int j = 0; j < wordIndices.length; j++)
                    {
                        final int wordIndex = wordIndices[j];
                        if (!wordsCommonTerm[wordIndex])
                        {
                            if (!firstAdded)
                            {
                                addTfByDocumentToBitSet(documentIndices,
                                    wordsTfByDocument[wordIndex]);
                                firstAdded = true;
                            }
                            else
                            {
                                final IntBitSet temp = new IntBitSet(documentCount);
                                addTfByDocumentToBitSet(temp,
                                    wordsTfByDocument[wordIndex]);
                                documentIndices.retainAll(temp);
                            }
                        }
                    }
                }
            }

            addTfByDocumentToBitSet(documentIndices, tfByDocument);

            labelsDocumentIndices[i] = documentIndices;
        }

        context.allLabels.documentIndices = labelsDocumentIndices;
    }

    private static void addTfByDocumentToBitSet(final IntBitSet documentIndices,
        final int [] tfByDocument)
    {
        for (int j = 0; j < tfByDocument.length / 2; j++)
        {
            documentIndices.add(tfByDocument[j * 2]);
        }
    }
}
