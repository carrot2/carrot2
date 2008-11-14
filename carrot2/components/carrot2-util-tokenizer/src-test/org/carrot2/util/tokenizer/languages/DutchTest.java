
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

package org.carrot2.util.tokenizer.languages;

import org.carrot2.core.linguistic.Language;
import org.carrot2.util.tokenizer.languages.dutch.Dutch;

/**
 * Test of the Dutch language.
 *  
 * @author Dawid Weiss
 * @version $Revision$
 */
public class DutchTest extends LanguageImplTestBase {

	public DutchTest() {
		super();
	}

	public DutchTest(String arg0) {
		super(arg0);
	}

	/*
	 * @see org.carrot2.util.tokenizer.languages.LanguageImplTestBase#getLanguageInstance()
	 */
	protected Language getLanguageInstance() {
        return new Dutch();
	}

    protected String [][] getWordsToCompare() {
        return new String [][] {
                {"leren", "ler"},
                {"zullen", "zull"},
                {"Nederlandse", "Nederland"}
        };
    }
    
}
