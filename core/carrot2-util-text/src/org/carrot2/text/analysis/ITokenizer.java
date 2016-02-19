
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

package org.carrot2.text.analysis;

import java.io.IOException;
import java.io.Reader;

import org.carrot2.text.preprocessing.LanguageModelStemmer;
import org.carrot2.text.preprocessing.PreprocessingContext.AllWords;
import org.carrot2.text.preprocessing.StopListMarker;
import org.carrot2.text.util.MutableCharArray;

/**
 * Splits input characters into tokens representing e.g. words, digits, acronyms,
 * punctuation. For each token, the following information is available:
 * <dl>
 * <dt>token type</dt>
 * <dd>Types of tokens: numbers, URIs, punctuation, acronyms and others. See all constants
 * in this class declared with <code>TT_</code> prefix, e.g. {@link #TT_TERM}.</dd>
 * <dt>token flags</dt>
 * <dd>Additional token flags such as an indication whether a punctuation token is a
 * sentence delimiter ({@link #TF_SEPARATOR_SENTENCE}).</dd>
 * </dl>
 * 
 * @see TokenTypeUtils
 */
public interface ITokenizer
{
    /*
     * Token type mask: 0x000f
     */
    public static final int TYPE_MASK = 0x000f;

    public static final int TT_TERM = 0x0001;
    public static final int TT_NUMERIC = 0x0002;
    public static final int TT_PUNCTUATION = 0x0003;
    public static final int TT_EMAIL = 0x0004;
    public static final int TT_ACRONYM = 0x0005;
    public static final int TT_FULL_URL = 0x0006;
    public static final int TT_BARE_URL = 0x0007;
    public static final int TT_FILE = 0x0008;
    public static final int TT_HYPHTERM = 0x0009;

    /**
     * Indicates the end of the token stream.
     */
    public static final int TT_EOF = -1;

    /*
     * Additional token flags, mask: 0xFF00
     */

    /** Current token is a sentence separator. */
    public static final short TF_SEPARATOR_SENTENCE = 0x0100;

    /** Current token is a document separator (never returned from parsing). */
    public static final short TF_SEPARATOR_DOCUMENT = 0x0200;

    /** Current token separates document's logical fields. */
    public static final short TF_SEPARATOR_FIELD = 0x0400;

    /** Current token terminates the input (never returned from parsing). */
    public static final short TF_TERMINATOR = 0x0800;

    /*
     * Token flags related to processing steps after tokenization. To save some memory,
     * these token flags are stored together with token type. These flags may not be
     * available directly from the tokenizer.
     */

    /**
     * The current token is a common word. This flag is not directly available from the
     * tokenizer.
     * 
     * @see AllWords#type
     * @see StopListMarker
     */
    public static final short TF_COMMON_WORD = 0x1000;

    /**
     * The current token is part of the query. This flag is not directly available from
     * the tokenizer.
     * 
     * @see AllWords#type
     * @see LanguageModelStemmer
     */
    public static final short TF_QUERY_WORD = 0x2000;

    /**
     * Resets the tokenizer to process new data
     * 
     * @param reader the input to tokenize. The reader <strong>will not be closed</strong>
     *            by the tokenizer when the end of stream is reached.
     */
    public void reset(Reader reader) throws IOException;

    /**
     * Returns the next token from the input stream.
     * 
     * @return the type of the token as defined by the {@link #TT_TERM} and other
     *         constants or {@link #TT_EOF} when the end of the data stream has been
     *         reached.
     * @see TokenTypeUtils
     */
    public short nextToken() throws IOException;

    /**
     * Sets the <strong>current</strong> token image to the provided buffer.
     * 
     * @param array buffer in which the <strong>current</strong> token image should be
     *            stored
     */
    public void setTermBuffer(MutableCharArray array);
}
