
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
package com.dawidweiss.carrot.core.local.linguistic;

/**
 * Language guesser detects the language of a chunk of characters and returns
 * its ISO-639 language code.
 * 
 * @author Dawid Weiss
 * @version $Revision$
 *
 * @see <a
 *      href="http://www.ics.uci.edu/pub/ietf/http/related/iso639.txt">http://www.ics.uci.edu/pub/ietf/http/related/iso639.txt</a>
 */
public interface LanguageGuesser {
    /**
     * Detects the language of a fragment of a character array. The returned
     * value is an ISO-639 language code.
     * 
     * @param charArray The character array to analyze.
     * @param offset    Start index of the fragment to analyze (inclusive).
     * @param length    Length of the fragment to analyze.
     *
     * @return The methods returns an ISO language code, or <code>null</code>
     *         if language could not be guessed.
     *
     * @see <a
     *      href="http://www.ics.uci.edu/pub/ietf/http/related/iso639.txt">http://www.ics.uci.edu/pub/ietf/http/related/iso639.txt</a>
     */
    public String guessLanguage(char [] charArray, int offset, int length);
}
