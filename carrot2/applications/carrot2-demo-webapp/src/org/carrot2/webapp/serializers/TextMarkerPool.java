
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
