
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
import org.carrot2.util.tokenizer.languages.russian.Russian;

/**
 * Russian language test.
 *  
 * @author Dawid Weiss
 */
public class RussianTest extends LanguageImplTestBase {

	public RussianTest(String methodName) {
		super(methodName);
	}
    
	protected Language getLanguageInstance() {
        return new Russian();
	}

    protected String [][] getWordsToCompare() {
        return new String [][] {
                {"абиссинию", "абиссин"},
                {"благословенной", "благословен"},
                {"вздремнет", "вздремнет"},
        };
    }
    
    
}
