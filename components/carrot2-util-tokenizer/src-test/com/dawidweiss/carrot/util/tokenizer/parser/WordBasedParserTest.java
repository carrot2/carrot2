
/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the license "carrot2.LICENSE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */

package com.dawidweiss.carrot.util.tokenizer.parser;


import java.io.*;

import junit.framework.*;

import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
import com.dawidweiss.carrot.util.tokenizer.parser.jflex.*;


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
        
        com.dawidweiss.carrot.core.local.linguistic.tokens.Token [] tokens 
            = new com.dawidweiss.carrot.core.local.linguistic.tokens.Token[20];
        
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
        
        com.dawidweiss.carrot.core.local.linguistic.tokens.Token [] tokens 
            = new com.dawidweiss.carrot.core.local.linguistic.tokens.Token[20];
        
        int howmany = parser.getNextTokens(tokens, 0);

        assertTokensEqual(tokens, new TokenImage []
        { 
              new TokenImage("hyphen-term", TypedToken.TOKEN_TYPE_TERM),
              new TokenImage("hyphen", TypedToken.TOKEN_TYPE_TERM),
              new TokenImage("-", TypedToken.TOKEN_TYPE_PUNCTUATION),
              new TokenImage("term", TypedToken.TOKEN_TYPE_TERM) 
        });
        
        assertEquals( 4, howmany);
        
        parser.reuse();
    }

    private void assertTokensEqual(com.dawidweiss.carrot.core.local.linguistic.tokens.Token [] tokens, TokenImage [] images) {
        StringBuffer buf = new StringBuffer();
        for (int i=0;i<images.length;i++) {
            buf.setLength(0);
            tokens[i].appendTo(buf);
            assertEquals("Images not equal: ", images[i].image, buf.toString());
            assertEquals("Types not equal: ", images[i].type, ((TypedToken) tokens[i]).getType());
        }
    }
}
