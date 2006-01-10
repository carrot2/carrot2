
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
import com.dawidweiss.carrot.util.tokenizer.languages.polish.Polish;

/**
 * Polish language test.
 *  
 * @author Dawid Weiss
 * @version $Revision$
 */
public class PolishTest extends LanguageImplTestBase {

	public PolishTest() {
		super();
	}

	public PolishTest(String arg0) {
		super(arg0);
	}

	/*
	 * @see com.dawidweiss.carrot.util.tokenizer.languages.LanguageImplTestBase#getLanguageInstance()
	 */
	protected Language getLanguageInstance() {
        return new Polish();
	}

    protected String [][] getWordsToCompare() {
        return new String [][] {
                {"okropnymi", "okropny"},
                {"mrocznie", "mroczny"}
        };
    }
}
