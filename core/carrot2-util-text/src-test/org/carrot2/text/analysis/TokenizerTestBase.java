/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.analysis;

import java.io.*;
import java.util.ArrayList;

import org.apache.lucene.analysis.*;

/**
 * A base class for testing Carrot2 tokenizers.
 */
abstract class TokenizerTestBase
{
    /**
     * Creates the Analyzer under tests.
     */
    protected abstract TokenStream createTokenStream(Reader reader);

    /**
     * Internal class for comparing sequences of tokens.
     */
    protected static class TokenImage
    {
        final int type;
        final String image;

        public TokenImage(String image, int type)
        {
            this.type = type;
            this.image = image;
        }

        @Override
        public boolean equals(Object o)
        {
            if (o instanceof TokenImage)
            {
                return (((TokenImage) o).image.equals(this.image) && (((TokenImage) o).type == this.type));
            }
            else
            {
                return false;
            }
        }

        @Override
        public int hashCode()
        {
            return image != null ? image.hashCode() ^ type : type;
        }

        public String toString()
        {
            final String rawType = "0x" + Integer.toHexString(type);
            return "[" + rawType + "] " + this.image;
        }
    }

    /**
     * Compare expected and produced token sequences.
     */
    @SuppressWarnings("deprecation")
    protected void assertEqualTokens(String testString, TokenImage [] expectedTokens)
    {
        try
        {
            final TokenStream tokenStream = createTokenStream(new StringReader(testString));

            final ArrayList<TokenImage> tokens = new ArrayList<TokenImage>();
            Token token = new Token();
            while ((token = tokenStream.next(token)) != null)
            {
                final String image = new String(token.termBuffer(), 0, token.termLength());
                final ITokenType payload = (ITokenType) token.getPayload();

                tokens.add(new TokenImage(image, payload.getRawFlags()));
            }

            org.junit.Assert.assertArrayEquals(expectedTokens, tokens.toArray());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    protected TokenImage term(String image)
    {
        return new TokenImage(image, ITokenType.TT_TERM);
    }
    
    protected TokenImage punctuation(String image)
    {
        return new TokenImage(image, ITokenType.TT_PUNCTUATION);
    }
    
    protected TokenImage numeric(String image)
    {
        return new TokenImage(image, ITokenType.TT_NUMERIC);
    }
}
