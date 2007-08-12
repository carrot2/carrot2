
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

package org.carrot2.util.tokenizer.languages;

import org.carrot2.core.linguistic.Language;
import org.carrot2.util.tokenizer.languages.turkish.Turkish;

/**
 * Turkish language test.
 *  
 * @author Dawid Weiss
 */
public class TurkishTest extends LanguageImplTestBase {

	public TurkishTest(String methodName) {
		super(methodName);
	}
    
	/*
	 * @see org.carrot2.util.tokenizer.languages.LanguageImplTestBase#getLanguageInstance()
	 */
	protected Language getLanguageInstance() {
        return new Turkish();
	}

    /**
     * 
     */
    protected String [][] getWordsToCompare() {
        return new String [][] {
                {"arabalar", "araba"},
                {"mühendisler", "mühendis"},
        };
    }

}
