
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.tokenizer.languages;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.carrot2.core.linguistic.Language;

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
    private static String [] languageCodes;

    private static Language [] languageArray;
    
    /** A mapping between ISO codes and Language instances */
    private static Map languages;

    /**
     * Classes implementing {@link Language} interface. We instantiate
     * them dynamically to avoid errors if some of them are not available.
     */
    final static String [] languageClasses = new String [] {
        "org.carrot2.util.tokenizer.languages.dutch.Dutch",
        "org.carrot2.util.tokenizer.languages.english.English",
        "org.carrot2.util.tokenizer.languages.french.French",
        "org.carrot2.util.tokenizer.languages.german.German",
        "org.carrot2.util.tokenizer.languages.italian.Italian",
        "org.carrot2.util.tokenizer.languages.polish.Polish",
        "org.carrot2.util.tokenizer.languages.spanish.Spanish",
        "org.carrot2.util.tokenizer.languages.danish.Danish",
        "org.carrot2.util.tokenizer.languages.finnish.Finnish",
        "org.carrot2.util.tokenizer.languages.norwegian.Norwegian",
        "org.carrot2.util.tokenizer.languages.portuguese.Portuguese",
        "org.carrot2.util.tokenizer.languages.russian.Russian",
        "org.carrot2.util.tokenizer.languages.swedish.Swedish"
    };
    
    /** Initialize the data */
    static
    {
        languages = new HashMap();
        languageCodes = new String[languageClasses.length];
        for (int i = 0; i < languageClasses.length; i++)
        {
        	String langClazz = languageClasses[i];
        	try {
        		Language lang = (Language) AllKnownLanguages.class.getClassLoader().loadClass(
        			langClazz).newInstance();
	            languages.put(lang.getIsoCode(), lang);
                languageCodes[i] = lang.getIsoCode();
        	} catch (Throwable t) {
        		logger.warn("Could not instantiate language: " + langClazz, t);
        	}
        }
        
        languageArray = (Language []) languages.values().toArray(
            new Language [languages.size()]);
    }

    /** Disallow instantiation */
    private AllKnownLanguages()
    {
    }

    public static final String [] getLanguageCodes()
    {
        return languageCodes;
    }
    
    public static Language [] getLanguages()
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