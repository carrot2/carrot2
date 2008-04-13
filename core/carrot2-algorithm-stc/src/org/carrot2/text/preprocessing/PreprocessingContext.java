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
     */
    public final CharSequenceIntMap tokenCoder = new CharSequenceIntMap();

    /**
     * Unique set of token images after running a {@link PreprocessingTasks#TOKENIZE} on a
     * set of {@link Document}s.
     * 
     * @see #allTokens
     * @see PreprocessingTasks#TOKENIZE
     */
    public CharSequence [] allTokenImages;

    /**
     * <p>
     * A set of tokens and synthetic separators after running a
     * {@link PreprocessingTasks#TOKENIZE} on a set of {@link Document}s.
     * <p>
     * Non-negative indices in this array point to token images in {@link #allTokenImages},
     * negative indices have the following meaning:
     * <ul>
     * <li>{@link #SEPARATOR_SENTENCE} - separates individual sentences (if tokenization
     * engine supports it).</li>
     * <li>{@link #SEPARATOR_DOCUMENT} - separates documents.</li>
     * </ul>
     * 
     * @see PreprocessingTasks#TOKENIZE
     */
    public int [] allTokens;

    /**
     * <p>
     * a set of {@link TokenType} bit flags for each token in {@link #allTokens} array.
     * Indices in this array correspond to indices in {@link #allTokens}.
     * 
     * @see PreprocessingTasks#TOKENIZE
     * @see #allTokenTypes
     */
    public int [] allTypes;

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

    /**
     * <i>Common word</i> flag for tokens in {@link #allTokenImages}.
     * 
     * @see PreprocessingTasks#MARK_TOKENS_STOPLIST
     */
    public boolean [] commonTermFlag;
}
