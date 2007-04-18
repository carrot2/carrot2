/*
 * Copyright (c) 2004 Poznan Supercomputing and Networking Center
 * 10 Noskowskiego Street, Poznan, Wielkopolska 61-704, Poland
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Poznan Supercomputing and Networking Center ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into
 * with PSNC.
 */

package org.carrot2.webapp.serializers;

import org.carrot2.core.linguistic.Language;
import org.carrot2.util.tokenizer.languages.english.English;


/**
 * A factory for {@link TextMarker}s.
 * 
 * @author Stanislaw Osinski
 */
public class TextMarkerPool
{
    /** The default instance of the TextMarker */
    public static final TextMarkerPool INSTANCE = new TextMarkerPool(
            new English());

    /** Language to be used */
    private Language language;


    /**
     * Creates a factory for the English language.
     */
    private TextMarkerPool(Language language)
    {
        this.language = language;
    }


    /**
     * Borrows one instance of {@link TextMarker} from the pool.
     */
    public TextMarker borrowTextMarker()
    {
        return new TextMarker(language.borrowTokenizer(), language
                .borrowStemmer(), language.getStopwords());
    }


    /**
     * Returns a {@link TextMarker} to the pool.
     * 
     * @param textMarker
     */
    public void returnTextMarker(TextMarker textMarker)
    {
        language.returnTokenizer(textMarker.tokenizer);
        language.returnStemmer(textMarker.stemmer);
    }
}
