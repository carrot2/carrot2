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
