
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.util.tokenizer.languages;

import com.dawidweiss.carrot.core.local.linguistic.Language;
import com.dawidweiss.carrot.util.tokenizer.languages.spanish.Spanish;

/**
 * Test of the Spanish language.
 *  
 * @author Dawid Weiss
 * @version $Revision$
 */
public class SpanishTest extends LanguageImplTestBase {

	public SpanishTest() {
		super();
	}

	public SpanishTest(String arg0) {
		super(arg0);
	}

	/*
	 * @see com.dawidweiss.carrot.util.tokenizer.languages.LanguageImplTestBase#getLanguageInstance()
	 */
	protected Language getLanguageInstance() {
        return new Spanish();
	}

    protected String [][] getWordsToCompare() {
        return new String [][] {
                {"noticias", "notici"},
                {"Imágenes", "Imagen"}
        };
    }
    
}
