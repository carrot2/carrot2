
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.preprocessing.filter;

import java.util.ArrayList;
import java.util.List;

import org.carrot2.text.preprocessing.PreprocessingContext;

import org.carrot2.shaded.guava.common.collect.Lists;

/**
 * Base class for complete phrase filtering.
 */
abstract class CompleteLabelFilterBase
{
    void filter(PreprocessingContext context, boolean [] acceptedStems,
        boolean [] acceptedPhrases, double labelOverrideThreshold)
    {
        if (acceptedStems.length + acceptedPhrases.length < 2)
        {
            return;
        }

        final int [] stemTf = context.allStems.tf;
        final int [] phraseTf = context.allPhrases.tf;
        final int [] mostFrequentOriginalWordIndex = context.allStems.mostFrequentOriginalWordIndex;
        final int [] wordsStemIndex = context.allWords.stemIndex;

        // Build labelIndex-wordIndices combos for each word and phrase. We'll use
        // them below to create an LCP array.
        final ArrayList<LabelIndexWithCodes> phraseIndexesWithCodes = Lists
            .newArrayListWithExpectedSize(acceptedStems.length + acceptedPhrases.length);
        for (int i = 0; i < acceptedStems.length + acceptedPhrases.length; i++)
        {
            phraseIndexesWithCodes.add(new LabelIndexWithCodes(i, getLabelWordIndexes(
                acceptedStems.length, mostFrequentOriginalWordIndex,
                context.allPhrases.wordIndices, i)));
        }

        // Sort and create LCP array
        final List<LabelIndexWithCodes> sortedPhrasesWithCodes = sortPhraseCodes(phraseIndexesWithCodes);
        int [] lcpArray = createLcp(sortedPhrasesWithCodes);

        // Remove superseded phrases
        int i = 0;
        while (i < sortedPhrasesWithCodes.size() - 1)
        {
            final LabelIndexWithCodes currentLabelWithCodes = sortedPhrasesWithCodes
                .get(i);
            final int currentLabelIndex = currentLabelWithCodes.getLabelIndex();

            // Check only those phrases that are not removed and that are
            // themselves subphrases of some longer phrases
            if (getLabelLength(acceptedStems.length, context.allPhrases.wordIndices,
                currentLabelIndex) == lcpArray[i]
                && isLabelAccepted(acceptedStems.length, wordsStemIndex,
                    currentLabelIndex, acceptedStems, acceptedPhrases))
            {
                int j = i;
                while (j < sortedPhrasesWithCodes.size() - 1
                    && lcpArray[j] >= lcpArray[i])
                {
                    final LabelIndexWithCodes nextPhraseWithCodes = sortedPhrasesWithCodes
                        .get(j + 1);
                    final int nextLabelIndex = nextPhraseWithCodes.getLabelIndex();

                    double labelOverride = calculateLabelOverride(acceptedStems.length,
                        stemTf, phraseTf, nextLabelIndex, currentLabelIndex);
                    if ((isLabelAccepted(acceptedStems.length, wordsStemIndex,
                        nextLabelIndex, acceptedStems, acceptedPhrases) && labelOverride >= labelOverrideThreshold))
                    {
                        markLabelAsRemoved(acceptedStems.length, currentLabelIndex,
                            acceptedStems, acceptedPhrases);
                        break;
                    }

                    j++;
                }
            }

            i++;
        }
    }

    abstract List<LabelIndexWithCodes> sortPhraseCodes(
        List<LabelIndexWithCodes> phrasesWithCodes);

    abstract int [] createLcp(List<LabelIndexWithCodes> sortedPhrasesWithCodes);

    static class LabelIndexWithCodes
    {
        final private int labelIndex;

        final private int [] codes;

        public LabelIndexWithCodes(int labelIndex, int [] codes)
        {
            this.labelIndex = labelIndex;
            this.codes = codes;
        }

        public int [] getCodes()
        {
            return codes;
        }

        public int getLabelIndex()
        {
            return labelIndex;
        }
    }

    private final static int [] getLabelWordIndexes(int wordCount,
        int [] mostFrequentWordIndex, int [][] wordIndices, int featureIndex)
    {
        if (featureIndex < wordCount)
        {
            return new int []
            {
                mostFrequentWordIndex[featureIndex]
            };
        }
        else
        {
            return wordIndices[featureIndex - wordCount];
        }
    }

    private final static int getLabelLength(int wordCount, int [][] wordIndices,
        int featureIndex)
    {
        return featureIndex < wordCount ? 1
            : wordIndices[featureIndex - wordCount].length;
    }

    private final static boolean isLabelAccepted(int wordCount, int [] wordStemIndex,
        int featureIndex, boolean [] acceptedStems, boolean [] acceptedPhrases)
    {
        if (featureIndex < wordCount)
        {
            return acceptedStems[featureIndex];
        }
        else
        {
            return acceptedPhrases[featureIndex - wordCount];
        }
    }

    private final static void markLabelAsRemoved(int wordCount, int featureIndex,
        boolean [] acceptedWords, boolean [] acceptedPhrases)
    {
        if (featureIndex < wordCount)
        {
            acceptedWords[featureIndex] = false;
        }
        else
        {
            acceptedPhrases[featureIndex - wordCount] = false;
        }
    }

    private final static double calculateLabelOverride(int wordCount, int [] wordTf,
        int [] phraseTf, int overridingLabelIndex, int overridenLabelIndex)
    {
        final int overridingTf = overridingLabelIndex < wordCount ? wordTf[overridingLabelIndex]
            : phraseTf[overridingLabelIndex - wordCount];
        final int overridenTf = overridenLabelIndex < wordCount ? wordTf[overridenLabelIndex]
            : phraseTf[overridenLabelIndex - wordCount];

        return ((double) overridingTf) / overridenTf;
    }
}
