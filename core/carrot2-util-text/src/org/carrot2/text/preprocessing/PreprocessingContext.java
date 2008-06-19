package org.carrot2.text.preprocessing;

import org.carrot2.core.Document;
import org.carrot2.text.*;
import org.carrot2.text.analysis.TokenType;

import com.google.common.base.*;

/**
 * Document preprocessing context provides low-level (usually integer-coded) data
 * structures useful for further processing.
 */
public final class PreprocessingContext
{
    /** Special token marker separating documents. */
    public static final int SEPARATOR_DOCUMENT = -1;

    /** Special token marker separating fields (sections) of one document. */
    public static final int SEPARATOR_FIELD = -2;

    /** Special token marker separating sentences. */
    public static final int SEPARATOR_SENTENCE = -3;

    /** Predicate for splitting on document separator. */
    public static final Predicate<Integer> ON_DOCUMENT_SEPARATOR = Predicates
        .isEqualTo(SEPARATOR_DOCUMENT);

    /** Predicate for splitting on field separator. */
    public static final Predicate<Integer> ON_FIELD_SEPARATOR = Predicates
        .isEqualTo(SEPARATOR_FIELD);

    /** Predicate for splitting on sentence separator. */
    public static final Predicate<Integer> ON_SENTENCE_SEPARATOR = Predicates
        .isEqualTo(SEPARATOR_SENTENCE);

    /**
     * Engine used to convert arbitrary character sequences into non-negative integer
     * codes so that each unique character sequence is assigned a unique code.
     * 
     * @see PreprocessingTasks#TOKENIZE
     */
    public CharSequenceIntMap tokenMap;

    /**
     * All arrays in this class have the same {@link AllTokens#length} and values across
     * different arrays correspond to each other for the same index.
     */
    public class AllTokens
    {
        /** Length of all arrays in this class. */
        public int length;

        /**
         * Token images.
         */
        public char [][] images;

        /**
         * A set of {@link TokenType} bit flags for each token.
         * 
         * @see PreprocessingTasks#TOKENIZE
         * @see #allTokenTypes
         */
        public int [] types;

        /**
         * Indices pointing to {@link AllFields}, -1 for separators.
         */
        public byte [] fieldIndices;

        /**
         * Document indices.
         */
        public int [] documentIndices;

        /**
         * A set of tokens and synthetic separators after running a
         * {@link PreprocessingTasks#TOKENIZE} on a set of {@link Document}s.
         * <p>
         * Non-negative indices in this array point to token images in
         * {@link #allTokenImages}, negative indices have the following meaning:
         * <ul>
         * <li>{@link #SEPARATOR_SENTENCE} - separates individual sentences (if
         * tokenization engine supports it).</li>
         * <li>{@link #SEPARATOR_DOCUMENT} - separates documents.</li>
         * </ul>
         * 
         * @see PreprocessingTasks#TOKENIZE
         */
        public int [] wordIndices;
    }

    /**
     * 
     */
    public AllTokens allTokens = new AllTokens();

    /**
     *
     */
    public class AllFields
    {
        public int length;

        public String [] names;
    }

    public AllFields allFields = new AllFields();

    /**
     *
     */
    public class AllWords
    {
        public int length;

        /**
         * Unique set of token images after running a {@link PreprocessingTasks#TOKENIZE}
         * on a set of {@link Document}s.
         * 
         * @see #allTokens
         * @see PreprocessingTasks#TOKENIZE
         */
        public CharSequence [] images;

        /**
         * <i>Common word</i> flag for tokens in {@link #images}.
         * 
         * @see PreprocessingTasks#MARK_TOKENS_STOPLIST
         */
        public boolean [] commonTermFlag;
    }

    /**
     * 
     */
    public AllWords allWords = new AllWords();

    /**
     *
     */
    public class AllStems
    {
        public int length;
    }

    /**
     * 
     */
    public AllStems allStems = new AllStems();

    /**
     * Token sequence with indices corresponding to {@link #allTokens} and values pointing
     * to case-normalized token versions.
     * 
     * @see PreprocessingTasks#CASE_NORMALIZE
     */
    public int [] allTokensNormalized;

    /**
     * Token sequence with indices identical to {@link #allTokens} and values pointing to
     * stemmed token versions.
     * 
     * @see PreprocessingTasks#STEMMING
     */
    public int [] allTokensStemmed;
}
