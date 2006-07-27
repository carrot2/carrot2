
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.stemming.snowball;

import org.carrot2.core.linguistic.Stemmer;

import org.tartarus.snowball.SnowballProgram;
import org.tartarus.snowball.ext.*;

import java.util.HashMap;
import java.util.Iterator;


/**
 * A factory of Snowball (http://snowball.tartarus.org) stemmer adapters implementing {@link Stemmer}
 * interface.
 *
 * @author Dawid Weiss
 * @version $Revision$
 */
public class SnowballStemmersFactory {
    /**
     * A translation map between ISO language codes and Snowball stemmer
     * classes.
     */
    private static final HashMap snowballLanguages;

    static {
        snowballLanguages = new HashMap();
        snowballLanguages.put("da", danishStemmer.class);
        snowballLanguages.put("nl", dutchStemmer.class);
        snowballLanguages.put("en", englishStemmer.class);
        snowballLanguages.put("fi", finnishStemmer.class);
        snowballLanguages.put("fr", frenchStemmer.class);
        snowballLanguages.put("de", germanStemmer.class);
        snowballLanguages.put("it", italianStemmer.class);
        snowballLanguages.put("no", norwegianStemmer.class);
        snowballLanguages.put("pt", portugueseStemmer.class);
        snowballLanguages.put("ru", russianStemmer.class);
        snowballLanguages.put("es", spanishStemmer.class);
        snowballLanguages.put("sv", swedishStemmer.class);

        // attempt to instantiate all the stemmers.
        // if a stemmer could not be instantiated, a runtime exception
        // will be thrown and the entire class will not be loaded.
        for (Iterator i = snowballLanguages.keySet().iterator(); i.hasNext();) {
            getInstance((String) i.next());
        }
    }

    /**
     * No instantiation using constructor. Use factory method {@link
     * #getInstance(String)}
     */
    private SnowballStemmersFactory() {
    }

    /**
     * Returns a new instance of {@link Stemmer} interface based on an
     * implementation from the Snawball package.
     * 
     * <p>
     * <b>The returned stemmers are NOT thread-safe.</b>
     * </p>
     *
     * @param language The ISO language code (as in {@link java.util.Locale}).
     *
     * @return A new stemmer instance, or <code>null</code> if no stemmer for
     *         the language exists.
     *
     * @throws RuntimeException If the stemmer class should be available,  but
     *         could not be instantiated for some reason.
     */
    public static Stemmer getInstance(String language)
        throws RuntimeException {
        if (snowballLanguages.containsKey(language)) {
            try {
                // attempts to instantiate the stemmer.
                SnowballProgram snowballStemmers = (SnowballProgram) ((Class) snowballLanguages.get(language)).newInstance();

                return new SnowballStemmerAdapter(snowballStemmers);
            } catch (InstantiationException e) {
                throw new RuntimeException("Could not instantiate the stemmer.",
                    e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Illegal access exception occurred.",
                    e);
            }
        } else {
            return null;
        }
    }
}
