package com.dawidweiss.carrot.util.tokenizer.languages;

import com.dawidweiss.carrot.core.local.linguistic.Language;
import com.dawidweiss.carrot.util.tokenizer.languages.italian.Italian;

/**
 * Test of the Italian language.
 *  
 * @author Dawid Weiss
 * @version $Revision$
 */
public class ItalianTest extends LanguageImplTestBase {

	public ItalianTest() {
		super();
	}

	public ItalianTest(String arg0) {
		super(arg0);
	}

	/*
	 * @see com.dawidweiss.carrot.util.tokenizer.languages.LanguageImplTestBase#getLanguageInstance()
	 */
	protected Language getLanguageInstance() {
        return new Italian();
	}

    protected String [][] getWordsToCompare() {
        return new String [][] {
                {"mangiava", "mang"},
                {"vogliamo", "vogl"}
        };
    }
    
}
