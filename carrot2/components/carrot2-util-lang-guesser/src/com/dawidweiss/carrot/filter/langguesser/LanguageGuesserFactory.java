
/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 *
 * Sponsored by: CCG, Inc.
 */


package com.dawidweiss.carrot.filter.langguesser;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import com.dawidweiss.carrot.core.local.linguistic.LanguageGuesser;
import org.apache.lucene.misc.TrigramLanguageGuesser;
import org.apache.lucene.misc.Trigrams;

/**
 * Language guesser factory for the Carrot2 framework.
 * 
 * <p>The actual language guesser code uses Trigrams
 * code, contributed to Lucene by Jean-Francois Halleux.
 * 
 * <p>This class retrieves a list of available languages from a resource:
 * <code>trigrams/languages.properties</code>. This resource
 * is a property file and should contain a property
 * named <code>languages</code>, which specifies
 * comma-delimited ISO names of languages for which
 * trigram files are available. A trigram resource's
 * name is a concatenation of 
 * <code>trigrams/</code><i>languageCode</i><code>.tri</code>. 
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
public class LanguageGuesserFactory {

    /**
     * A list of languages available for identification.
     */
    private static final Set languages;
    
    /**
     * A hashmap of language code - {@link Trigrams} pairs for all
     * recognized languages.
     */
    private static final HashMap langMap; 
    
    /**
     * Initialize languages.
     */
    static {
        languages = new HashSet();

        // read properties file specifying available languages.
        Properties props = new Properties();
        try {
			props.load(
                LanguageGuesserFactory.class.getResourceAsStream("/trigrams/languages.properties"));
		} catch (IOException e) {
            throw new RuntimeException("Could not load the required language.properties resource.");
		}

        String languagesList = props.getProperty("languages");
        langMap = new HashMap();
        if (languagesList == null)
            throw new RuntimeException("languages.properties must have a property 'languages'.");
        StringTokenizer tokenizer = new StringTokenizer( languagesList, ",;");
        while (tokenizer.hasMoreTokens()) {
        	String langCode = tokenizer.nextToken().trim();
            // check that the resource indeed exists.
            InputStream is = LanguageGuesserFactory.class.getResourceAsStream("/trigrams/" + langCode + ".tri");
            if (is == null)
                throw new RuntimeException("Trigrams resource does not exist for language: "
                        + langCode);
            try {
                is = new DataInputStream(new BufferedInputStream(is));
            	Trigrams t = Trigrams.loadFromInputStream(
                        (DataInputStream) is);
            	languages.add(langCode);
                langMap.put(langCode, t);
            } catch (IOException e) {
                throw new RuntimeException("Could not read language trigram file: "
                        + langCode);
			} finally {
                try {
					is.close();
				} catch (IOException e) {}
            }
        }
    }

    /**
     * @return Returns a new instance of a trigram language guesser with all 
     *  known languages.
     */
    public static LanguageGuesser getLanguageGuesser() {
        TrigramLanguageGuesser guesser = new TrigramLanguageGuesser(langMap);
        return new LanguageGuesserAdapter(guesser);
    }

    /**
     * @return Returns a new instance of a trigram language guesser restricted
     * to only those languages in <code>allowedLanguages</code>.
     * 
     * @param allowedLanguages A list of allowed languages.
     */
    public static LanguageGuesser getLanguageGuesser(List allowedLanguages) {
        HashMap newMap = new HashMap();
        for (Iterator i = langMap.keySet().iterator(); i.hasNext(); ) {
            String langCode = (String) i.next();
            if (allowedLanguages.contains(langCode)) {
                newMap.put(langCode, langMap.get(langCode));
            }
        }

        return new LanguageGuesserAdapter(new TrigramLanguageGuesser(newMap));
    }
    
}
