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
     * 
     */
    public static boolean isSentenceDelimiter(TokenType type)
    {
        return (type.getRawFlags() & TokenType.TF_SENTENCEMARKER) != 0;
    }
}
