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
package org.carrot2.text.preprocessing;

import com.carrotsearch.hppc.ByteArrayList;
import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.sorting.IndirectSort;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import org.carrot2.language.Stemmer;
import org.carrot2.language.Tokenizer;
import org.carrot2.text.preprocessing.PreprocessingContext.AllStems;
import org.carrot2.text.preprocessing.PreprocessingContext.AllWords;
import org.carrot2.util.CharArrayComparators;
import org.carrot2.util.CharArrayUtils;
import org.carrot2.util.MutableCharArray;

/**
 * Applies stemming to words and calculates a number of frequency statistics for stems.
 *
 * <p>This class saves the following results to the {@link PreprocessingContext}:
 *
 * <ul>
 *   <li>{@link AllWords#stemIndex}
 *   <li>{@link AllStems#image}
 *   <li>{@link AllStems#mostFrequentOriginalWordIndex}
 *   <li>{@link AllStems#tf}
 *   <li>{@link AllStems#tfByDocument}
 *   <li>{@link AllWords#type} is populated with {@link Tokenizer#TF_QUERY_WORD}
 * </ul>
 *
 * <p>This class requires that {@link InputTokenizer} and {@link CaseNormalizer} be invoked first.
 */
final class LanguageModelStemmer {
  /** Performs stemming and saves the results to the <code>context</code>. */
  public void stem(PreprocessingContext context, String queryHint) {
    final Stemmer stemmer = context.languageComponents.get(Stemmer.class);

    final char[][] wordImages = context.allWords.image;
    final char[][] stemImages = new char[wordImages.length][];

    final MutableCharArray mutableCharArray = new MutableCharArray(CharArrayUtils.EMPTY_ARRAY);
    char[] buffer = new char[128];

    for (int i = 0; i < wordImages.length; i++) {
      final char[] word = wordImages[i];
      if (buffer.length < word.length) buffer = new char[word.length];

      final boolean different = CharArrayUtils.toLowerCase(word, buffer);

      mutableCharArray.reset(buffer, 0, word.length);
      final CharSequence stemmed = stemmer.stem(mutableCharArray);
      if (stemmed != null) {
        mutableCharArray.reset(stemmed);
        stemImages[i] = context.intern(mutableCharArray);
      } else {
        // We need to put the original word here, otherwise, we wouldn't be able
        // to compute frequencies for stems.
        if (different) stemImages[i] = context.intern(mutableCharArray);
        else stemImages[i] = word;
      }
    }

    addStemStatistics(context, stemImages, prepareQueryWords(queryHint, stemmer));
  }

  /** Adds frequency statistics to the stems. */
  private void addStemStatistics(
      PreprocessingContext context, char[][] wordStemImages, Set<MutableCharArray> queryStems) {
    final int[] stemImagesOrder =
        IndirectSort.mergesort(
            wordStemImages,
            0,
            wordStemImages.length,
            CharArrayComparators.FAST_CHAR_ARRAY_COMPARATOR);

    // Local array references
    final int[] wordTfArray = context.allWords.tf;
    final int[][] wordTfByDocumentArray = context.allWords.tfByDocument;
    final byte[] wordsFieldIndices = context.allWords.fieldIndices;
    final short[] wordsType = context.allWords.type;

    final int allWordsCount = wordTfArray.length;

    // Pointers from AllWords to AllStems
    final int[] stemIndexesArray = new int[allWordsCount];

    if (stemImagesOrder.length == 0) {
      context.allStems.image = new char[0][];
      context.allStems.mostFrequentOriginalWordIndex = new int[0];
      context.allStems.tf = new int[0];
      context.allStems.tfByDocument = new int[0][];
      context.allStems.fieldIndices = new byte[0];

      context.allWords.stemIndex = new int[context.allWords.image.length];
      return;
    }

    // Lists to accommodate the results
    final ArrayList<char[]> stemImages = new ArrayList<>(allWordsCount);
    final IntArrayList stemTf = new IntArrayList(allWordsCount);
    final IntArrayList stemMostFrequentWordIndexes = new IntArrayList(allWordsCount);
    final ArrayList<int[]> stemTfByDocumentList = new ArrayList<>(allWordsCount);
    final ByteArrayList fieldIndexList = new ByteArrayList();

    // Counters
    int totalTf = wordTfArray[stemImagesOrder[0]];
    int mostFrequentWordFrequency = wordTfArray[stemImagesOrder[0]];
    int mostFrequentWordIndex = stemImagesOrder[0];
    int stemIndex = 0;

    // A list of document-term-frequency pairs, by document, for all words with identical stems.
    final ArrayList<int[]> stemTfsByDocument = new ArrayList<>();

    stemTfsByDocument.add(wordTfByDocumentArray[stemImagesOrder[0]]);
    byte fieldIndices = 0;
    fieldIndices |= wordsFieldIndices[0];

    // For locating query words
    final MutableCharArray buffer = new MutableCharArray(wordStemImages[stemImagesOrder[0]]);
    boolean inQuery = queryStems.contains(buffer);

    // Go through all words in the order of stem images
    for (int i = 0; i < stemImagesOrder.length - 1; i++) {
      final int orderIndex = stemImagesOrder[i];
      final char[] stem = wordStemImages[orderIndex];
      final int nextInOrderIndex = stemImagesOrder[i + 1];
      final char[] nextStem = wordStemImages[nextInOrderIndex];

      stemIndexesArray[orderIndex] = stemIndex;
      if (inQuery) {
        wordsType[orderIndex] |= Tokenizer.TF_QUERY_WORD;
      }

      // Now check if token image is changing
      final boolean sameStem =
          CharArrayComparators.FAST_CHAR_ARRAY_COMPARATOR.compare(stem, nextStem) == 0;

      if (sameStem) {
        totalTf += wordTfArray[nextInOrderIndex];
        stemTfsByDocument.add(wordTfByDocumentArray[nextInOrderIndex]);
        fieldIndices |= wordsFieldIndices[nextInOrderIndex];
        if (mostFrequentWordFrequency < wordTfArray[nextInOrderIndex]) {
          mostFrequentWordFrequency = wordTfArray[nextInOrderIndex];
          mostFrequentWordIndex = nextInOrderIndex;
        }
      } else {
        stemImages.add(stem);
        stemTf.add(totalTf);
        stemMostFrequentWordIndexes.add(mostFrequentWordIndex);
        storeTfByDocument(stemTfByDocumentList, stemTfsByDocument);
        fieldIndexList.add(fieldIndices);

        stemIndex++;
        totalTf = wordTfArray[nextInOrderIndex];
        mostFrequentWordFrequency = wordTfArray[nextInOrderIndex];
        mostFrequentWordIndex = nextInOrderIndex;
        fieldIndices = 0;
        fieldIndices |= wordsFieldIndices[nextInOrderIndex];

        stemTfsByDocument.clear();
        stemTfsByDocument.add(wordTfByDocumentArray[nextInOrderIndex]);

        buffer.reset(wordStemImages[nextInOrderIndex]);
        inQuery = queryStems.contains(buffer);
      }
    }

    // Store tf for the last stem in the array
    stemImages.add(wordStemImages[stemImagesOrder[stemImagesOrder.length - 1]]);
    stemTf.add(totalTf);
    stemMostFrequentWordIndexes.add(mostFrequentWordIndex);
    stemIndexesArray[stemImagesOrder[stemImagesOrder.length - 1]] = stemIndex;
    storeTfByDocument(stemTfByDocumentList, stemTfsByDocument);
    fieldIndexList.add(fieldIndices);
    if (inQuery) {
      wordsType[stemImagesOrder[stemImagesOrder.length - 1]] |= Tokenizer.TF_QUERY_WORD;
    }

    // Convert lists to arrays and store them in allStems
    context.allStems.image = stemImages.toArray(new char[stemImages.size()][]);
    context.allStems.mostFrequentOriginalWordIndex = stemMostFrequentWordIndexes.toArray();
    context.allStems.tf = stemTf.toArray();
    context.allStems.tfByDocument =
        stemTfByDocumentList.toArray(new int[stemTfByDocumentList.size()][]);
    context.allStems.fieldIndices = fieldIndexList.toArray();

    // References in allWords
    context.allWords.stemIndex = stemIndexesArray;
  }

  /** */
  private void storeTfByDocument(ArrayList<int[]> target, ArrayList<int[]> source) {
    assert source.size() > 0 : "Empty source document list?";

    if (source.size() == 1) {
      // Just copy the reference over if a single list is available.
      target.add(source.get(0));
    } else {
      // Merge sparse representations if more than one.
      target.add(SparseArray.mergeSparseArrays(source));
    }
  }

  private Set<MutableCharArray> prepareQueryWords(String query, Stemmer stemmer) {
    final Set<MutableCharArray> queryWords = new HashSet<>();

    if (query != null) {
      final String[] split = query.toLowerCase(Locale.ROOT).split("\\s");
      for (int i = 0; i < split.length; i++) {
        final CharSequence stem = stemmer.stem(split[i]);
        if (stem != null) {
          queryWords.add(new MutableCharArray(stem));
        } else {
          queryWords.add(new MutableCharArray(split[i]));
        }
      }
    }

    return queryWords;
  }
}
