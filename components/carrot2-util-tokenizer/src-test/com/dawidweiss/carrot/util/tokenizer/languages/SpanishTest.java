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
