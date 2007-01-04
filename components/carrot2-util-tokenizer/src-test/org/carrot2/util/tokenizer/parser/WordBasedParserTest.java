
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.tokenizer.parser;


import java.io.StringReader;

import junit.framework.TestCase;

import org.carrot2.core.linguistic.tokens.TypedToken;
import org.carrot2.util.tokenizer.parser.jflex.JFlexWordBasedParser;


/**
 * Test JavaCC tokenizer definition and the Carrot2 wrapper.
 */
public class WordBasedParserTest
    extends TestCase
{
    public WordBasedParserTest(String s)
    {
        super(s);
    }

    private static class TokenImage
    {
        int type;
        String image;

        public TokenImage(String image, int type)
        {
            this.type = type;
            this.image = image;
        }
    }
    
    public void testBasicTokenization() {
        WordBasedParserBase parser = new JFlexWordBasedParser();
        String TEST = "Abecadło 10 dweiss@man.poznan.pl, chyba.";
        parser.restartTokenizationOn(new StringReader(TEST));
        
        org.carrot2.core.linguistic.tokens.Token [] tokens 
            = new org.carrot2.core.linguistic.tokens.Token[20];
        
        int howmany = parser.getNextTokens(tokens, 0);

        assertTokensEqual( tokens, new TokenImage [] {
            new TokenImage("Abecadło", TypedToken.TOKEN_TYPE_TERM),
            new TokenImage("10", TypedToken.TOKEN_TYPE_NUMERIC),
            new TokenImage("dweiss@man.poznan.pl", TypedToken.TOKEN_TYPE_SYMBOL),
            new TokenImage(",", TypedToken.TOKEN_TYPE_PUNCTUATION),
            new TokenImage("chyba", TypedToken.TOKEN_TYPE_TERM),
            new TokenImage(".", TypedToken.TOKEN_TYPE_PUNCTUATION | TypedToken.TOKEN_FLAG_SENTENCE_DELIM)
        });
        
        assertEquals( 6, howmany);
        
        parser.reuse();
    }

    public void testAdvancedTokenization() {
        WordBasedParserBase parser = new JFlexWordBasedParser();
        String TEST = "hyphen-term hyphen- term";
        parser.restartTokenizationOn(new StringReader(TEST));
        
        org.carrot2.core.linguistic.tokens.Token [] tokens 
            = new org.carrot2.core.linguistic.tokens.Token[20];
        
        int howmany = parser.getNextTokens(tokens, 0);

        assertTokensEqual(tokens, new TokenImage []
        { 
              new TokenImage("hyphen-term", TypedToken.TOKEN_TYPE_TERM),
              // TODO: This is accepted as a symbol? Should it?
              new TokenImage("hyphen-", TypedToken.TOKEN_TYPE_SYMBOL),
              new TokenImage("term", TypedToken.TOKEN_TYPE_TERM) 
        });
        
        assertEquals(3, howmany);
        
        parser.reuse();
    }

    private void assertTokensEqual(org.carrot2.core.linguistic.tokens.Token [] tokens, TokenImage [] images) {
        StringBuffer buf = new StringBuffer();
        for (int i=0;i<images.length;i++) {
            buf.setLength(0);
            tokens[i].appendTo(buf);
            assertEquals("Images not equal: "
                    + images[i].image + " " + buf.toString(), images[i].image, buf.toString());
            assertEquals("Types not equal: ", images[i].type, ((TypedToken) tokens[i]).getType());
        }
    }
}
