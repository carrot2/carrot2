package org.carrot2.text.preprocessing;

import java.util.Arrays;
import java.util.List;

import org.carrot2.core.attribute.Processing;
import org.carrot2.text.analysis.TokenType;
import org.carrot2.text.util.*;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.IntRange;

import bak.pcj.list.IntArrayList;
import bak.pcj.list.IntList;
import bak.pcj.set.IntBitSet;
import bak.pcj.set.IntSet;

import com.google.common.collect.Lists;

/**
 * Performs case normalization and calculates a number of frequency statistics for words.
 */
@Bindable
public final class CaseNormalizer
{
    /**
     * Word Document Frequency cut-off. Words appearing in less than <code>dfCutoff</code>
     * documents will be ignored.
     * 
     * @level Advanced
     * @group Preprocessing
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 1, max = 100)
    public int dfCutoff = 1;

    /**
     * Performs normalization and saves the results to the <code>context</code>.
     */
    public void normalize(PreprocessingContext context)
    {
        // Local references to already existing arrays
        final char [][] tokenImages = context.allTokens.image;
        final int [] tokenTypesArray = context.allTokens.type;
        final int [] documentIndexesArray = context.allTokens.documentIndex;
        final int tokenCount = tokenImages.length;
        final int documentCount = context.documents.size();

        // Sort token images
        final int [] tokenImagesOrder = IndirectSorter.sort(tokenImages,
            CharArrayComparators.NORMALIZING_CHAR_ARRAY_COMPARATOR);

        // Create holders for new arrays
        final List<char []> normalizedWordImages = Lists.newArrayList();
        final IntList normalizedWordTf = new IntArrayList();
        final List<int []> wordTfByDocumentList = Lists.newArrayList();

        final int [] wordIndexes = new int [tokenCount];
        Arrays.fill(wordIndexes, -1);

        // Initial values for counters
        int tf = 1;
        int maxTf = 1;
        int maxTfVariantIndex = tokenImagesOrder[0];
        int totalTf = 1;
        int variantStartIndex = 0;

        // An int set for document frequency calculation
        IntSet documentIndexes = new IntBitSet(documentCount);

        // An array for tracking words' tf across documents
        int [] wordTfByDocument = new int [documentCount];

        if (documentIndexesArray[tokenImagesOrder[0]] >= 0)
        {
            documentIndexes.add(documentIndexesArray[tokenImagesOrder[0]]);
            wordTfByDocument[documentIndexesArray[tokenImagesOrder[0]]] = 1;
        }

        // Go through the ordered token images
        for (int i = 0; i < tokenImagesOrder.length - 1; i++)
        {
            final char [] image = tokenImages[tokenImagesOrder[i]];
            final char [] nextImage = tokenImages[tokenImagesOrder[i + 1]];
            final int tokenType = tokenTypesArray[tokenImagesOrder[i]];
            final int documentIndex = documentIndexesArray[tokenImagesOrder[i + 1]];

            // Reached the end of non-null tokens?
            if (image == null)
            {
                break;
            }

            // Check if we want to index this token at all
            if (tokenType == TokenType.TT_PUNCTUATION
                || (tokenType & TokenType.TF_SEPARATOR_SENTENCE) != 0)
            {
                variantStartIndex = i + 1;
                maxTfVariantIndex = tokenImagesOrder[i + 1];
                continue;
            }

            // Now check if image case is changing
            final boolean sameCase = CharArrayComparators.FAST_CHAR_ARRAY_COMPARATOR
                .compare(image, nextImage) == 0;
            if (sameCase)
            {
                // Case has not changed, just increase counters
                tf++;
                totalTf++;

                documentIndexes.add(documentIndex);
                wordTfByDocument[documentIndex] += 1;
                continue;
            }

            // Case (or even token image) has changed. Update most frequent case
            // variant
            if (maxTf < tf)
            {
                maxTf = tf;
                maxTfVariantIndex = tokenImagesOrder[i];
                tf = 1;
            }

            final boolean sameImage = CharArrayComparators.CASE_INSENSITIVE_CHAR_ARRAY_COMPARATOR
                .compare(image, nextImage) == 0;

            // Check if token image has changed
            if (sameImage)
            {
                totalTf++;
                documentIndexes.add(documentIndex);
                wordTfByDocument[documentIndex] += 1;
            }
            else
            {
                // The image has changed completely.
                // Before we start processing the new image, we need to
                // see if we want to store the previous image, and if so
                // we need add some data about it to the arrays
                int wordDf = documentIndexes.size();
                if (wordDf >= dfCutoff)
                {
                    // Add the word to the word list
                    normalizedWordImages.add(tokenImages[maxTfVariantIndex]);
                    normalizedWordTf.add(totalTf);

                    // Add this word's index in AllWords to all its instances
                    // in the AllTokens multiarray
                    for (int j = variantStartIndex; j < i + 1; j++)
                    {
                        wordIndexes[tokenImagesOrder[j]] = normalizedWordImages.size() - 1;
                    }

                    // Flatten the wordTfByDocument map and add to the list
                    wordTfByDocumentList.add(IntArrayUtils
                        .toSparseEncoding(wordTfByDocument));
                }

                // Reinitialize counters
                totalTf = 1;
                tf = 1;
                maxTf = 1;
                maxTfVariantIndex = tokenImagesOrder[i + 1];
                variantStartIndex = i + 1;

                // Re-initialize int set used for document frequency calculation
                documentIndexes.clear();
                Arrays.fill(wordTfByDocument, 0);
                if (documentIndexesArray[tokenImagesOrder[i + 1]] >= 0)
                {
                    documentIndexes.add(documentIndexesArray[tokenImagesOrder[i + 1]]);
                    wordTfByDocument[documentIndexesArray[tokenImagesOrder[i + 1]]] += 1;
                }
            }
        }

        // Mapping from allTokens
        context.allTokens.wordIndex = wordIndexes;

        context.allWords.image = normalizedWordImages
            .toArray(new char [normalizedWordImages.size()] []);
        context.allWords.tf = normalizedWordTf.toArray();
        context.allWords.tfByDocument = wordTfByDocumentList
            .toArray(new int [wordTfByDocumentList.size()] []);
    }
}
