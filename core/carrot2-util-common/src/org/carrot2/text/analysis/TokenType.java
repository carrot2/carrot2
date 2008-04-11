package org.carrot2.text.analysis;

/**
 * <p>
 * Provides bitwise flags with additional information about each token:
 * <dl>
 * <dt>token type</dt>
 * <dd>Types of tokens: numbers, URIs, punctuation, acronyms and others. See all
 * constants in this class declared with <code>TT_</code> prefix.</dd>
 * <dt>token flags</dt>
 * <dd>Additional token flags such as an indication whether a punctuation token is a
 * sentence delimiter ({@link #TF_SENTENCEMARKER}).</dd>
 * </dl>
 * 
 * @see TokenTypeUtils
 */
public interface TokenType
{
    /*
     * Token type mask: 0x00ff
     */
    public static final int TT_TERM = 0x0001;
    public static final int TT_NUMERIC = 0x0002;
    public static final int TT_PUNCTUATION = 0x0003;
    public static final int TT_EMAIL = 0x0004;
    public static final int TT_ACRONYM = 0x0005;
    public static final int TT_FULL_URL = 0x0006;
    public static final int TT_BARE_URL = 0x0007;
    public static final int TT_FILE = 0x0008;
    public static final int TT_HYPHTERM = 0x0009;

    /*
     * Additional token flags, mask: 0x0f00
     */
    public static final int TF_SENTENCEMARKER = 0x0100;

    /**
     * @return Returns raw bitmask associated with the token.
     */
    public int getRawFlags();
}
