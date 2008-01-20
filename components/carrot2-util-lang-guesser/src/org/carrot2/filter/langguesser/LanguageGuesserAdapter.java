
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

package org.carrot2.filter.langguesser;
import java.io.CharArrayReader;

import org.apache.lucene.misc.TrigramLanguageGuesser;

/**
 * Interface adapter between Carrot'2 
 * {@link org.carrot2.core.linguistic.LanguageGuesser}
 * and 
 * {@link TrigramLanguageGuesser}.  
 *  
 * @author Dawid Weiss
 * @version $Revision$
 */
public class LanguageGuesserAdapter
    implements org.carrot2.core.linguistic.LanguageGuesser {

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
	 * @see org.carrot2.core.linguistic.LanguageGuesser#guessLanguage(char[], int, int)
	 */
	public String guessLanguage(char[] charArray, int offset, int length) {
	    // TODO: this should be replaced with a more reliable
	    // statistical test of sample length.
	    final int MINIMAL_SAMPLE_LENGTH = 40;
	    if (length < MINIMAL_SAMPLE_LENGTH) {
	        return null;
	    }
	    
		String langCode = guesser.guessLanguage(new CharArrayReader(charArray, offset, length));
		return langCode;
	}
    
}
