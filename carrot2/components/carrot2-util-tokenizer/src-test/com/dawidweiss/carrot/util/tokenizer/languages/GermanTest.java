package com.dawidweiss.carrot.util.tokenizer.languages;

import com.dawidweiss.carrot.core.local.linguistic.Language;
import com.dawidweiss.carrot.util.tokenizer.languages.german.German;

/**
 * Test of the German language.
 *  
 * @author Dawid Weiss
 * @version $Revision$
 */
public class GermanTest extends LanguageImplTestBase {

	public GermanTest() {
		super();
	}

	public GermanTest(String arg0) {
		super(arg0);
	}

	/*
	 * @see com.dawidweiss.carrot.util.tokenizer.languages.LanguageImplTestBase#getLanguageInstance()
	 */
	protected Language getLanguageInstance() {
        return new German();
	}

    protected String [][] getWordsToCompare() {
        return new String [][] {
                {"Prominente", "Prominent"},
                {"bekommen", "bekomm"}
        };
    }
    
}
