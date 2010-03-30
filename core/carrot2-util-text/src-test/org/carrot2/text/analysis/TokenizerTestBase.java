/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.analysis;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;

/**
 * A base class for testing Carrot2 tokenizers.
 */
abstract class TokenizerTestBase
{
    /**
     * Creates the Analyzer under tests.
     */
    protected abstract Tokenizer createTokenStream() throws IOException;

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
    protected void assertEqualTokens(String testString, TokenImage [] expectedTokens)
    {
        try
        {
            final Tokenizer tokenStream = createTokenStream();
            tokenStream.reset(new StringReader(testString));

            final TermAttribute term = tokenStream.getAttribute(TermAttribute.class);
            final ITokenTypeAttribute type = tokenStream
                .getAttribute(ITokenTypeAttribute.class);
            final ArrayList<TokenImage> tokens = new ArrayList<TokenImage>();
            while (tokenStream.incrementToken())
            {
                tokens.add(new TokenImage(term.term(), type.getRawFlags()));
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
        return new TokenImage(image, ITokenTypeAttribute.TT_TERM);
    }

    protected TokenImage punctuation(String image)
    {
        return new TokenImage(image, ITokenTypeAttribute.TT_PUNCTUATION);
    }

    protected TokenImage numeric(String image)
    {
        return new TokenImage(image, ITokenTypeAttribute.TT_NUMERIC);
    }
}
