package org.carrot2.text.analysis;

/**
 * Utility methods for working with {@link TokenType}s.
 */
public final class TokenTypeUtils
{
    private TokenTypeUtils()
    {
        // no instances.
    }

    /**
     * Mask the given raw token type and leave just the token
     * type bits.
     */
    public static int maskType(int rawType) 
    {
        return rawType & TokenType.TYPE_MASK;
    }
}
