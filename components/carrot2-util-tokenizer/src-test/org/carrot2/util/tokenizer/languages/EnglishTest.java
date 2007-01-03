
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
import org.carrot2.util.tokenizer.languages.english.English;

/**
 * Polish language test.
 *  
 * @author Dawid Weiss
 * @version $Revision$
 */
public class EnglishTest extends LanguageImplTestBase {

	public EnglishTest() {
		super();
	}

	public EnglishTest(String arg0) {
		super(arg0);
	}

    public void testNotAStopWord() {
    }
    
	/*
	 * @see org.carrot2.util.tokenizer.languages.LanguageImplTestBase#getLanguageInstance()
	 */
	protected Language getLanguageInstance() {
        return new English();
	}

    protected String [][] getWordsToCompare() {
        return new String [][] {
                {"pulps", "pulp"},
                {"driving", "drive"},
                {"king's", "king"}
        };
    }

}
