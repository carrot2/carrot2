

/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the license "carrot2.LICENSE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */


package com.dawidweiss.carrot.util.tokenizer.parser;


import java.io.StringReader;

import junit.framework.TestCase;


/**
 * Test JavaCC tokenizer definition and the Carrot2 wrapper.
 */
public class WordBasedParserImplTest
    extends TestCase
{
    public WordBasedParserImplTest(String s)
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

        public boolean equals(Object o)
        {
            if (o instanceof TokenImage)
            {
                if (
                    ((TokenImage) o).image.equals(this.image)
                        && (((TokenImage) o).type == this.type)
                )
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }


        public String toString()
        {
            String typeName;

            switch (type)
            {
                case WordBasedParserImplConstants.EMAIL:
                    typeName = "EMAIL";
                    break;

                case WordBasedParserImplConstants.ACRONYM:
                    typeName = "ACRONYM";
                    break;

                case WordBasedParserImplConstants.TERM:
                    typeName = "TERM";
                    break;

                case WordBasedParserImplConstants.HYPHTERM:
                    typeName = "HYPHTERM";
                    break;

                case WordBasedParserImplConstants.URL:
                    typeName = "URL";

                    break;

                case WordBasedParserImplConstants.SENTENCEMARKER:
                    typeName = "SENTENCEMARKER";

                    break;
                case WordBasedParserImplConstants.PUNCTUATION:
                    typeName = "PUNCTUATION";
                    break;
                    
                case WordBasedParserImplConstants.NUMERIC:
                    typeName = "NUMERIC";
                    break;

                default:
                    typeName = "UNRECOGNIZED?! = " + type;
            }

            return "[" + typeName + "]" + this.image;
        }
    }

    public void test_Tokenizer_TYPE_TERM()
    {
        String test = " simple terms simpleterm 9numterm numerm99x \"quoted string\"";
        TokenImage [] tokens = 
        {
            new TokenImage("simple", WordBasedParserImplConstants.TERM),
            new TokenImage("terms", WordBasedParserImplConstants.TERM),
            new TokenImage("simpleterm", WordBasedParserImplConstants.TERM),
            new TokenImage("9numterm", WordBasedParserImplConstants.TERM),
            new TokenImage("numerm99x", WordBasedParserImplConstants.TERM),
            new TokenImage("quoted", WordBasedParserImplConstants.TERM),
            new TokenImage("string", WordBasedParserImplConstants.TERM)
        };

        compareTokenArrays(test, tokens);
    }


    public void test_Tokenizer_TYPE_EMAIL()
    {
        String test = "e-mails dweiss@go2.pl dawid.weiss@go2.com.pl bubu@some-host.com me@me.org bubu99@yahoo.com";
        TokenImage [] tokens = 
        {
            new TokenImage("e-mails", WordBasedParserImplConstants.HYPHTERM),
            new TokenImage("dweiss@go2.pl", WordBasedParserImplConstants.EMAIL),
            new TokenImage("dawid.weiss@go2.com.pl", WordBasedParserImplConstants.EMAIL),
            new TokenImage("bubu@some-host.com", WordBasedParserImplConstants.EMAIL),
            new TokenImage("me@me.org", WordBasedParserImplConstants.EMAIL),
            new TokenImage("bubu99@yahoo.com", WordBasedParserImplConstants.EMAIL)
        };

        compareTokenArrays(test, tokens);
    }


    public void test_Tokenizer_TYPE_URL()
    {
        String test =
            " urls http://www.google.com http://www.cs.put.poznan.pl/index.jsp?query=term&query2=term "
            + " ftp://ftp.server www.google.com   not.an.url   go2.pl/mail http://www.digimine.com/usama/datamine/.";
        TokenImage [] tokens = 
        {
            new TokenImage("urls", WordBasedParserImplConstants.TERM),
            new TokenImage("http://www.google.com", WordBasedParserImplConstants.URL),
            new TokenImage(
                "http://www.cs.put.poznan.pl/index.jsp?query=term&query2=term", WordBasedParserImplConstants.URL), 
            new TokenImage("ftp://ftp.server", WordBasedParserImplConstants.URL),
            new TokenImage("www.google.com", WordBasedParserImplConstants.URL),
            
            new TokenImage("not", WordBasedParserImplConstants.TERM),
            new TokenImage(".", WordBasedParserImplConstants.SENTENCEMARKER),
            new TokenImage("an", WordBasedParserImplConstants.TERM),
            new TokenImage(".", WordBasedParserImplConstants.SENTENCEMARKER),
            new TokenImage("url", WordBasedParserImplConstants.TERM),
            
            new TokenImage("go2.pl/mail", WordBasedParserImplConstants.URL),
            
            new TokenImage("http://www.digimine.com/usama/datamine/", WordBasedParserImplConstants.URL),
            new TokenImage(".", WordBasedParserImplConstants.SENTENCEMARKER)
        };

        compareTokenArrays(test, tokens);
    }


    public void test_Tokenizer_TYPE_TERM_acronyms()
    {
        String test = " acronyms I.B.M. S.C. z o.o. AT&T garey&johnson&willet";
        TokenImage [] tokens = 
        {
            new TokenImage("acronyms", WordBasedParserImplConstants.TERM),
            new TokenImage("I.B.M.", WordBasedParserImplConstants.ACRONYM),
            new TokenImage("S.C.", WordBasedParserImplConstants.ACRONYM),
            
            new TokenImage("z", WordBasedParserImplConstants.TERM), new TokenImage("o.o.", WordBasedParserImplConstants.ACRONYM),
            
            new TokenImage("AT&T", WordBasedParserImplConstants.ACRONYM),
            new TokenImage("garey&johnson&willet", WordBasedParserImplConstants.ACRONYM),
        };

        compareTokenArrays(test, tokens);
    }

    public void test_Tokenizer_TYPE_NUMERIC()
    {
        String test = " numeric 127 0 12.87 12,12 12-2003/23 term2003 2003term ";
        TokenImage [] tokens = 
        {
            new TokenImage("numeric", WordBasedParserImplConstants.TERM),

            new TokenImage("127", WordBasedParserImplConstants.NUMERIC),
            new TokenImage("0", WordBasedParserImplConstants.NUMERIC),
            new TokenImage("12.87", WordBasedParserImplConstants.NUMERIC),
            new TokenImage("12,12", WordBasedParserImplConstants.NUMERIC),
            new TokenImage("12-2003/23", WordBasedParserImplConstants.NUMERIC),
            new TokenImage("term2003", WordBasedParserImplConstants.TERM),
            new TokenImage("2003term", WordBasedParserImplConstants.TERM)
            
        };

        compareTokenArrays(test, tokens);
    }
    
    /*
     * Convert token types to generic TypedToken values and recompare.
     */
    private static void compareTokenArrays(String test, TokenImage [] expectedTokens) {
        WordBasedParserImpl parser = new WordBasedParserImpl(new StringReader(test)); 

        int i = 0;
        while (true) {
            Token t;
            t = parser.getNextToken();
            if (t.kind == WordBasedParserImplConstants.EOF)
                break;

            int expectedType = expectedTokens[i].type;
            // check if token type matches the expected type.
            assertEquals("TypedToken type mismatch.",
                expectedType, t.kind);

            assertEquals("TypedToken image mismatch.",
                expectedTokens[i].image, t.image);

            i++;
        }
    }
}
