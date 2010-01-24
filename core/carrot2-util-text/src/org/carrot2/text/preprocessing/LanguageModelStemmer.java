
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.preprocessing;

import java.util.*;

import org.carrot2.text.linguistic.IStemmer;
import org.carrot2.text.preprocessing.PreprocessingContext.AllStems;
import org.carrot2.text.preprocessing.PreprocessingContext.AllWords;
import org.carrot2.text.util.CharArrayComparators;
import org.carrot2.text.util.MutableCharArray;
import org.carrot2.util.*;
import org.carrot2.util.attribute.Bindable;

import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.BitSet;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Applies stemming to words and calculates a number of frequency statistics for stems.
 * <p>
 * This class saves the following results to the {@link PreprocessingContext}:
 * <ul>
 * <li>{@link AllWords#stemIndex}</li>
 * <li>{@link AllStems#image}</li>
 * <li>{@link AllStems#mostFrequentOriginalWordIndex}</li>
 * <li>{@link AllStems#tf}</li>
 * <li>{@link AllStems#tfByDocument}</li>
 * <li>{@link AllWords#FLAG_QUERY} in This class requires that {@link Tokenizer}
 * and {@link CaseNormalizer} be invoked first.
 */
@Bindable(prefix = "LanguageModelStemmer")
public final class LanguageModelStemmer
{
    /**
     * Performs stemming and saves the results to the <code>context</code>.
     */
    public void stem(PreprocessingContext context)
    {
        final IStemmer stemmer = context.language.getStemmer();

        final char [][] wordImages = context.allWords.image;
        final char [][] stemImages = new char [wordImages.length] [];

        final MutableCharArray mutableCharArray = new MutableCharArray("");
        char [] buffer = new char [128];

        for (int i = 0; i < wordImages.length; i++)
        {
            final char [] word = wordImages[i];
            if (buffer.length < word.length) buffer = new char [word.length];

            final boolean different = CharArrayUtils.toLowerCase(word, buffer);

            mutableCharArray.reset(buffer, 0, word.length);
            final CharSequence stemmed = stemmer.stem(mutableCharArray);
            if (stemmed != null)
            {
                stemImages[i] = CharSequenceUtils.toCharArray(stemmed);
            }
            else
            {
                // We need to put the original word here, otherwise, we wouldn't be able
                // to compute frequencies for stems.
                if (different)
                    stemImages[i] = CharArrayUtils.copyOf(buffer, 0, word.length);
                else
                    stemImages[i] = word;
            }
        }

        addStemStatistics(context, stemImages, prepareQueryWords(context.query, stemmer));
    }

    /**
     * Adds frequency statistics to the stems.
     */
    private void addStemStatistics(PreprocessingContext context,
        char [][] wordStemImages, Set<MutableCharArray> queryStems)
    {
        final int [] stemImagesOrder = IndirectSort.sort(wordStemImages, 0, wordStemImages.length,
            CharArrayComparators.FAST_CHAR_ARRAY_COMPARATOR);

        // Local array references
        final int [] wordTfArray = context.allWords.tf;
        final int [][] wordTfByDocumentArray = context.allWords.tfByDocument;
        final byte [][] wordsFieldIndices = context.allWords.fieldIndices;
        final int [] wordsFlag = context.allWords.flag;

        final int allWordsCount = wordTfArray.length;

        // Pointers from AllWords to AllStems
        final int [] stemIndexesArray = new int [allWordsCount];

        if (stemImagesOrder.length == 0)
        {
            context.allStems.image = new char [0] [];
            context.allStems.mostFrequentOriginalWordIndex = new int [0];
            context.allStems.tf = new int [0];
            context.allStems.tfByDocument = new int [0] [];
            context.allStems.fieldIndices = new byte [0] [];

            context.allWords.stemIndex = new int [context.allWords.image.length];
            return;
        }

        // Lists to accommodate the results
        final List<char []> stemImages = new ArrayList<char []>(allWordsCount);
        final IntArrayList stemTf = new IntArrayList(allWordsCount);
        final IntArrayList stemMostFrequentWordIndexes = new IntArrayList(allWordsCount);
        final List<int []> stemTfByDocumentList = new ArrayList<int []>(allWordsCount);
        final List<byte []> fieldIndexList = Lists.newArrayList();

        // Counters
        int totalTf = wordTfArray[stemImagesOrder[0]];
        int mostFrequentWordFrequency = wordTfArray[stemImagesOrder[0]];
        int mostFrequentWordIndex = stemImagesOrder[0];
        final BitSet originalWordIndexesSet = new BitSet(allWordsCount);
        originalWordIndexesSet.set(stemImagesOrder[0]);
        int stemIndex = 0;
        final int [] stemTfByDocument = new int [context.documents.size()];
        IntArrayUtils.addAllFromSparselyEncoded(stemTfByDocument,
            wordTfByDocumentArray[stemImagesOrder[0]]);
        final BitSet fieldIndices = new BitSet(context.allFields.name.length);
        addAll(fieldIndices, wordsFieldIndices[0]);

        // For locating query words
        final MutableCharArray buffer = new MutableCharArray(
            wordStemImages[stemImagesOrder[0]]);
        boolean inQuery = queryStems.contains(buffer);

        // Go through all words in the order of stem images
        for (int i = 0; i < stemImagesOrder.length - 1; i++)
        {
            final int orderIndex = stemImagesOrder[i];
            final char [] stem = wordStemImages[orderIndex];
            final int nextInOrderIndex = stemImagesOrder[i + 1];
            final char [] nextStem = wordStemImages[nextInOrderIndex];

            stemIndexesArray[orderIndex] = stemIndex;
            if (inQuery)
            {
                wordsFlag[orderIndex] |= AllWords.FLAG_QUERY;
            }

            // Now check if token image is changing
            final boolean sameStem = CharArrayComparators.FAST_CHAR_ARRAY_COMPARATOR
                .compare(stem, nextStem) == 0;

            if (sameStem)
            {
                totalTf += wordTfArray[nextInOrderIndex];
                IntArrayUtils.addAllFromSparselyEncoded(stemTfByDocument,
                    wordTfByDocumentArray[nextInOrderIndex]);
                addAll(fieldIndices, wordsFieldIndices[nextInOrderIndex]);
                if (mostFrequentWordFrequency < wordTfArray[nextInOrderIndex])
                {
                    mostFrequentWordFrequency = wordTfArray[nextInOrderIndex];
                    mostFrequentWordIndex = nextInOrderIndex;
                }
                originalWordIndexesSet.set(nextInOrderIndex);
            }
            else
            {
                stemImages.add(stem);
                stemTf.add(totalTf);
                stemMostFrequentWordIndexes.add(mostFrequentWordIndex);
                stemTfByDocumentList
                    .add(IntArrayUtils.toSparseEncoding(stemTfByDocument));
                fieldIndexList.add(PcjCompat.toByteArray(fieldIndices));

                stemIndex++;
                totalTf = wordTfArray[nextInOrderIndex];
                mostFrequentWordFrequency = wordTfArray[nextInOrderIndex];
                mostFrequentWordIndex = nextInOrderIndex;
                originalWordIndexesSet.clear();
                originalWordIndexesSet.set(nextInOrderIndex);
                fieldIndices.clear();
                addAll(fieldIndices, wordsFieldIndices[nextInOrderIndex]);

                Arrays.fill(stemTfByDocument, 0);
                IntArrayUtils.addAllFromSparselyEncoded(stemTfByDocument,
                    wordTfByDocumentArray[nextInOrderIndex]);

                buffer.reset(wordStemImages[nextInOrderIndex]);
                inQuery = queryStems.contains(buffer);
            }
        }

        // Store tf for the last stem in the array
        stemImages.add(wordStemImages[stemImagesOrder[stemImagesOrder.length - 1]]);
        stemTf.add(totalTf);
        stemMostFrequentWordIndexes.add(mostFrequentWordIndex);
        stemIndexesArray[stemImagesOrder[stemImagesOrder.length - 1]] = stemIndex;
        stemTfByDocumentList.add(IntArrayUtils.toSparseEncoding(stemTfByDocument));
        fieldIndexList.add(PcjCompat.toByteArray(fieldIndices));
        if (inQuery)
        {
            wordsFlag[stemImagesOrder[stemImagesOrder.length - 1]] |= AllWords.FLAG_QUERY;
        }

        // Convert lists to arrays and store them in allStems
        context.allStems.image = stemImages.toArray(new char [stemImages.size()] []);
        context.allStems.mostFrequentOriginalWordIndex = stemMostFrequentWordIndexes
            .toArray();
        context.allStems.tf = stemTf.toArray();
        context.allStems.tfByDocument = stemTfByDocumentList
            .toArray(new int [stemTfByDocumentList.size()] []);
        context.allStems.fieldIndices = fieldIndexList.toArray(new byte [fieldIndexList
            .size()] []);

        // References in allWords
        context.allWords.stemIndex = stemIndexesArray;
    }

    private final static void addAll(BitSet set, byte [] values)
    {
        for (byte b : values)
        {
            set.set(b);
        }
    }

    private Set<MutableCharArray> prepareQueryWords(String query, IStemmer stemmer)
    {
        final Set<MutableCharArray> queryWords = Sets.newHashSet();

        if (query != null)
        {
            final String [] split = query.toLowerCase().split("\\s");
            for (int i = 0; i < split.length; i++)
            {
                final CharSequence stem = stemmer.stem(split[i]);
                if (stem != null)
                {
                    queryWords.add(new MutableCharArray(stem));
                }
                else
                {
                    queryWords.add(new MutableCharArray(split[i]));
                }
            }
        }

        return queryWords;
    }
}
