package org.carrot2.text.analysis;

/**
 * 
 */
public final class TokenTypeUtils
{
    /*
     * 
     */
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

    /**
     * Return <code>true</code> if the given token type denotes
     * a sentence separator.
     * 
     * @see TokenType#TF_SEPARATOR_SENTENCE
     */
    public static boolean isSentenceDelimiter(TokenType type)
    {
        return (type.getRawFlags() & TokenType.TF_SEPARATOR_SENTENCE) != 0;
    }
}
