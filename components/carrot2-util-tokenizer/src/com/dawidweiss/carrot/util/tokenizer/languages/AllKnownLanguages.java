/*
 * Created on 2004-04-02 To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.dawidweiss.carrot.util.tokenizer.languages;

import java.util.*;

import com.dawidweiss.carrot.core.local.linguistic.Language;
import com.dawidweiss.carrot.util.tokenizer.languages.dutch.Dutch;
import com.dawidweiss.carrot.util.tokenizer.languages.english.English;
import com.dawidweiss.carrot.util.tokenizer.languages.french.French;
import com.dawidweiss.carrot.util.tokenizer.languages.german.German;
import com.dawidweiss.carrot.util.tokenizer.languages.italian.Italian;
import com.dawidweiss.carrot.util.tokenizer.languages.polish.Polish;
import com.dawidweiss.carrot.util.tokenizer.languages.spanish.Spanish;

/**
 * A factory that allows access to all known languages implemented in this
 * module.
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
public class AllKnownLanguages
{

    /** All known languages */
    private static Language [] languageArray;

    /** A mapping between ISO codes and Language instances */
    private static Map languages;

    /** Initialize the data */
    static
    {
        languageArray = new Language[]
        { 
         	new English(), 
         	new Polish(), 
         	new Dutch(), 
         	new French(), 
         	new German(),
         	new Italian(), 
         	new Spanish() 
        };

        languages = new HashMap();
        for (int i = 0; i < languageArray.length; i++)
        {
            languages.put(languageArray[i].getIsoCode(), languageArray[i]);
        }
    }

    /** Disallow instantiation */
    private AllKnownLanguages()
    {
    }

    /**
     * @return
     */
    public static final Language [] getLanguages()
    {
        return languageArray;
    }

    /**
     * Returns a {@link Language}instance for the given ISO code. If no known
     * language corresponds to the ISO code <code>null</code> will be
     * returned.
     * 
     * @param isoCode
     * @return a {@link Language}instance for the given ISO code
     */
    public static final Language getLanguageForIsoCode(String isoCode)
    {
        return (Language) languages.get(isoCode);
    }
}