/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
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

    /** A list of supported language codes */
    private static List languageCodes;

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
        languageCodes = new ArrayList(languageArray.length);
        for (int i = 0; i < languageArray.length; i++)
        {
        	String langClazz = languageArray[i];
        	try {
        		Language lang = (Language) AllKnownLanguages.class.getClassLoader().loadClass(
        			langClazz).newInstance();
	            languages.put(lang.getIsoCode(), lang);
                languageCodes.add(lang.getIsoCode());
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
    public static final List getLanguageCodes()
    {
        return languageCodes;
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