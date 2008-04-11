package org.carrot2.text.analysis;

import org.apache.lucene.index.Payload;

/**
 * Lucene's {@link Payload} implementing {@link TokenInfo}. 
 */
final class TokenTypePayload extends Payload implements TokenType
{
    private static final long serialVersionUID = 0x200804101135L;

    /**
     * Flags associated with the current token.
     */
    private int flags;

    /**
     * @see TokenType#getRawFlags()
     */
    public int getRawFlags()
    {
        return flags;
    }
    
    /**
     * Sets {@link #flags}.
     */
    void setRawFlags(int flags)
    {
        this.flags = flags;
    }
}
