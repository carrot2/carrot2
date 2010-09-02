package org.carrot2.text.linguistic;

/**
 * Provides a stemming engine, typically for a hard-coded language.
 */
public interface IStemmerFactory
{
    /**
     * Creates a new (or reusable, but thread-safe) instance of a stemmer.
     */
    public IStemmer createInstance();
}
