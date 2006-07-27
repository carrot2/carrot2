
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.lingo.tokenizer;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public interface TokenizerImplConstants {
    /** DOCUMENT ME! */
    int EOF = 0;

    /** DOCUMENT ME! */
    int URL = 1;

    /** DOCUMENT ME! */
    int WWWADDR = 2;

    /** DOCUMENT ME! */
    int URLPATH = 3;

    /** DOCUMENT ME! */
    int TERM = 4;

    /** DOCUMENT ME! */
    int HYPHTERM = 5;

    /** DOCUMENT ME! */
    int PERSON = 6;

    /** DOCUMENT ME! */
    int ACRONYM = 7;

    /** DOCUMENT ME! */
    int EMAIL = 8;

    /** DOCUMENT ME! */
    int SENTENCEMARKER = 9;

    /** DOCUMENT ME! */
    int PUNCTUATION = 10;

    /** DOCUMENT ME! */
    int NUMERIC = 11;

    /** DOCUMENT ME! */
    int SYMBOL = 12;

    /** DOCUMENT ME! */
    int WHSPACE = 13;

    /** DOCUMENT ME! */
    int LETTERSEQ = 14;

    /** DOCUMENT ME! */
    int LETTER = 15;

    /** DOCUMENT ME! */
    int DIGIT = 16;

    /** DOCUMENT ME! */
    int SPACES = 17;

    /** DOCUMENT ME! */
    int NOISE = 18;

    /** DOCUMENT ME! */
    int DEFAULT = 0;

    /** DOCUMENT ME! */
    String[] tokenImage = {
            "<EOF>", "<URL>", "<WWWADDR>", "<URLPATH>", "<TERM>", "<HYPHTERM>",
            "<PERSON>", "<ACRONYM>", "<EMAIL>", "<SENTENCEMARKER>",
            "<PUNCTUATION>", "<NUMERIC>", "<SYMBOL>", "<WHSPACE>", "<LETTERSEQ>",
            "<LETTER>", "<DIGIT>", "<SPACES>", "<NOISE>",
        };
}
