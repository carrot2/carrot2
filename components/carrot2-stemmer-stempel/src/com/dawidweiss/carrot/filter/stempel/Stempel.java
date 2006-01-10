
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

package com.dawidweiss.carrot.filter.stempel;

import com.dawidweiss.carrot.core.local.linguistic.Stemmer;


/**
 * An adapter for Stempel -- an algorithmic stemmer for the Polish language
 * written by Andrzej Bialecki. <a href="http://www.getopt.org/stempel">
 * Check out Stempel's Web Site</a>
 *
 * @author Dawid Weiss
 * @version $Revision$
 */
public class Stempel implements Stemmer {

    /**
     * An instance of Stempel that we use.
     */
    private final org.getopt.stempel.Stemmer stempel;    

    /**
     * Creates an instance of stempel stemmer with the default table (2000 sample).
     */
    public Stempel() {
        try {
        	this.stempel = new org.getopt.stempel.Stemmer();
		} catch (Exception e) {
            throw new RuntimeException("Could not load Stempel stemmer.");
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
        String string = new String(charArray, beginCharacter,
                endCharacter - beginCharacter).toLowerCase();
        return stempel.stem(string, false);
    }
}
