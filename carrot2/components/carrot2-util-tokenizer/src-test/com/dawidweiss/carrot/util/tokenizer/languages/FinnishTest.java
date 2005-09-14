package com.dawidweiss.carrot.util.tokenizer.languages;

import com.dawidweiss.carrot.core.local.linguistic.Language;
import com.dawidweiss.carrot.util.tokenizer.languages.finnish.Finnish;

/**
 * Test of the Finnish language.
 *  
 * @author Dawid Weiss
 * @version $Revision$
 */
public class FinnishTest extends LanguageImplTestBase {

	public FinnishTest() {
		super();
	}

	public FinnishTest(String arg0) {
		super(arg0);
	}

	protected Language getLanguageInstance() {
        return new Finnish();
	}

    protected String [][] getWordsToCompare() {
        return new String [][] {
                /* TODO: Add words here. */
        };
    }
}
