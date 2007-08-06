
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
import org.carrot2.util.tokenizer.languages.norwegian.Norwegian;

/**
 * Test of the Norwegian language.
 *  
 * @author Dawid Weiss
 * @version $Revision$
 */
public class NorwegianTest extends LanguageImplTestBase {

	public NorwegianTest() {
		super();
	}

	public NorwegianTest(String arg0) {
		super(arg0);
	}

	protected Language getLanguageInstance() {
        return new Norwegian();
	}

    protected String [][] getWordsToCompare() {
        return new String [][] {
                /* TODO: Add words here. */
        };
    }
}
