package org.carrot2.text.preprocessing;

import static org.junit.Assert.assertEquals;

import java.io.*;

import org.apache.lucene.analysis.*;
import org.carrot2.text.*;
import org.carrot2.text.analysis.ExtendedWhitespaceTokenizer;
import org.junit.Test;

public class TokenizerTaskTest
{
    @Test
    public void testUniqueTokenCodes() throws IOException
    {
        final TokenizerTask tokenizer = build("abc def abc abc");

        final MutableCharArray [] images = tokenizer.getTokenImages();
        final int [] tokens = tokenizer.getTokens();

        assertEquals(tokens[0], tokens[2]);
        assertEquals(tokens[0], tokens[3]);
        assertEquals(2, images.length);
        assertEquals(new MutableCharArray("abc"), images[0]);
        assertEquals(new MutableCharArray("def"), images[1]);
        assertEquals(tokenizer.getTokenTypes().length, tokens.length);
    }

    /**
     * 
     */
    private TokenizerTask build(String text) throws IOException
    {
        final TokenizerTaskImpl builder = new TokenizerTaskImpl();
        final Tokenizer tokenizer = new ExtendedWhitespaceTokenizer(
            new StringReader(text));

        Token token = null;
        while ((token = tokenizer.next(token)) != null)
        {
            builder.add(token);
        }

        return builder;
    }
}
