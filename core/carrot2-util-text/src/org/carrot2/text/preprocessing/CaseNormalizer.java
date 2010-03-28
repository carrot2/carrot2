/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
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
import org.carrot2.text.analysis.ITokenType;
import org.carrot2.text.preprocessing.PreprocessingContext.AllTokens;
import org.carrot2.text.preprocessing.PreprocessingContext.AllWords;
import org.carrot2.text.util.CharArrayComparators;
import org.carrot2.util.PcjCompat;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.constraint.IntRange;

import com.carrotsearch.hppc.BitSet;
import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntIntOpenHashMap;
import com.carrotsearch.hppc.IntStack;
import com.carrotsearch.hppc.cursors.IntIntCursor;
import com.carrotsearch.hppc.sorting.IndirectSort;
import com.google.common.collect.Lists;

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
     * An empty <code>int []</code>. 
     */
    private static final int [] EMPTY_INT_ARRAY = new int [0];

    /**
     * Word Document Frequency threshold. Words appearing in fewer than
     * <code>dfThreshold</code> documents will be ignored.
     * 
     * @level Advanced
     * @group Preprocessing
     * @label Word Document Frequency threshold
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 1, max = 100)
    public int dfThreshold = 1;

    /**
     * Performs normalization and saves the results to the <code>context</code>.
     */
    public void normalize(PreprocessingContext context)
    {
        // Local references to already existing arrays
        final char [][] tokenImages = context.allTokens.image;
        final int [] tokenTypesArray = context.allTokens.type;
        final int [] documentIndexesArray = context.allTokens.documentIndex;
        final byte [] tokensFieldIndex = context.allTokens.fieldIndex;
        final int tokenCount = tokenImages.length;
        final int documentCount = context.documents.size();

        // Sort token images
        final int [] tokenImagesOrder = IndirectSort.sort(tokenImages, 0,
            tokenImages.length, CharArrayComparators.NORMALIZING_CHAR_ARRAY_COMPARATOR);

        // Create holders for new arrays
        final List<char []> normalizedWordImages = Lists.newArrayList();
        final IntArrayList normalizedWordTf = new IntArrayList();
        final List<int []> wordTfByDocumentList = Lists.newArrayList();
        final List<byte []> fieldIndexList = Lists.newArrayList();
        final IntArrayList types = new IntArrayList();

        final int [] wordIndexes = new int [tokenCount];
        Arrays.fill(wordIndexes, -1);

        // Initial values for counters
        int tf = 1;
        int maxTf = 1;
        int maxTfVariantIndex = tokenImagesOrder[0];
        int totalTf = 1;
        int variantStartIndex = 0;

        // An int set for document frequency calculation
        final BitSet documentIndices = new BitSet(documentCount);

        // A byte set for word fields tracking
        final BitSet fieldIndices = new BitSet(context.allFields.name.length);

        // A stack for pushing information about the term's documents.
        final IntStack wordDocuments = new IntStack();

        if (documentIndexesArray[tokenImagesOrder[0]] >= 0)
        {
            documentIndices.set(documentIndexesArray[tokenImagesOrder[0]]);
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
                    documentIndices, fieldIndices, wordDocuments, i);
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

                documentIndices.set(documentIndex);
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
                documentIndices.set(documentIndex);
                wordDocuments.push(documentIndex);
            }
            else
            {
                // The image has changed completely.
                // Before we start processing the new image, we need to
                // see if we want to store the previous image, and if so
                // we need add some data about it to the arrays
                int wordDf = (int) documentIndices.cardinality();
                if (wordDf >= dfThreshold)
                {
                    // Add the word to the word list
                    normalizedWordImages.add(tokenImages[maxTfVariantIndex]);
                    normalizedWordTf.add(totalTf);
                    fieldIndexList.add(PcjCompat.toByteArray(fieldIndices));
                    types.add(tokenType);

                    // Add this word's index in AllWords to all its instances
                    // in the AllTokens multiarray
                    for (int j = variantStartIndex; j < i + 1; j++)
                    {
                        wordIndexes[tokenImagesOrder[j]] = normalizedWordImages.size() - 1;
                    }

                    // Flatten the wordTfByDocument map and add to the list
                    int [] sparseEncoding = toSparseEncoding(wordDocuments);
                    wordTfByDocumentList.add(sparseEncoding);
                }

                // Reinitialize counters
                totalTf = 1;
                tf = 1;
                maxTf = 1;
                maxTfVariantIndex = tokenImagesOrder[i + 1];
                variantStartIndex = i + 1;

                // Re-initialize int set used for document frequency calculation
                resetForNewTokenImage(documentIndexesArray, tokenImagesOrder,
                    documentIndices, fieldIndices, wordDocuments, i);
            }
        }

        // Mapping from allTokens
        context.allTokens.wordIndex = wordIndexes;

        context.allWords.image = normalizedWordImages
            .toArray(new char [normalizedWordImages.size()] []);
        context.allWords.tf = normalizedWordTf.toArray();
        context.allWords.tfByDocument = 
            wordTfByDocumentList.toArray(new int [wordTfByDocumentList.size()] []);
        context.allWords.fieldIndices = fieldIndexList.toArray(
            new byte [fieldIndexList.size()] []);
        context.allWords.type = types.toArray();
        context.allWords.flag = new int [types.size()];
    }

    /**
     * Convert a list of documents to sparse document-count representation.
     */
    private int [] toSparseEncoding(IntStack documents)
    {
        if (documents.size() == 0)
            return EMPTY_INT_ARRAY;

        // For smaller arrays, count using sorting.
        if (documents.size() < 1000)
        {
            return toSparseEncodingBySort(documents);
        }
        else
        {
            return toSparseEncodingByHash(documents);
        }
    }

    /**
     * Convert to sparse encoding using a hash map.
     */
    private int [] toSparseEncodingByHash(IntStack documents)
    {
        final IntIntOpenHashMap map = new IntIntOpenHashMap();

        final int toIndex = documents.size();
        final int [] buffer = documents.buffer;
        for (int i = 0; i < toIndex; i++)
        {
            map.putOrAdd(buffer[i], 1, 1);
        }

        final int [] result = new int [map.size() * 2];
        int k = 0;
        for (IntIntCursor c : map)
        {
            result[k++] = c.key;
            result[k++] = c.value;
        }
        return result;
    }

    /**
     * Convert to sparse encoding using sorting and counting.
     */
    private int [] toSparseEncodingBySort(IntStack documents)
    {
        Arrays.sort(documents.buffer, 0, documents.size());
        final int [] result = new int [2 * countUnique(documents.buffer, 0, documents.size())];

        final int fromIndex = 0;
        final int toIndex = documents.size();
        final int [] buffer = documents.buffer;

        int doc = buffer[fromIndex];
        int count = 1;
        int k = 0;
        for (int i = fromIndex + 1; i < toIndex; i++)
        {
            final int newDoc = buffer[i];
            if (newDoc != doc)
            {
                result[k++] = doc;
                result[k++] = count;
                count = 0;
                doc = newDoc;
            }
            count++;
        }
        if (k < result.length)
        {
            result[k++] = doc;
            result[k++] = count;
        }
        assert k == result.length;
        return result;
    }

    /**
     * Count unique values in the sorted array.
     */
    private int countUnique(int [] buffer, int fromIndex, int toIndex)
    {
        int unique = 0;
        if (fromIndex < toIndex)
        {
            int val = buffer[fromIndex];
            unique++;
            for (int i = fromIndex + 1; i < toIndex; i++)
            {
                final int j = buffer[i];
                assert j >= val : "Not sorted as expected.";
                if (val != j)
                {
                    unique++;
                    val = j;
                }
            }
        }
        return unique;
    }

    /**
     * Initializes the counters for the a token image.
     */
    private void resetForNewTokenImage(final int [] documentIndexesArray,
        final int [] tokenImagesOrder, final BitSet documentIndices,
        final BitSet fieldIndices, IntStack wordDocuments, int i)
    {
        documentIndices.clear();
        fieldIndices.clear();
        wordDocuments.clear();
        if (documentIndexesArray[tokenImagesOrder[i + 1]] >= 0)
        {
            documentIndices.set(documentIndexesArray[tokenImagesOrder[i + 1]]);
            wordDocuments.push(documentIndexesArray[tokenImagesOrder[i + 1]]);
        }
    }

    /**
     * Determines whether we should include the token in AllWords.
     */
    private boolean isNotIndexed(final int tokenType)
    {
        return tokenType == ITokenType.TT_PUNCTUATION
            || tokenType == ITokenType.TT_FULL_URL
            || (tokenType & ITokenType.TF_SEPARATOR_SENTENCE) != 0;
    }
}
