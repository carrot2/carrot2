
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

import java.util.List;

import org.carrot2.core.Document;
import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.text.linguistic.ILanguageModel;
import org.carrot2.text.linguistic.IStemmer;
import org.carrot2.text.util.MutableCharArray;

import com.carrotsearch.hppc.BitSet;
import com.carrotsearch.hppc.ObjectOpenHashSet;
import com.carrotsearch.hppc.predicates.ShortPredicate;

/**
 * Document preprocessing context provides low-level (usually integer-coded) data
 * structures useful for further processing.
 * 
 * <p><img src="doc-files/preprocessing-arrays.png"
 *      alt="Internals of PreprocessingContext"/></p>
 */
public final class PreprocessingContext
{
    /** Predicate for splitting on document separator. */
    public static final ShortPredicate ON_DOCUMENT_SEPARATOR = 
        equalTo(ITokenizer.TF_SEPARATOR_DOCUMENT);

    /** Predicate for splitting on field separator. */
    public static final ShortPredicate ON_FIELD_SEPARATOR = 
        equalTo(ITokenizer.TF_SEPARATOR_FIELD);

    /** Predicate for splitting on sentence separator. */
    public static final ShortPredicate ON_SENTENCE_SEPARATOR = new ShortPredicate()
    {
        public boolean apply(short tokenType)
        {
            return (tokenType & ITokenizer.TF_SEPARATOR_SENTENCE) != 0;
        }
    };

    /** 
     * Return a new {@link ShortPredicate} returning <code>true</code>
     * if the argument equals a given value. 
     */
    public static final ShortPredicate equalTo(final short t)
    {
        return new ShortPredicate() {
            public boolean apply(short value)
            {
                return value == t; 
            }
        };
    }

    /** Query used to perform processing, may be <code>null</code> */
    public final String query;

    /** A list of documents to process. */
    public final List<Document> documents;

    /** Language model to be used */
    public final ILanguageModel language;

    /**
     * Token interning cache. Token images are interned to save memory and allow reference
     * comparisons.
     */
    public ObjectOpenHashSet<MutableCharArray> tokenCache = new ObjectOpenHashSet<MutableCharArray>();

    /**
     * Creates a preprocessing context for the provided <code>documents</code> and with
     * the provided <code>languageModel</code>.
     */
    public PreprocessingContext(ILanguageModel languageModel, List<Document> documents,
        String query)
    {
        this.query = query;
        this.documents = documents;
        this.language = languageModel;
    }

    /**
     * Information about all tokens of the input {@link PreprocessingContext#documents}.
     * Each element of each of the arrays corresponds to one individual token from the
     * input or a synthetic separator inserted between documents, fields and sentences.
     * Last element of this array is a special terminator entry.
     * <p>
     * All arrays in this class have the same length and values across different arrays
     * correspond to each other for the same index.
     */
    public static class AllTokens
    {
        /**
         * Token image as it appears in the input. On positions where {@link #type} is
         * equal to one of {@link ITokenizer#TF_TERMINATOR},
         * {@link ITokenizer#TF_SEPARATOR_DOCUMENT} or
         * {@link ITokenizer#TF_SEPARATOR_FIELD} , image is <code>null</code>.
         * <p>
         * This array is produced by {@link Tokenizer}.
         */
        public char [][] image;

        /**
         * Token's {@link ITokenizer} bit flags.
         * <p>
         * This array is produced by {@link Tokenizer}.
         */
        public short [] type;

        /**
         * Document field the token came from. The index points to arrays in
         * {@link AllFields}, equal to <code>-1</code> for document and field separators.
         * <p>
         * This array is produced by {@link Tokenizer}.
         */
        public byte [] fieldIndex;

        /**
         * Index of the document this token came from, points to elements of
         * {@link PreprocessingContext#documents}. Equal to <code>-1</code> for document
         * separators.
         * <p>
         * This array is produced by {@link Tokenizer}.
         * 
         * TODO: is this always needed? Seems awfully repetitive, esp. for long docs.
         */
        public int [] documentIndex;

        /**
         * A pointer to {@link AllWords} arrays for this token. Equal to <code>-1</code>
         * for document, field and {@link ITokenizer#TT_PUNCTUATION} tokens (including
         * sentence separators).
         * <p>
         * This array is produced by {@link CaseNormalizer}.
         */
        public int [] wordIndex;

        /**
         * The suffix order of tokens. Suffixes starting with a separator come at the end
         * of the array.
         * <p>
         * This array is produced by {@link PhraseExtractor}.
         */
        public int [] suffixOrder;

        /**
         * The Longest Common Prefix for the adjacent suffix-sorted token sequences.
         * <p>
         * This array is produced by {@link PhraseExtractor}.
         */
        public int [] lcp;
    }

    /**
     * Information about all tokens of the input {@link PreprocessingContext#documents}.
     */
    public final AllTokens allTokens = new AllTokens();

    /**
     * Information about all fields processed for the input
     * {@link PreprocessingContext#documents}.
     */
    public static class AllFields
    {
        /**
         * Name of the document field. Entries of {@link AllTokens#fieldIndex} point to
         * this array.
         * <p>
         * This array is produced by {@link Tokenizer}.
         */
        public String [] name;
    }

    /**
     * Information about all fields processed for the input
     * {@link PreprocessingContext#documents}.
     */
    public final AllFields allFields = new AllFields();

    /**
     * Information about all unique words found in the input
     * {@link PreprocessingContext#documents}. Each entry in each array corresponds to one
     * unique word with respect to case, e.g. <em>data</em> and <em>DATA</em> will be
     * conflated to one entry in the arrays. Different grammatical forms of one word, e.g.
     * e.g <em>computer</em> and <em>computers</em>, will have different entries in the
     * arrays (see {@link AllStems} for inflection-conflated versions).
     * <p>
     * All arrays in this class have the same length and values across different arrays
     * correspond to each other for the same index.
     */
    public static class AllWords
    {
        /**
         * The most frequently appearing variant of the word with respect to case. E.g. if
         * a token <em>MacOS</em> appeared 12 times in the input and <em>macos</em>
         * appeared 3 times, the image will be equal to <em>MacOS</em>.
         * <p>
         * This array is produced by {@link CaseNormalizer}.
         */
        public char [][] image;

        /**
         * Token type of this word copied from {@link AllTokens#type}. Additional
         * flags are set for each word by 
         * {@link CaseNormalizer} and {@link LanguageModelStemmer}.
         * 
         * <p>
         * This array is produced by {@link CaseNormalizer}.
         * This array is modified by {@link LanguageModelStemmer}.
         * 
         * @see ITokenizer
         */
        public short [] type;

        /**
         * Term Frequency of the word, aggregated across all variants with respect to
         * case. Frequencies for each variant separately are not available.
         * <p>
         * This array is produced by {@link CaseNormalizer}.
         */
        public int [] tf;

        /**
         * Term Frequency of the word for each document. The length of this array is equal
         * to the number of documents this word appeared in (Document Frequency)
         * multiplied by 2. Elements at even indices contain document indices pointing to
         * {@link PreprocessingContext#documents}, elements at odd indices contain the
         * frequency of the word in the document. For example, an array with 4 values:
         * <code>[2, 15, 138, 7]</code> means that the word appeared 15 times in document
         * at index 2 and 7 times in document at index 138.
         * <p>
         * This array is produced by {@link CaseNormalizer}.
         */
        public int [][] tfByDocument;

        /**
         * A pointer to the {@link AllStems} arrays for this word.
         * <p>
         * This array is produced by {@link LanguageModelStemmer}.
         */
        public int [] stemIndex;

        /**
         * A bit-packed indices of all fields in which this word appears at least once. 
         * Indexes (positions) of selected bits are pointers to the 
         * {@link AllFields} arrays. Fast conversion between the bit-packed representation
         * and <code>byte[]</code> with index values is done by {@link #toFieldIndexes(byte)}  
         * <p>
         * This array is produced by {@link CaseNormalizer}.
         */
        public byte [] fieldIndices;
    }

    /**
     * Information about all unique words found in the input
     * {@link PreprocessingContext#documents}.
     */
    public final AllWords allWords = new AllWords();

    /**
     * Information about all unique stems found in the input
     * {@link PreprocessingContext#documents}. Each entry in each array corresponds to one
     * base form different words can be transformed to by the {@link IStemmer} used while
     * processing. E.g. the English <em>mining</em> and <em>mine</em> will be aggregated
     * to one entry in the arrays, while they will have separate entries in
     * {@link AllWords}.
     * <p>
     * All arrays in this class have the same length and values across different arrays
     * correspond to each other for the same index.
     */
    public static class AllStems
    {
        /**
         * Stem image as produced by the {@link IStemmer}, may not correspond to any
         * correct word.
         * <p>
         * This array is produced by {@link LanguageModelStemmer}.
         */
        public char [][] image;

        /**
         * Pointer to the {@link AllWords} arrays, to the most frequent original form of
         * the stem. Pointers to the less frequent variants are not available.
         * <p>
         * This array is produced by {@link LanguageModelStemmer}.
         */
        public int [] mostFrequentOriginalWordIndex;

        /**
         * Term frequency of the stem, i.e. the sum of all {@link AllWords#tf} values
         * for which the {@link AllWords#stemIndex} points to this stem.
         * <p>
         * This array is produced by {@link LanguageModelStemmer}.
         */
        public int [] tf;

        /**
         * Term frequency of the stem for each document. For the encoding of this array,
         * see {@link AllWords#tfByDocument}.
         * <p>
         * This array is produced by {@link LanguageModelStemmer}.
         */
        public int [][] tfByDocument;

        /**
         * A bit-packed indices of all fields in which this word appears at least once. 
         * Indexes (positions) of selected bits are pointers to the 
         * {@link AllFields} arrays. Fast conversion between the bit-packed representation
         * and <code>byte[]</code> with index values is done by {@link #toFieldIndexes(byte)}  
         * <p>
         * This array is produced by {@link LanguageModelStemmer}
         */
        public byte [] fieldIndices;
    }

    /**
     * Information about all unique stems found in the input
     * {@link PreprocessingContext#documents}.
     */
    public final AllStems allStems = new AllStems();

    /**
     * Information about all frequently appearing sequences of words found in the input
     * {@link PreprocessingContext#documents}. Each entry in each array corresponds to one
     * sequence.
     * <p>
     * All arrays in this class have the same length and values across different arrays
     * correspond to each other for the same index.
     */
    public static class AllPhrases
    {
        /**
         * Pointers to {@link AllWords} for each word in the phrase sequence.
         * <p>
         * This array is produced by {@link PhraseExtractor}.
         */
        public int [][] wordIndices;

        /**
         * Term frequency of the word sequence.
         * <p>
         * This array is produced by {@link PhraseExtractor}.
         */
        public int [] tf;

        /**
         * Term frequency of the word sequence for each document. For the encoding of this
         * array, see {@link AllWords#tfByDocument}.
         * <p>
         * This array is produced by {@link PhraseExtractor}.
         */
        public int [][] tfByDocument;
    }

    /**
     * Information about all frequently appearing sequences of words found in the input
     * {@link PreprocessingContext#documents}.
     */
    public AllPhrases allPhrases = new AllPhrases();

    /**
     * Information about words and phrases that might be good cluster label candidates.
     * Each entry in each array corresponds to one label candidate.
     * <p>
     * All arrays in this class have the same length and values across different arrays
     * correspond to each other for the same index.
     */
    public static class AllLabels
    {
        /**
         * Feature index of the label candidate. Features whose values are less than the
         * size of {@link AllWords} arrays are single word features and point to entries
         * in {@link AllWords}. Features whose values are larger or equal to the size of
         * {@link AllWords}, after subtracting the size of {@link AllWords}, point to
         * {@link AllPhrases}.
         * <p>
         * This array is produced by {@link LabelFilterProcessor}.
         */
        public int [] featureIndex;

        /**
         * Indices of documents assigned to the label candidate.
         * <p>
         * This array is produced by {@link DocumentAssigner}.
         */
        public BitSet [] documentIndices;

        /**
         * The first index in {@link #featureIndex} which 
         * points to {@link AllPhrases}, or -1 if there are no phrases
         * in {@link #featureIndex}.
         * <p>
         * This value is set by {@link LabelFilterProcessor}.
         * 
         * @see #featureIndex
         */
        public int firstPhraseIndex;
    }

    /**
     * Information about words and phrases that might be good cluster label candidates.
     */
    public final AllLabels allLabels = new AllLabels();

    /**
     * Returns <code>true</code> if this context contains any words.
     */
    public boolean hasWords()
    {
        return allWords.image.length > 0;
    }

    /**
     * Returns <code>true</code> if this context contains any label candidates.
     */
    public boolean hasLabels()
    {
        return allLabels.featureIndex != null && allLabels.featureIndex.length > 0;
    }

    /**
     * Static conversion between selected bits and an array of indexes of these bits. 
     */
    private final static int [][] bitsCache;
    static
    {
        bitsCache = new int [0x100][];
        for (int i = 0; i < 0x100; i++)
        {
            bitsCache[i] = new int [Integer.bitCount(i & 0xFF)];
            for (int v = 0, bit = 0, j = i & 0xff; j != 0; j >>>= 1, bit++)
            {
                if ((j & 0x1) != 0)
                    bitsCache[i][v++] = bit;
            }
        }
    }
    
    /**
     * Convert the selected bits in a byte to an array of indexes.
     */
    public int [] toFieldIndexes(byte b)
    {
        return bitsCache[b & 0xff];
    }

    /* 
     * These should really be package-private, shouldn't they? We'd need to move classes under pipeline.
     * here for accessibility.
     */

    /**
     * This method should be invoked after all preprocessing contributors have been executed
     * to release temporary data structures. 
     */
    public void preprocessingFinished()
    {
        this.tokenCache = null;
    }

    /**
     * Return a unique char buffer representing a given character sequence.
     */
    public char [] intern(MutableCharArray chs)
    {
        if (tokenCache.contains(chs))
        {
            return tokenCache.lget().getBuffer();
        }
        else
        {
            final char [] tokenImage = new char [chs.length()];
            System.arraycopy(chs.getBuffer(), chs.getStart(), tokenImage, 0, chs.length());
            tokenCache.add(new MutableCharArray(tokenImage));
            return tokenImage;
        }
    }
}
