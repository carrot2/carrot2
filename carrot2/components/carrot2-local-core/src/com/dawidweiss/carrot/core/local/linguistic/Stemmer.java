
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

package com.dawidweiss.carrot.core.local.linguistic;

/**
 * An interface for classes providing word-conflation algorithms (transforming
 * an inflected form of a word to its "base" representation).
 * 
 * <p>
 * <b>Instances of this class may not be thread-safe</b>
 * </p>
 * 
 * <p>
 * Word-conflation can be realized in many ways and various definitions of the
 * terminology can be found around the net and in dictionaries.
 * 
 * <dl>
 * <dt>
 * A lemma
 * </dt>
 * <dd>
 * A lemma is a set of related morphological forms. These are related by
 * orthography in most cases.
 * </dd>
 * <dt>
 * A lexeme
 * </dt>
 * <dd>
 * A lexeme is a meaning realized by a set of forms. It is also a minimal
 * meaningful unit of language, the meaning of which cannot be understood from
 * that of its component morphemes. Take off (in the senses to mimic, to
 * become airborne, etc.) is a lexeme, as well as the independent morphemes
 * take and off [source: Collins English Dictionary].
 * </dd>
 * <dt>
 * A stem
 * </dt>
 * <dd>
 * The root of a word. In highly inflectional languages like Polish all forms
 * of a given lexeme share a common part called stem which carry the semantic
 * contents of the word. [source: Zygmunt Vetulani, Bogdan Walczak, Tomasz
 * Obrebski, Grazyna Vetulani: Unambiguous coding of the inflection of Polish
 * nouns and its application in electronic dictionaries - format POLEX]
 * </dd>
 * </dl>
 * </p>
 * 
 * <p>
 * Conflating terms to stems or lexemes is a common task in Information
 * Retrieval. Two types of algorithms can be distinguished: stemmers are based
 * on heuristic rules and often return an incorrect  (but unique within one
 * inflected meaning of a word) root form of a word. Lemmatization programs
 * (or more complex morphological analyzers)  are often dictionary-based and
 * usually return an exact symbol for an inflected word (whether it is a
 * lexeme, or some other symbol that is unique for a lemma).
 * </p>
 * 
 * <p>
 * For this interface we do not make any distinction between algorithms for
 * <i>stemming</i> and  <i>lemmatization</i>, as long as they transform
 * meaning-related words to an identical string.
 * </p>
 *
 * @author Dawid Weiss
 */
public interface Stemmer {
    /**
     * Returns a conflated form of a sequence of characters representing a term
     * and contained in a character array starting at index
     * <code>startCharacterIndex</code> (inclusive) and of length
     * <code>length</code>.
     *
     * @param charArray The array containing a fragment to be stemmed.
     * @param startCharacterIndex Star index in the <code>charArray</code>
     *        (inclusive).
     * @param length The length of the character sequence to consider.
     *
     * @return A <code>String</code> with the lemma (stem) of the indicated
     *         sequence of characters, or <code>null</code> if no stem is
     *         available.
     */
    public String getStem(char[] charArray, int startCharacterIndex, int length);
}
