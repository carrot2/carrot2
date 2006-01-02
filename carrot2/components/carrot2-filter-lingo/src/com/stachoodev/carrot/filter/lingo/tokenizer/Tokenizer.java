
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.filter.lingo.tokenizer;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public interface Tokenizer
{
    /**
     * Token type representing  regular terms - word, hyphenated words
     * (alpha-beta), even single-letter conjunctions (i,a)
     */
    public static final int TYPE_TERM = 0x0001;

    /**
     * Token type representing  URLs - currently only http and file urls
     * supported.
     */
    public static final int TYPE_URL = 0x0002;

    /**
     * Token type representing e-mail addresses
     */
    public static final int TYPE_EMAIL = 0x0003;

    /**
     * Token type representing abbreviated names, for example G. Bush or
     * S.C.Johnson. <em>If only surname is present in the text, it will be
     * returned as a <code>TYPE_TERM</code></em> token!
     */
    public static final int TYPE_PERSON = 0x0004;

    /**
     * Token type representing sentence boundary ('.', '?' or alike)
     */
    public static final int TYPE_SENTENCEMARKER = 0x0005;

    /**
     * INNER-SENTENCE PUNCTUATION MARK
     */
    public static final int TYPE_PUNCTUATION = 0x0006;

    /**
     * Numeric sequence
     */
    public static final int TYPE_NUMERIC = 0x0007;

    /**
     * Restart token parsing for input string <code>string</code>.
     */
    public void restartTokenizerOn(String string);

    /**
     * Return the next token from the parsing data. The token value is returned
     * from the method, while the type of the token is stored in the zero
     * index of the input parameter <code>tokenTypeHolder</code>. If
     * tokenTypeHolder is <code>null</code>, token type is not saved anywhere.
     * <code>null</code> is returned when end of the input data has been
     * reached. This method is <em>not</em> synchronized.
     */
    public String getNextToken(int [] tokenTypeHolder);
}