/*
 * Created on 2004-04-02 To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.dawidweiss.carrot.util.tokenizer.languages;

import java.util.*;

import com.dawidweiss.carrot.core.local.linguistic.Language;

import org.apache.log4j.Logger;

/**
 * A factory that allows access to all known languages implemented in this
 * module.
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
public class AllKnownLanguages
{
	private final static Logger logger = Logger.getLogger(AllKnownLanguages.class);

    /** All known languages */
    private static Language [] languageArray;

    /** A mapping between ISO codes and Language instances */
    private static Map languages;

    /** Initialize the data */
    static
    {
    	// Load them dynamically. Removing some JARs will not hurt
    	// this class then.
    	String [] languageArray = new String [] {
    		"com.dawidweiss.carrot.util.tokenizer.languages.dutch.Dutch",
			"com.dawidweiss.carrot.util.tokenizer.languages.english.English",
			"com.dawidweiss.carrot.util.tokenizer.languages.french.French",
			"com.dawidweiss.carrot.util.tokenizer.languages.german.German",
			"com.dawidweiss.carrot.util.tokenizer.languages.italian.Italian",
			"com.dawidweiss.carrot.util.tokenizer.languages.polish.Polish",
			"com.dawidweiss.carrot.util.tokenizer.languages.spanish.Spanish"
    	};

        languages = new HashMap();
        for (int i = 0; i < languageArray.length; i++)
        {
        	String langClazz = languageArray[i];
        	try {
        		Language lang = (Language) AllKnownLanguages.class.getClassLoader().loadClass(
        			langClazz).newInstance();
	            languages.put(lang.getIsoCode(), lang);
        	} catch (Throwable t) {
        		logger.warn("Could not instantiate language: " + langClazz, t);
        	}
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