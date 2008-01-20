
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.stemming.snowball;

import org.carrot2.core.linguistic.Stemmer;

import org.tartarus.snowball.SnowballProgram;


/**
 * An adapter of Snowball-generated stemmers.
 * 
 * <p>
 * <b>The adapter is not thread-safe.</b>
 * </p>
 *
 * @author Dawid Weiss
 * @version $Revision$
 */
public class SnowballStemmerAdapter implements Stemmer {
    /** 
     * An instance of the snowball stemmer to use for 
     * this adapter.
     */
    private final SnowballProgram snowballStemmer;

    /**
     * Creates a new {@link Stemmer} interface adapter.
     *
     * @param snowballStemmer An instance of a snowball stemmer program.
     */
    protected SnowballStemmerAdapter(SnowballProgram snowballStemmer) {
        this.snowballStemmer = snowballStemmer;
    }

    /**
     * @return Returns the stem for a character sequence.
     *
     * @see org.carrot2.core.linguistic.Stemmer#getStem(char[],
     *      int, int)
     */
    public String getStem(char[] charArray, int startCharacter, int length) {
        snowballStemmer.setCurrent(charArray, startCharacter, length);

        final boolean result = snowballStemmer.stem();
        if (!result) {
            return null;
        }
        return snowballStemmer.getCurrent();
    }
}
