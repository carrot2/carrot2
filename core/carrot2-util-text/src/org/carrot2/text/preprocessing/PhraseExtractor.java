
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

import java.util.Collections;
import java.util.List;

import org.carrot2.core.attribute.Processing;
import org.carrot2.text.preprocessing.PreprocessingContext.AllPhrases;
import org.carrot2.text.preprocessing.PreprocessingContext.AllTokens;
import org.carrot2.util.IntMapUtils;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.IntRange;

import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntIntHashMap;
import com.carrotsearch.hppc.cursors.IntIntCursor;
import org.carrot2.shaded.guava.common.collect.Lists;

/**
 * Extracts frequent phrases from the provided document. A frequent phrase is a sequence
 * of words that appears in the documents more than once. This phrase extractor aggregates
 * different inflection variants of phrase words into one phrase, returning the most
 * frequent variant. For example, if phrase <i>computing science</i> appears 2 times and
 * <i>computer sciences</i> appears 4 times, the latter will be returned with aggregated
 * frequency of 6.
 * <p>
 * This class saves the following results to the {@link PreprocessingContext}:
 * <ul>
 * <li>{@link AllPhrases#wordIndices}</li>
 * <li>{@link AllPhrases#tf}</li>
 * <li>{@link AllPhrases#tfByDocument}</li>
 * <li>{@link AllTokens#suffixOrder}</li>
 * <li>{@link AllTokens#lcp}</li>
 * </ul>
 * <p>
 * This class requires that {@link Tokenizer}, {@link CaseNormalizer} and
 * {@link LanguageModelStemmer} be invoked first.
 */
@Bindable(prefix = "PhraseExtractor")
public class PhraseExtractor
{
    /** Internal minimum phrase length, we may want to make it an attribute at some point */
    private static final int MIN_PHRASE_LENGTH = 2;

    /** Internal maximum phrase length, we may want to make it an attribute at some point */
    static final int MAX_PHRASE_LENGTH = 8;

    /**
     * Phrase Document Frequency threshold. Phrases appearing in fewer than
     * <code>dfThreshold</code> documents will be ignored.
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 1, max = 100)
    @Label("Phrase document frequency threshold")
    @Level(AttributeLevel.ADVANCED)
    @Group(DefaultGroups.PHRASE_EXTRACTION)
    public int dfThreshold = 1;

    /**
     * Suffix sorter to be used by this phrase extractor. When the suffix sorter gets some
     * attributes, we'll need to make this field public.
     */
    private SuffixSorter suffixSorter = new SuffixSorter();

    /**
     * Performs phrase extraction and saves the results to the provided
     * <code>context</code>.
     */
    public void extractPhrases(PreprocessingContext context)
    {
        // Perform suffix sorting first
        suffixSorter.suffixSort(context);

        final int [] suffixArray = context.allTokens.suffixOrder;
        final int [] lcpArray = context.allTokens.lcp;
        final int [] wordIndexesArray = context.allTokens.wordIndex;
        final int [] documentIndexArray = context.allTokens.documentIndex;
        final int [] stemIndexes = context.allWords.stemIndex;

        // Find all subphrases
        List<Substring> rcs = discoverRcs(suffixArray, lcpArray, documentIndexArray);

        List<int []> phraseWordIndexes = Lists.newArrayList();
        IntArrayList phraseTf = new IntArrayList();
        List<int []> phraseTfByDocumentList = Lists.newArrayList();

        if (rcs.size() > 0)
        {
            // Determine most frequent originals and create the final phrase
            // array. Also merge the phrase tf by document maps into flat
            // arrays.
            Collections.sort(rcs, new SubstringComparator(wordIndexesArray, stemIndexes));

            int totalPhraseTf = rcs.get(0).frequency;
            Substring mostFrequentOriginal = rcs.get(0);
            IntIntHashMap phraseTfByDocument = new IntIntHashMap();
            phraseTfByDocument.putAll(mostFrequentOriginal.tfByDocument);

            // Don't change the rcs list type from ArrayList or we'll
            // run into O(n^2) iteration cost :)
            for (int i = 0; i < rcs.size() - 1; i++)
            {
                final Substring substring = rcs.get(i);
                final Substring nextSubstring = rcs.get(i + 1);

                if (substring
                    .isEquivalentTo(nextSubstring, wordIndexesArray, stemIndexes))
                {
                    totalPhraseTf += nextSubstring.frequency;
                    addAllWithOffset(phraseTfByDocument, nextSubstring.tfByDocument, -1);
                    if (mostFrequentOriginal.frequency < nextSubstring.frequency)
                    {
                        mostFrequentOriginal = nextSubstring;
                    }
                }
                else
                {
                    int [] wordIndexes = new int [(mostFrequentOriginal.to - mostFrequentOriginal.from)];
                    for (int j = 0; j < wordIndexes.length; j++)
                    {
                        wordIndexes[j] = wordIndexesArray[mostFrequentOriginal.from + j];
                    }
                    phraseWordIndexes.add(wordIndexes);
                    phraseTf.add(totalPhraseTf);
                    phraseTfByDocumentList.add(IntMapUtils.flatten(phraseTfByDocument));

                    totalPhraseTf = nextSubstring.frequency;
                    mostFrequentOriginal = nextSubstring;
                    phraseTfByDocument.clear();
                    phraseTfByDocument.putAll(nextSubstring.tfByDocument);
                }
            }

            // Add the last substring
            final Substring substring = rcs.get(rcs.size() - 1);
            int [] wordIndexes = new int [(substring.to - substring.from)];
            for (int j = 0; j < wordIndexes.length; j++)
            {
                wordIndexes[j] = wordIndexesArray[mostFrequentOriginal.from + j];
            }
            phraseWordIndexes.add(wordIndexes);
            phraseTf.add(totalPhraseTf);
            phraseTfByDocumentList.add(IntMapUtils.flatten(phraseTfByDocument));
        }

        // Store the results to allPhrases
        context.allPhrases.wordIndices = phraseWordIndexes
            .toArray(new int [phraseWordIndexes.size()] []);
        context.allPhrases.tf = phraseTf.toArray();
        context.allPhrases.tfByDocument = phraseTfByDocumentList
            .toArray(new int [phraseTfByDocumentList.size()] []);
    }

    /**
     * Discovers Right Complete Substrings in the given LCP Suffix Array.
     */
    private List<Substring> discoverRcs(int [] suffixArray, int [] lcpArray,
        int [] documentIndexArray)
    {
        Substring [] rcsStack;
        int sp;

        int i;
        rcsStack = new Substring [lcpArray.length];
        sp = -1;

        i = 1;

        final List<Substring> result = Lists.newArrayList();
        while (i < lcpArray.length - 1)
        {
            final int currentSuffixIndex = suffixArray[i];
            final int currentDocumentIndex = documentIndexArray[currentSuffixIndex];
            final int currentLcp = Math.min(MAX_PHRASE_LENGTH, lcpArray[i]);

            if (sp < 0)
            {
                if (currentLcp >= MIN_PHRASE_LENGTH)
                {
                    // Push to the stack phrases of length 2..currentLcp. Only the 
                    // topmost phrase will get its frequencies incremented, the other
                    // ones will "inherit" the counts when the topmost phrase is 
                    // popped off the stack.
                    final int length = currentLcp;
                    for (int j = length - 2; j >= 0; j--)
                    {
                        sp++;

                        // Set initial tf = 2 for the topmost phrase. For the other phrases,
                        // set tf = 1. During popping of the topmost phrase, the phrase lying
                        // "below" on the stack, will get its tf increased by the tf of 
                        // the phrase being popped, minus 1.
                        rcsStack[sp] = new Substring(i, currentSuffixIndex,
                            currentSuffixIndex + currentLcp - j, (j == 0 ? 2 : 1));

                        // By document tf. Again, topmost phrase gets tf = 2, the other
                        // ones get tf = 1. This time, we need to track from which document's
                        // tf we need to set off the "minus 1", hence the documentIndexToOffset field.
                        rcsStack[sp].tfByDocument = new IntIntHashMap();
                        rcsStack[sp].tfByDocument.put(
                            documentIndexArray[suffixArray[i - 1]], 1);
                        if (j == 0)
                        {
                            rcsStack[sp].tfByDocument.putOrAdd(currentDocumentIndex,
                                1, 1);
                        }
                        else
                        {
                            rcsStack[sp].documentIndexToOffset = documentIndexArray[suffixArray[i - 1]];
                        }
                    }
                }

                i++;
            }
            else
            {
                Substring r = rcsStack[sp];
                if ((r.to - r.from) < currentLcp)
                {
                    Substring r1 = rcsStack[sp];

                    // The phrase we're about to add is an extension of the topmost
                    // phrase on the stack. The new phrase will contribute to the 
                    // topmost phrase's tf, so we need to track the document index
                    // from which we'd set off the "minus 1".
                    r1.documentIndexToOffset = documentIndexArray[suffixArray[i - 1]];

                    // Add the intermediate phrases too (which makes
                    // the algorithm no longer linear btw)
                    int length = currentLcp - (r1.to - r1.from);
                    for (int j = length - 1; j >= 0; j--)
                    {
                        if (currentLcp - j >= MIN_PHRASE_LENGTH)
                        {
                            sp++;
                            rcsStack[sp] = new Substring(i, currentSuffixIndex,
                                currentSuffixIndex + currentLcp - j, (j == 0 ? 2 : 1));

                            rcsStack[sp].tfByDocument = new IntIntHashMap();
                            rcsStack[sp].tfByDocument.put(
                                documentIndexArray[suffixArray[i - 1]], 1);
                            if (j == 0)
                            {
                                rcsStack[sp].tfByDocument.putOrAdd(currentDocumentIndex,
                                    1, 1);
                            }
                            else
                            {
                                rcsStack[sp].documentIndexToOffset = documentIndexArray[suffixArray[i - 1]];
                            }
                        }
                    }

                    i++;
                }
                else
                {
                    Substring r1 = rcsStack[sp];
                    if ((r1.to - r1.from) == currentLcp)
                    {
                        // Increase the frequency of the generalized phrase
                        rcsStack[sp].frequency += 1;
                        rcsStack[sp].tfByDocument.putOrAdd(currentDocumentIndex, 1, 1);

                        i++;
                    }
                    else
                    {
                        Substring s;

                        // Pop generalized phrases off the stack
                        do
                        {
                            if (rcsStack[sp].tfByDocument.size() >= dfThreshold)
                            {
                                // Add the generalized phrase to the result
                                result.add(rcsStack[sp]);
                            }

                            s = rcsStack[sp];
                            sp--;

                            // As we update only the frequency of the stack's
                            // topmost substring we need to propagate the
                            // accumulated frequencies to the shorter
                            // substrings
                            if (sp >= 0)
                            {
                                // The "minus 1" mentioned above.
                                rcsStack[sp].frequency += s.frequency - 1;
                                addAllWithOffset(rcsStack[sp].tfByDocument,
                                    s.tfByDocument, rcsStack[sp].documentIndexToOffset);
                            }
                        }
                        while (sp >= 0
                            && (rcsStack[sp].to - rcsStack[sp].from) > currentLcp);
                    }
                }
            }
        }

        return result;
    }

    private static void addAllWithOffset(IntIntHashMap dest, IntIntHashMap src,
        int documentIndexToOffset)
    {
        for (IntIntCursor c : src)
        {
            final int key = c.key;
            final int value = c.value + (key != documentIndexToOffset ? 0 : -1);
            dest.putOrAdd(key, value, value);
        }
    }
}
