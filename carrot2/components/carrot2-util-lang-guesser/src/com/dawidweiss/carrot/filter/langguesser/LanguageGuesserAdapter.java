
/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 *
 * Sponsored by: CCG, Inc.
 */

package com.dawidweiss.carrot.filter.langguesser;
import java.io.CharArrayReader;

import org.apache.lucene.misc.TrigramLanguageGuesser;

/**
 * Interface adapter between Carrot'2 
 * {@link com.dawidweiss.carrot.core.local.linguistic.LanguageGuesser}
 * and 
 * {@link TrigramLanguageGuesser}.  
 *  
 * @author Dawid Weiss
 * @version $Revision$
 */
public class LanguageGuesserAdapter
    implements com.dawidweiss.carrot.core.local.linguistic.LanguageGuesser {

    /** Wrapped trigram guesser. */
    private final TrigramLanguageGuesser guesser;
    
    /**
     * Creates a new adapter.
     * 
     * @param guesser Trigram guesser.
     */
    public LanguageGuesserAdapter(TrigramLanguageGuesser guesser) {
        this.guesser = guesser;
    }
    
	/** 
     * Returns the language of a chunk of text.
     * 
	 * @see com.dawidweiss.carrot.core.local.linguistic.LanguageGuesser#guessLanguage(char[], int, int)
	 */
	public String guessLanguage(char[] charArray, int offset, int length) {
	    // TODO: this should be replaced with a more reliable
	    // statistical test of sample length.
	    final int MINIMAL_SAMPLE_LENGTH = 80;
	    if (length < MINIMAL_SAMPLE_LENGTH) {
	        return null;
	    }
	    
		String langCode = guesser.guessLanguage(new CharArrayReader(charArray, offset, length));
		return langCode;
	}
    
}
