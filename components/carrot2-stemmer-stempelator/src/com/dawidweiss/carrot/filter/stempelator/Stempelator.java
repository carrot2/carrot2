
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

package com.dawidweiss.carrot.filter.stempelator;

import java.io.IOException;

import com.dawidweiss.carrot.core.local.linguistic.Stemmer;


/**
 * An implementation of a heuristic (dictionary-driven and stempel-based) stemming 
 * engine for the Polish language.
 *
 * @author Dawid Weiss
 * @version $Revision$
 *
 * @see <a href="http://www.cs.put.poznan.pl/dweiss/xml/projects/lametyzator/index.xml?lang=en">http://www.cs.put.poznan.pl/dweiss/xml/projects/lametyzator/index.xml</a>
 */
public class Stempelator implements Stemmer {
    private static com.dawidweiss.stemmers.Stempelator instance;

    public Stempelator() {
        synchronized (this.getClass()) {
            if (instance == null) {
                try {
                	instance = new com.dawidweiss.stemmers.Stempelator();
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
