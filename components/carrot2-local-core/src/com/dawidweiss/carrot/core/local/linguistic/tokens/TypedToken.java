
/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 *
 * Sponsored by: CCG, Inc.
 */

package com.dawidweiss.carrot.core.local.linguistic.tokens;

/**
 * A token with an associated function in a sentence. The function can be one
 * of the following: punctuation marker, sentence delimiter, numeric entity,
 * symbol entity (url's, e-mails), term (generic token). 
 * 
 * <p>
 * Every token has an associated <code>type</code>. This type is expressed as a
 * union of flags and token types defined as constants in  {@link TypedToken}
 * interface.
 * </p>
 * 
 * <p>
 * For example, punctuation-type token that also marks a sentence-delimeter
 * will have an associated type value of:  <code>TOKEN_TYPE_PUNCTUATION |
 * TOKEN_FLAG_SENTENCE_DELIM</code>
 * </p>
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
public interface TypedToken extends Token {
    /**
     * Punctuation token. For latin languages: commas, full stops, semicolons
     * etc.
     */
    public static final short TOKEN_TYPE_PUNCTUATION    = 1 << 0;

    /**
     * A numeric token - numbers, dates, value ranges.
     */
    public static final short TOKEN_TYPE_NUMERIC        = 1 << 1;

    /**
     * A symbol. Symbols are usually not part of the grammar, but some
     * literals. For example, e-mail addresses, or URLs are symbols.
     */
    public static final short TOKEN_TYPE_SYMBOL         = 1 << 2;

    /**
     * A term (word). A single lexical entity.
     */
    public static final short TOKEN_TYPE_TERM           = 1 << 3;

    /**
     * An unknown type of a token.
     */
    public static final short TOKEN_TYPE_UNKNOWN        = 1 << 9;

    /**
     * A flag indicating a sentence delimeter.
     */
    public static final short TOKEN_FLAG_SENTENCE_DELIM = 1 << 10;

    /**
     * If <code>true</code> the token is a stop-word.
     */
    public static final short TOKEN_FLAG_STOPWORD       = 1 << 11;
    
    /**
     * A constant with a bit mask leaving only the type of the token
     */
    public static final short MASK_TOKEN_TYPE           = 
        TOKEN_TYPE_PUNCTUATION | TOKEN_TYPE_NUMERIC | TOKEN_TYPE_TERM
        | TOKEN_TYPE_SYMBOL | TOKEN_TYPE_UNKNOWN;
    
    /**
     * @return Returns one of the constants defined in  {@link TypedToken}
     *         interface - a union of type and flags for this token.
     */
    public short getType();
}
