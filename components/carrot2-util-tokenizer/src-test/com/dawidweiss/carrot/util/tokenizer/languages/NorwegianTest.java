
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

package com.dawidweiss.carrot.util.tokenizer.languages;

import com.dawidweiss.carrot.core.local.linguistic.Language;
import com.dawidweiss.carrot.util.tokenizer.languages.norwegian.Norwegian;

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
