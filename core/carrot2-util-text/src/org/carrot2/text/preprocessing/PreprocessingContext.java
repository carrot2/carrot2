package org.carrot2.text.preprocessing;

import java.util.List;

import org.carrot2.core.Document;
import org.carrot2.text.analysis.TokenType;
import org.carrot2.text.linguistic.Stemmer;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * Document preprocessing context provides low-level (usually integer-coded) data
 * structures useful for further processing.
 */
public final class PreprocessingContext
{
    /** Predicate for splitting on document separator. */
    public static final Predicate<Integer> ON_DOCUMENT_SEPARATOR = Predicates
        .isEqualTo(TokenType.TF_SEPARATOR_DOCUMENT);

    /** Predicate for splitting on field separator. */
    public static final Predicate<Integer> ON_FIELD_SEPARATOR = Predicates
        .isEqualTo(TokenType.TF_SEPARATOR_FIELD);

    /** Predicate for splitting on sentence separator. */
    public static final Predicate<Integer> ON_SENTENCE_SEPARATOR = new Predicate<Integer>()
    {
        public boolean apply(Integer tokenType)
        {
            return (tokenType.intValue() & TokenType.TF_SEPARATOR_SENTENCE) != 0;
        }
    };

    /** Documents to which this context's data refer */
    public List<Document> documents;

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
         * equal to one of {@link TokenType#TF_TERMINATOR},
         * {@link TokenType#TF_SEPARATOR_DOCUMENT} or {@link TokenType#TF_SEPARATOR_FIELD},
         * image is <code>null</code>.
         * <p>
         * This array is produced by the {@link PreprocessingTasks#TOKENIZE} task.
         */
        public char [][] image;

        /**
         * Token's {@link TokenType} bit flags.
         * <p>
         * This array is produced by the {@link PreprocessingTasks#TOKENIZE} task.
         */
        public int [] type;

        /**
         * Document field the token came from. The index points to arrays in
         * {@link AllFields}, equal to <code>-1</code> for document and field
         * separators.
         * <p>
         * This array is produced by the {@link PreprocessingTasks#TOKENIZE} task.
         */
        public byte [] fieldIndex;

        /**
         * Index of the document this token came from, points to elements of
         * {@link PreprocessingContext#documents}. Equal to <code>-1</code> for
         * document separators.
         * <p>
         * This array is produced by the {@link PreprocessingTasks#TOKENIZE} task.
         */
        public int [] documentIndex;

        /**
         * A pointer to {@link AllWords} arrays for this token. Equal to <code>-1</code>
         * for document, field and {@link TokenType#TT_PUNCTUATION} tokens (including
         * sentence separators).
         * <p>
         * This array is produced by the {@link PreprocessingTasks#CASE_NORMALIZE} task.
         */
        public int [] wordIndex;
    }

    /**
     * Information about all tokens of the input {@link PreprocessingContext#documents}.
     */
    public AllTokens allTokens = new AllTokens();

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
         * This array is produced by the {@link PreprocessingTasks#TOKENIZE} task.
         */
        public String [] name;
    }

    /**
     * Information about all fields processed for the input
     * {@link PreprocessingContext#documents}.
     */
    public AllFields allFields = new AllFields();

    /**
     * Information about all unique words found in the input
     * {@link PreprocessingContext#documents}. Each entry in each array corresponds to
     * one unique word with respect to case, e.g. <em>data</em> and <em>DATA</em> will
     * be conflated to one entry in the arrays. Different grammatical forms of one word,
     * e.g. e.g <em>computer</em> and <em>computers</em>, will have different entries
     * in the arrays (see {@link AllStems} for inflection-conflated versions).
     * <p>
     * All arrays in this class have the same length and values across different arrays
     * correspond to each other for the same index.
     */
    public static class AllWords
    {
        /**
         * The most frequently appearing variant of the word with respect to case. E.g. if
         * a token <em>ACM</em> appeared 12 times in the input and <em>Acm</em>
         * appeared 3 times, the image will be equal to <em>ACM</em>.
         * <p>
         * This array is produced by the {@link PreprocessingTasks#CASE_NORMALIZE} task.
         */
        public char [][] image;

        /**
         * Term Frequency of the word, aggregated across all variants with respect to
         * case. Frequencies for each variant separately are not available.
         * <p>
         * This array is produced by the {@link PreprocessingTasks#CASE_NORMALIZE} task.
         */
        public int [] tf;

        /**
         * Term Frequency of the word for each document. The length of this array is equal
         * to the number of documents this word appeared in (Document Frequency)
         * multiplied by 2. Elements at even indices contain document indices pointing to
         * {@link PreprocessingContext#documents}, elements at odd indices contain the
         * frequency of the word in the document. For example, an array with 4 values:
         * <code>[2, 15, 138, 7]</code> means that the word appeared 15 times in
         * document at index 2 and 7 times in document at index 138.
         * <p>
         * This array is produced by the {@link PreprocessingTasks#CASE_NORMALIZE} task.
         */
        public int [][] tfByDocument;

        /**
         * Common word flag for the word, equal to <code>true</code> if the word is a
         * stop word. <b>This array will be replaced with a more generic word flags array
         * in the near future.</b>
         * <p>
         * This array is produced by the {@link PreprocessingTasks#CASE_NORMALIZE} task.
         */
        public boolean [] commonTermFlag;

        /**
         * A pointer to the {@link AllStems} arrays for this word.
         * <p>
         * This array is produced by the {@link PreprocessingTasks#STEMMING} task.
         */
        public int [] stemIndex;
    }

    /**
     * Information about all unique words found in the input
     * {@link PreprocessingContext#documents}.
     */
    public AllWords allWords = new AllWords();

    /**
     * Information about all unique stems found in the input
     * {@link PreprocessingContext#documents}. Each entry in each array corresponds to
     * one base form different words can be transformed to by the {@link Stemmer} used
     * while processing. E.g. the English <em>mining</em> and <em>mine</em> will be
     * aggregated to one entry in the arrays, while they will have separate entries in
     * {@link AllWords}.
     * <p>
     * All arrays in this class have the same length and values across different arrays
     * correspond to each other for the same index.
     */
    public static class AllStems
    {
        /**
         * Stem image as produced by the {@link Stemmer}, may not correspond to any
         * correct word.
         * <p>
         * This array is produced by the {@link PreprocessingTasks#STEMMING} task.
         */
        public char [][] images;

        /**
         * Pointer to the {@link AllWords} arrays, to the most frequent original form of
         * the stem. Pointers to the less frequent variants are not available.
         * <p>
         * This array is produced by the {@link PreprocessingTasks#STEMMING} task.
         */
        public int [] mostFrequentOriginalWordIndex;

        /**
         * Term frequency of the stem, i.e. the sum of all words from {@link AllWords}
         * pointing to the stem.
         * <p>
         * This array is produced by the {@link PreprocessingTasks#STEMMING} task.
         */
        public int [] tf;

        /**
         * Term frequency of the stem for each document. For the encoding of this array,
         * see {@link AllWords#tfByDocument}.
         * <p>
         * This array is produced by the {@link PreprocessingTasks#STEMMING} task.
         */
        public int [][] tfByDocument;
    }

    /**
     * Information about all unique stems found in the input
     * {@link PreprocessingContext#documents}.
     */
    public AllStems allStems = new AllStems();
}
