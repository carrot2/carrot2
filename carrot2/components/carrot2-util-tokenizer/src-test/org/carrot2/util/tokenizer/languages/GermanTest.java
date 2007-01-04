
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
import org.carrot2.util.tokenizer.languages.german.German;

/**
 * Test of the German language.
 *  
 * @author Dawid Weiss
 * @version $Revision$
 */
public class GermanTest extends LanguageImplTestBase {

	public GermanTest() {
		super();
	}

	public GermanTest(String arg0) {
		super(arg0);
	}

	/*
	 * @see org.carrot2.util.tokenizer.languages.LanguageImplTestBase#getLanguageInstance()
	 */
	protected Language getLanguageInstance() {
        return new German();
	}

    protected String [][] getWordsToCompare() {
        return new String [][] {
                {"Prominente", "Prominent"},
                {"bekommen", "bekomm"}
        };
    }
    
}
