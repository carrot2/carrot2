package org.carrot2.text.analysis;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;
import org.carrot2.text.MutableCharArray;
import org.junit.Test;

public class TokenArrayBuilderTest
{
    @Test
    public void testUniqueTokenCodes() throws IOException
    {
        final TokenArrayBuilder builder = build("abc def abc abc");

        final MutableCharArray [] images = builder.getTokenImages();
        final int [] tokens = builder.getTokens();

        assertEquals(tokens[0], tokens[2]);
        assertEquals(tokens[0], tokens[3]);
        assertEquals(2, images.length);
        assertEquals(new MutableCharArray("abc"), images[0]);
        assertEquals(new MutableCharArray("def"), images[1]);
    }

    /**
     * 
     */
    private TokenArrayBuilder build(String text) throws IOException
    {
        final TokenArrayBuilder builder = new TokenArrayBuilder();
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
