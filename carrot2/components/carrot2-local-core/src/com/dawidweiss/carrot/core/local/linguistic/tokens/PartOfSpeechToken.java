
/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
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
 * A token with an associated part of speech.The part of speech is encoded in a
 * numerical value in the range of <code>short</code>.
 * 
 * <p>
 * Some common POS are defined in this class as constants. For a more verbose
 * list of token types in English see for example: <a
 * href="http://www.lri.fr/~roche/CoursDESS/tags.html">Penn-Treebank Part Of
 * Speech token list.</a>
 * </p>
 *
 * @author Dawid Weiss
 */
public interface PartOfSpeechToken extends Token {
    /**
     * An unknown (unrecognized) POS.
     */
    public final static short POS_UNKNOWN = 0;

    /**
     * Preposition.
     */
    public final static short POS_PREPOSITION = 1;

    /**
     * Adjective.
     */
    public final static short POS_ADJECTIVE = 2;

    /**
     * Noun.
     */
    public final static short POS_NOUN = 3;

    /**
     * Proper noun.
     */
    public final static short POS_NOUN_PROPER = 4;

    /**
     * Pronoun.
     */
    public final static short POS_PRONOUN = 5;

    /**
     * Adverb.
     */
    public final static short POS_ADVERB = 6;

    /**
     * Verb.
     */
    public final static short POS_VERB = 7;

    /**
     * Conjunction.
     */
    public final static short POS_CONJUNCTION = 8;

    /**
     * @return Returns the part-of-speech code of this token.
     */
    public short getPartOfSpeechCode();
}
