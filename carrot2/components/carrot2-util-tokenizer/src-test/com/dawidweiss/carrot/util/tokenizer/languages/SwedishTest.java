package com.dawidweiss.carrot.util.tokenizer.languages;

import com.dawidweiss.carrot.core.local.linguistic.Language;
import com.dawidweiss.carrot.util.tokenizer.languages.swedish.Swedish;

/**
 * Test of the Swedish language.
 *  
 * @author Dawid Weiss
 * @version $Revision$
 */
public class SwedishTest extends LanguageImplTestBase {

	public SwedishTest() {
		super();
	}

	public SwedishTest(String arg0) {
		super(arg0);
	}

	protected Language getLanguageInstance() {
        return new Swedish();
	}

    protected String [][] getWordsToCompare() {
        return new String [][] {
                /* TODO: Add words here. */
        };
    }
}
