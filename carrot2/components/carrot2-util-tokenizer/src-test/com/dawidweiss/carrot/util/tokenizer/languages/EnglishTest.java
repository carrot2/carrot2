package com.dawidweiss.carrot.util.tokenizer.languages;

import com.dawidweiss.carrot.core.local.linguistic.Language;
import com.dawidweiss.carrot.util.tokenizer.languages.english.English;

/**
 * Polish language test.
 *  
 * @author Dawid Weiss
 * @version $Revision$
 */
public class EnglishTest extends LanguageImplTestBase {

	public EnglishTest() {
		super();
	}

	public EnglishTest(String arg0) {
		super(arg0);
	}

    public void testNotAStopWord() {
    }
    
	/*
	 * @see com.dawidweiss.carrot.util.tokenizer.languages.LanguageImplTestBase#getLanguageInstance()
	 */
	protected Language getLanguageInstance() {
        return new English();
	}

    protected String [][] getWordsToCompare() {
        return new String [][] {
                {"pulps", "pulp"},
                {"driving", "drive"}
        };
    }

}
