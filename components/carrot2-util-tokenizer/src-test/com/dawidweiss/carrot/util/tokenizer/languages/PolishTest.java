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
                {"mrocznie", "mrocznia"}
        };
    }

}
