package org.carrot2.text.analysis;

/**
 * Token type and flags returned from 
 * {@link ExtendedWhitespaceTokenizer#getLastTokenType()}.
 */
public final class TokenInfo implements Cloneable
{
    /*
     * Token type mask: 0x00ff
     */
    public static final int TERM         = 0x0001;
    public static final int NUMERIC      = 0x0002;
    public static final int PUNCTUATION  = 0x0003;
    public static final int EMAIL        = 0x0004;
    public static final int ACRONYM      = 0x0005;
    public static final int FULL_URL     = 0x0006;
    public static final int BARE_URL     = 0x0007;
    public static final int FILE         = 0x0008;
    public static final int HYPHTERM     = 0x0009;

    /*
     * Additional token flags, mask: 0x0f00
     */
    public static final int SENTENCEMARKER = 0x0100;

    /**
     * Full token information (including token flag bits).
     */
    private int info;

    /**
     * Create a new token type with the given value.
     */
    TokenInfo(int info)
    {
        this.info = info;
    }

    /**
     * @return Return <code>true</code> if this token was a sentence
     * marker.
     */
    public final boolean isSentenceMarker()
    {
        return (this.info & SENTENCEMARKER) != 0;
    }

    /**
     * @return Returns masked value of this token type.
     * @see 
     */
    public final int getTokenType()
    {
        return (this.info & 0x00ff);
    }

    /**
     * Clones this token info.
     */
    protected Object clone() throws CloneNotSupportedException
    {
        return new TokenInfo(this.info);
    }
    
    /**
     * Dump as string.
     */
    public String toString()
    {
        return "raw type: 0x" + Integer.toHexString(info);
    }
    
    /**
     * Reset the value of this object.  
     */
    void setValue(int info)
    {
        this.info = info;
    }

    /**
     * @return Returns masked value of this token type.
     */
    int getRawTokenInfo()
    {
        return this.info;
    }
}