package com.dawidweiss.carrot.util.tokenizer.languages;

import com.dawidweiss.carrot.core.local.linguistic.Language;
import com.dawidweiss.carrot.util.tokenizer.languages.danish.Danish;

/**
 * Test of the Danish language.
 *  
 * @author Dawid Weiss
 * @version $Revision$
 */
public class DanishTest extends LanguageImplTestBase {

	public DanishTest() {
		super();
	}

	public DanishTest(String arg0) {
		super(arg0);
	}

	protected Language getLanguageInstance() {
        return new Danish();
	}

    protected String [][] getWordsToCompare() {
        return new String [][] {
                /* TODO: Add words here. */
        };
    }
}
