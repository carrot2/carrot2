package org.carrot2.text.linguistic;

/**
 * Always returns the singleton {@link IdentityStemmer#INSTANCE}.
 */
final class IdentityStemmerFactory implements IStemmerFactory
{
    @Override
    public IStemmer createInstance()
    {
        return IdentityStemmer.INSTANCE;
    }
}
