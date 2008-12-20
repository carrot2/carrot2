package org.carrot2.text.linguistic;

/**
 * An implementation of {@link IStemmer} that always returns <code>null</code> from
 * {@link #stem(CharSequence)}.
 */
final class IdentityStemmer implements IStemmer
{
    public CharSequence stem(CharSequence word)
    {
        return null;
    }
}
