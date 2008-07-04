package org.carrot2.text.preprocessing.filter;

import java.util.ArrayList;
import java.util.List;

import org.carrot2.text.preprocessing.PreprocessingContext;

import com.google.common.collect.Lists;

/**
 * Base class for complete phrase filtering.
 */
abstract class CompleteLabelFilterBase
{
    void filter(PreprocessingContext context, boolean [] acceptedWords,
        boolean [] acceptedPhrases, double labelOverrideThreshold)
    {
        if (acceptedWords.length + acceptedPhrases.length < 2)
        {
            return;
        }

        final int [] wordTf = context.allWords.tf;
        final int [] phraseTf = context.allPhrases.tf;

        // For the purpose of this filter, create a common array of word and phrase
        // indices (features).
        final int [] labelIndexes = new int [acceptedWords.length
            + acceptedPhrases.length];
        for (int i = 0; i < labelIndexes.length; i++)
        {
            labelIndexes[i] = i;
        }

        final ArrayList<LabelIndexWithCodes> phraseIndexesWithCodes = Lists
            .newArrayListWithCapacity(labelIndexes.length);
        for (int i = 0; i < labelIndexes.length; i++)
        {
            phraseIndexesWithCodes.add(new LabelIndexWithCodes(labelIndexes[i],
                getLabelWordIndexes(acceptedWords.length, context.allPhrases.wordIndices,
                    labelIndexes[i])));
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
            if (getLabelLength(acceptedWords.length, context.allPhrases.wordIndices,
                currentLabelIndex) == lcpArray[i]
                && isLabelAccepted(acceptedWords.length, currentLabelIndex,
                    acceptedWords, acceptedPhrases))
            {
                int j = i;
                while (j < sortedPhrasesWithCodes.size() - 1
                    && lcpArray[j] >= lcpArray[i])
                {
                    final LabelIndexWithCodes nextPhraseWithCodes = sortedPhrasesWithCodes
                        .get(j + 1);
                    final int nextLabelIndex = nextPhraseWithCodes.getLabelIndex();

                    double labelOverride = calculateLabelOverride(acceptedWords.length,
                        wordTf, phraseTf, nextLabelIndex, currentLabelIndex);
                    if ((isLabelAccepted(acceptedWords.length, currentLabelIndex,
                        acceptedWords, acceptedPhrases) && labelOverride >= labelOverrideThreshold))
                    {
                        markLabelAsRemoved(acceptedWords.length, currentLabelIndex,
                            acceptedWords, acceptedPhrases);
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

    private final static int [] getLabelWordIndexes(int wordCount, int [][] wordIndices,
        int featureIndex)
    {
        if (featureIndex < wordCount)
        {
            return new int []
            {
                featureIndex
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

    private final static boolean isLabelAccepted(int wordCount, int featureIndex,
        boolean [] acceptedWords, boolean [] acceptedPhrases)
    {
        if (featureIndex < wordCount)
        {
            return acceptedWords[featureIndex];
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
