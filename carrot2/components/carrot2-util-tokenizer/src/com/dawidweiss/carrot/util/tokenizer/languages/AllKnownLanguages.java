/*
 * Created on 2004-04-02
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.dawidweiss.carrot.util.tokenizer.languages;

import com.dawidweiss.carrot.core.local.linguistic.Language;
import com.dawidweiss.carrot.util.tokenizer.languages.dutch.Dutch;
import com.dawidweiss.carrot.util.tokenizer.languages.english.English;
import com.dawidweiss.carrot.util.tokenizer.languages.french.French;
import com.dawidweiss.carrot.util.tokenizer.languages.german.German;
import com.dawidweiss.carrot.util.tokenizer.languages.italian.Italian;
import com.dawidweiss.carrot.util.tokenizer.languages.polish.Polish;
import com.dawidweiss.carrot.util.tokenizer.languages.spanish.Spanish;


/**
 * A factory that allows access to all known languages
 * implemented in this module.
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
public class AllKnownLanguages {

    /** Disallow instantiation */
    private AllKnownLanguages() {
    }
    
    public static final Language [] getLanguages() {
        return new Language [] {
                new English(),
                new Polish(),
                new Dutch(),
                new French(),
                new German(),
                new Italian(),
                new Spanish()
        };
    }
    
}
