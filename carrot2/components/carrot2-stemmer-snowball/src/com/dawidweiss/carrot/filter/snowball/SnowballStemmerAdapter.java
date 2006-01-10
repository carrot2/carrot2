
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

package com.dawidweiss.carrot.filter.snowball;

import com.dawidweiss.carrot.core.local.linguistic.Stemmer;

import net.sf.snowball.SnowballProgram;


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
     * @see com.dawidweiss.carrot.core.local.linguistic.Stemmer#getStem(char[],
     *      int, int)
     */
    public String getStem(char[] charArray, int startCharacter, int length) {
        snowballStemmer.setCurrent(charArray, startCharacter, length);

        if (snowballStemmer.stem() == false) {
            return null;
        }

        return snowballStemmer.getCurrent();
    }
}
