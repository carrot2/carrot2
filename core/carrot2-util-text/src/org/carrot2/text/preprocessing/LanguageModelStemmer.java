package org.carrot2.text.preprocessing;

import java.util.*;

import org.carrot2.text.MutableCharArray;
import org.carrot2.text.linguistic.LanguageModel;
import org.carrot2.text.linguistic.Stemmer;
import org.carrot2.text.util.*;
import org.carrot2.util.CharArrayUtils;
import org.carrot2.util.CharSequenceUtils;

import bak.pcj.list.IntArrayList;
import bak.pcj.list.IntList;
import bak.pcj.set.IntBitSet;
import bak.pcj.set.IntSet;

/**
 * Implementation of {@link PreprocessingTasks#STEMMING} based on the current
 * {@link LanguageModel}.
 */
final class LanguageModelStemmer
{
    /**
     * Performs stemming and saves the results to the <code>context</code>.
     */
    void stem(PreprocessingContext context, LanguageModel language)
    {
        final MutableCharArray current = new MutableCharArray("");
        final Stemmer stemmer = language.getStemmer();

        final char [][] wordImages = context.allWords.image;
        final char [][] stemImages = new char [wordImages.length] [];

        for (int i = 0; i < wordImages.length; i++)
        {
            final char [] lowerCaseWord = CharArrayUtils.toLowerCase(wordImages[i]);
            current.reset(lowerCaseWord);
            final CharSequence stemmed = stemmer.stem(current);

            if (stemmed != null)
            {
                stemImages[i] = CharSequenceUtils.toCharArray(stemmed);
            }
            else
            {
                // We need to put the original word here, otherwise, we wouldn't be able
                // to compute frequencies for stems.
                stemImages[i] = lowerCaseWord;
            }
        }

        addStemStatistics(context, stemImages);
    }

    /**
     * Adds frequency statistics to the stems.
     */
    private void addStemStatistics(PreprocessingContext context, char [][] wordStemImages)
    {
        final int [] stemImagesOrder = IndirectSorter.sort(wordStemImages,
            CharArrayComparators.FAST_CHAR_ARRAY_COMPARATOR);

        // Local array references
        final int [] wordTfArray = context.allWords.tf;
        final int [][] wordTfByDocumentArray = context.allWords.tfByDocument;

        final int allWordsCount = wordTfArray.length;

        // Pointers from AllWords to AllStems
        final int [] stemIndexesArray = new int [allWordsCount];

        if (stemImagesOrder.length == 0)
        {
            context.allStems.images = new char [0] [];
            context.allStems.mostFrequentOriginalWordIndex = new int [0];
            context.allStems.tf = new int [0];
            context.allStems.tfByDocument = new int [0] [];

            context.allWords.stemIndex = new int [context.allWords.image.length];

            return;
        }

        // Lists to accommodate the results
        final List<char []> stemImages = new ArrayList<char []>(allWordsCount);
        final IntList stemTf = new IntArrayList(allWordsCount);
        final IntList stemMostFrequentWordIndexes = new IntArrayList(allWordsCount);
        final List<int []> stemTfByDocumentList = new ArrayList<int []>(allWordsCount);

        // Counters
        int totalTf = wordTfArray[stemImagesOrder[0]];
        int mostFrequentWordFrequency = wordTfArray[stemImagesOrder[0]];
        int mostFrequentWordIndex = stemImagesOrder[0];
        final IntSet originalWordIndexesSet = new IntBitSet(allWordsCount);
        originalWordIndexesSet.add(stemImagesOrder[0]);
        int stemIndex = 0;
        final int [] stemTfByDocument = new int [context.documents.size()];
        IntArrayUtils.addAllFromSparselyEncoded(stemTfByDocument,
            wordTfByDocumentArray[stemImagesOrder[0]]);

        // Go through all words in the order of stem images
        for (int i = 0; i < stemImagesOrder.length - 1; i++)
        {
            final char [] stem = wordStemImages[stemImagesOrder[i]];
            final int nextInOrderIndex = stemImagesOrder[i + 1];
            final char [] nextStem = wordStemImages[nextInOrderIndex];

            stemIndexesArray[stemImagesOrder[i]] = stemIndex;

            // Now check if token image is changing
            final boolean sameStem = CharArrayComparators.FAST_CHAR_ARRAY_COMPARATOR
                .compare(stem, nextStem) == 0;

            if (sameStem)
            {
                totalTf += wordTfArray[nextInOrderIndex];
                IntArrayUtils.addAllFromSparselyEncoded(stemTfByDocument,
                    wordTfByDocumentArray[nextInOrderIndex]);
                if (mostFrequentWordFrequency < wordTfArray[nextInOrderIndex])
                {
                    mostFrequentWordFrequency = wordTfArray[nextInOrderIndex];
                    mostFrequentWordIndex = nextInOrderIndex;
                }
                originalWordIndexesSet.add(nextInOrderIndex);
            }
            else
            {
                stemImages.add(stem);
                stemTf.add(totalTf);
                stemMostFrequentWordIndexes.add(mostFrequentWordIndex);
                stemTfByDocumentList
                    .add(IntArrayUtils.toSparseEncoding(stemTfByDocument));

                stemIndex++;
                totalTf = wordTfArray[nextInOrderIndex];
                mostFrequentWordFrequency = wordTfArray[nextInOrderIndex];
                mostFrequentWordIndex = nextInOrderIndex;
                originalWordIndexesSet.clear();
                originalWordIndexesSet.add(nextInOrderIndex);

                Arrays.fill(stemTfByDocument, 0);
                IntArrayUtils.addAllFromSparselyEncoded(stemTfByDocument,
                    wordTfByDocumentArray[nextInOrderIndex]);
            }
        }

        // Store tf for the last stem in the array
        stemImages.add(wordStemImages[stemImagesOrder[stemImagesOrder.length - 1]]);
        stemTf.add(totalTf);
        stemMostFrequentWordIndexes.add(mostFrequentWordIndex);
        stemIndexesArray[stemImagesOrder[stemImagesOrder.length - 1]] = stemIndex;
        stemTfByDocumentList.add(IntArrayUtils.toSparseEncoding(stemTfByDocument));

        // Convert lists to arrays and store them in allStems
        context.allStems.images = stemImages.toArray(new char [stemImages.size()] []);
        context.allStems.mostFrequentOriginalWordIndex = stemMostFrequentWordIndexes
            .toArray();
        context.allStems.tf = stemTf.toArray();
        context.allStems.tfByDocument = stemTfByDocumentList
            .toArray(new int [stemTfByDocumentList.size()] []);

        // References in allWords
        context.allWords.stemIndex = stemIndexesArray;
    }
}
