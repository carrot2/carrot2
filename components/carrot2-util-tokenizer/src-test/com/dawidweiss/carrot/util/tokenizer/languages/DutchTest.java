package com.dawidweiss.carrot.util.tokenizer.languages;

import com.dawidweiss.carrot.core.local.linguistic.Language;
import com.dawidweiss.carrot.util.tokenizer.languages.dutch.Dutch;

/**
 * Test of the Dutch language.
 *  
 * @author Dawid Weiss
 * @version $Revision$
 */
public class DutchTest extends LanguageImplTestBase {

	public DutchTest() {
		super();
	}

	public DutchTest(String arg0) {
		super(arg0);
	}

	/*
	 * @see com.dawidweiss.carrot.util.tokenizer.languages.LanguageImplTestBase#getLanguageInstance()
	 */
	protected Language getLanguageInstance() {
        return new Dutch();
	}

    protected String [][] getWordsToCompare() {
        return new String [][] {
                {"leren", "ler"},
                {"zullen", "zull"},
                {"Nederlandse", "Nederland"}
        };
    }
    
}
