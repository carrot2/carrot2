
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

package org.carrot2.text.preprocessing;

import java.util.Arrays;
import java.util.List;

import org.carrot2.core.attribute.Processing;
import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.text.preprocessing.PreprocessingContext.AllTokens;
import org.carrot2.text.preprocessing.PreprocessingContext.AllWords;
import org.carrot2.text.util.CharArrayComparators;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.AttributeLevel;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.DefaultGroups;
import org.carrot2.util.attribute.Group;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Label;
import org.carrot2.util.attribute.Level;
import org.carrot2.util.attribute.constraint.IntRange;

import com.carrotsearch.hppc.BitSet;
import com.carrotsearch.hppc.ByteArrayList;
import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntStack;
import com.carrotsearch.hppc.ShortArrayList;
import com.carrotsearch.hppc.sorting.IndirectSort;
import org.carrot2.shaded.guava.common.collect.Lists;

/**
 * Performs case normalization and calculates a number of frequency statistics for words.
 * The aim of case normalization is to find the most frequently appearing variants of
 * words in terms of case. For example, if in the input documents <i>MacOS</i> appears 20
 * times, <i>Macos</i> 5 times and <i>macos</i> 2 times, case normalizer will select
 * <i>MacOS</i> to represent all variants and assign the aggregated term frequency of 27
 * to it.
 * <p>
 * This class saves the following results to the {@link PreprocessingContext}:
 * <ul>
 * <li>{@link AllTokens#wordIndex}</li>
 * <li>{@link AllWords#image}</li>
 * <li>{@link AllWords#tf}</li>
 * <li>{@link AllWords#tfByDocument}</li>
 * </ul>
 * <p>
 * This class requires that {@link Tokenizer} be invoked first.
 */
@Bindable(prefix = "CaseNormalizer")
public final class CaseNormalizer
{
    /**
     * Word Document Frequency threshold. Words appearing in fewer than
     * <code>dfThreshold</code> documents will be ignored.
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 1, max = 100)
    @Label("Word document frequency threshold")
    @Level(AttributeLevel.ADVANCED)
    @Group(DefaultGroups.PREPROCESSING)
    public int dfThreshold = 1;

    /**
     * Performs normalization and saves the results to the <code>context</code>.
     */
    public void normalize(PreprocessingContext context)
    {
        // Local references to already existing arrays
        final char [][] tokenImages = context.allTokens.image;
        final short [] tokenTypesArray = context.allTokens.type;
        final int [] documentIndexesArray = context.allTokens.documentIndex;
        final byte [] tokensFieldIndex = context.allTokens.fieldIndex;
        final int tokenCount = tokenImages.length;

        // Sort token images
        final int [] tokenImagesOrder = IndirectSort.mergesort(tokenImages, 0,
            tokenImages.length, CharArrayComparators.NORMALIZING_CHAR_ARRAY_COMPARATOR);

        // Create holders for new arrays
        final List<char []> normalizedWordImages = Lists.newArrayList();
        final IntArrayList normalizedWordTf = new IntArrayList();
        final List<int []> wordTfByDocumentList = Lists.newArrayList();
        final ByteArrayList fieldIndexList = new ByteArrayList();
        final ShortArrayList types = new ShortArrayList();

        final int [] wordIndexes = new int [tokenCount];
        Arrays.fill(wordIndexes, -1);

        // Initial values for counters
        int tf = 1;
        int maxTf = 1;
        int maxTfVariantIndex = tokenImagesOrder[0];
        int totalTf = 1;
        int variantStartIndex = 0;

        // A byte set for word fields tracking
        final BitSet fieldIndices = new BitSet(context.allFields.name.length);

        // A stack for pushing information about the term's documents.
        final IntStack wordDocuments = new IntStack();

        if (documentIndexesArray[tokenImagesOrder[0]] >= 0)
        {
            wordDocuments.push(documentIndexesArray[tokenImagesOrder[0]]);
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
            if (isNotIndexed(tokenType))
            {
                variantStartIndex = i + 1;
                maxTfVariantIndex = tokenImagesOrder[i + 1];

                resetForNewTokenImage(documentIndexesArray, tokenImagesOrder, 
                    fieldIndices, wordDocuments, i);
                continue;
            }

            fieldIndices.set(tokensFieldIndex[tokenImagesOrder[i]]);

            // Now check if image case is changing
            final boolean sameCase = CharArrayComparators.FAST_CHAR_ARRAY_COMPARATOR
                .compare(image, nextImage) == 0;
            if (sameCase)
            {
                // Case has not changed, just increase counters
                tf++;
                totalTf++;
                wordDocuments.push(documentIndex);
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
                wordDocuments.push(documentIndex);
            }
            else
            {
                // The image has changed completely.
                // Before we start processing the new image, we need to
                // see if we want to store the previous image, and if so
                // we need add some data about it to the arrays
                
                // wordDocuments.size() may contain duplicate entries from the same document, 
                // but this check is faster than deduping, so we do it first.  
                if (wordDocuments.size() >= dfThreshold)
                {
                    // Flatten the list of documents this term occurred in.
                    final int [] sparseEncoding = SparseArray.toSparseEncoding(wordDocuments);
                    final int df = (sparseEncoding.length >> 1); 
                    if (df >= dfThreshold)
                    {
                        wordTfByDocumentList.add(sparseEncoding);
    
                        // Add the word to the word list
                        normalizedWordImages.add(tokenImages[maxTfVariantIndex]);
                        types.add(tokenTypesArray[maxTfVariantIndex]);
                        normalizedWordTf.add(totalTf);
                        fieldIndexList.add((byte) fieldIndices.bits[0]);

                        // Add this word's index in AllWords to all its instances
                        // in the AllTokens multiarray
                        for (int j = variantStartIndex; j < i + 1; j++)
                        {
                            wordIndexes[tokenImagesOrder[j]] = normalizedWordImages.size() - 1;
                        }
                    }
                }

                // Reinitialize counters
                totalTf = 1;
                tf = 1;
                maxTf = 1;
                maxTfVariantIndex = tokenImagesOrder[i + 1];
                variantStartIndex = i + 1;

                // Re-initialize int set used for document frequency calculation
                resetForNewTokenImage(documentIndexesArray, tokenImagesOrder,
                    fieldIndices, wordDocuments, i);
            }
        }

        // Mapping from allTokens
        context.allTokens.wordIndex = wordIndexes;

        context.allWords.image = normalizedWordImages
            .toArray(new char [normalizedWordImages.size()] []);
        context.allWords.tf = normalizedWordTf.toArray();
        context.allWords.tfByDocument = 
            wordTfByDocumentList.toArray(new int [wordTfByDocumentList.size()] []);
        context.allWords.fieldIndices = fieldIndexList.toArray();
        context.allWords.type = types.toArray();
    }

    /**
     * Initializes the counters for the a token image.
     */
    private void resetForNewTokenImage(final int [] documentIndexesArray,
        final int [] tokenImagesOrder,
        final BitSet fieldIndices, IntStack wordDocuments, int i)
    {
        fieldIndices.clear();
        wordDocuments.clear();
        if (documentIndexesArray[tokenImagesOrder[i + 1]] >= 0)
        {
            wordDocuments.push(documentIndexesArray[tokenImagesOrder[i + 1]]);
        }
    }

    /**
     * Determines whether we should include the token in AllWords.
     */
    private boolean isNotIndexed(final int tokenType)
    {
        return tokenType == ITokenizer.TT_PUNCTUATION
            || tokenType == ITokenizer.TT_FULL_URL
            || (tokenType & ITokenizer.TF_SEPARATOR_SENTENCE) != 0;
    }
}
