
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.stemming.morfologik;

import java.io.IOException;

import org.carrot2.core.linguistic.Stemmer;


/**
 * An implementation of a heuristic stemming 
 * engine for the Polish language.
 */
public class Stempelator implements Stemmer {
    private static morfologik.stemmers.IStemmer instance;

    public Stempelator() {
        synchronized (this.getClass()) {
            if (instance == null) {
                try {
                	instance = new morfologik.stemmers.Stempelator();
                } catch (IOException e) {
                    throw new RuntimeException("Could not initialize Stempelator.", e);
                }
            }
        }
    }
    
    /**
     * Stems a term in characters array (a performance consideration) starting
     * at index <code>beginCharacter</code> (inclusive) and ending at
     * <code>endCharacter</code> (exclusive). This follows the pattern of
     * <code>String.substring</code>. If more than one base form the term is
     * available, the first one returned by the FSAStemmer is returned.
     */
    public String getStem(char[] charArray, int beginCharacter, int endCharacter) {
        final String string = new String(charArray, beginCharacter, endCharacter - beginCharacter).toLowerCase();
        final String[] stemmedForms = instance.stem(string);

        if (stemmedForms.length > 0) {
            return stemmedForms[0];
        } else {
            return null;
        }
    }
}
