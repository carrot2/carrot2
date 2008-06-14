package org.carrot2.text.linguistic;

/**
 * Simple lemmatization engine transforming an inflected form of a word to its base form
 * or some unique token.
 */
public interface Stemmer
{
    public CharSequence stem(CharSequence word);
}
