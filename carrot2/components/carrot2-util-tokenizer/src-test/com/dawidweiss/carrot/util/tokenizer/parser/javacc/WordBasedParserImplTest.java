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
package com.dawidweiss.carrot.util.tokenizer.parser.javacc;

import java.io.*;

import junit.framework.*;

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
                case JavaCCWordBasedParserImplConstants.EMAIL:
                    typeName = "EMAIL";
                    break;

                case JavaCCWordBasedParserImplConstants.ACRONYM:
                    typeName = "ACRONYM";
                    break;

                case JavaCCWordBasedParserImplConstants.TERM:
                    typeName = "TERM";
                    break;

                case JavaCCWordBasedParserImplConstants.HYPHTERM:
                    typeName = "HYPHTERM";
                    break;

                case JavaCCWordBasedParserImplConstants.URL:
                    typeName = "URL";

                    break;

                case JavaCCWordBasedParserImplConstants.SENTENCEMARKER:
                    typeName = "SENTENCEMARKER";

                    break;
                case JavaCCWordBasedParserImplConstants.PUNCTUATION:
                    typeName = "PUNCTUATION";
                    break;
                    
                case JavaCCWordBasedParserImplConstants.NUMERIC:
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
            new TokenImage("simple", JavaCCWordBasedParserImplConstants.TERM),
            new TokenImage("terms", JavaCCWordBasedParserImplConstants.TERM),
            new TokenImage("simpleterm", JavaCCWordBasedParserImplConstants.TERM),
            new TokenImage("9numterm", JavaCCWordBasedParserImplConstants.TERM),
            new TokenImage("numerm99x", JavaCCWordBasedParserImplConstants.TERM),
            new TokenImage("quoted", JavaCCWordBasedParserImplConstants.TERM),
            new TokenImage("string", JavaCCWordBasedParserImplConstants.TERM)
        };

        compareTokenArrays(test, tokens);
    }


    public void test_Tokenizer_TYPE_EMAIL()
    {
        String test = "e-mails dweiss@go2.pl dawid.weiss@go2.com.pl bubu@some-host.com me@me.org bubu99@yahoo.com";
        TokenImage [] tokens = 
        {
            new TokenImage("e-mails", JavaCCWordBasedParserImplConstants.HYPHTERM),
            new TokenImage("dweiss@go2.pl", JavaCCWordBasedParserImplConstants.EMAIL),
            new TokenImage("dawid.weiss@go2.com.pl", JavaCCWordBasedParserImplConstants.EMAIL),
            new TokenImage("bubu@some-host.com", JavaCCWordBasedParserImplConstants.EMAIL),
            new TokenImage("me@me.org", JavaCCWordBasedParserImplConstants.EMAIL),
            new TokenImage("bubu99@yahoo.com", JavaCCWordBasedParserImplConstants.EMAIL)
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
            new TokenImage("urls", JavaCCWordBasedParserImplConstants.TERM),
            new TokenImage("http://www.google.com", JavaCCWordBasedParserImplConstants.URL),
            new TokenImage(
                "http://www.cs.put.poznan.pl/index.jsp?query=term&query2=term", JavaCCWordBasedParserImplConstants.URL), 
            new TokenImage("ftp://ftp.server", JavaCCWordBasedParserImplConstants.URL),
            new TokenImage("www.google.com", JavaCCWordBasedParserImplConstants.URL),
            
            new TokenImage("not", JavaCCWordBasedParserImplConstants.TERM),
            new TokenImage(".", JavaCCWordBasedParserImplConstants.SENTENCEMARKER),
            new TokenImage("an", JavaCCWordBasedParserImplConstants.TERM),
            new TokenImage(".", JavaCCWordBasedParserImplConstants.SENTENCEMARKER),
            new TokenImage("url", JavaCCWordBasedParserImplConstants.TERM),
            
            new TokenImage("go2.pl/mail", JavaCCWordBasedParserImplConstants.URL),
            
            new TokenImage("http://www.digimine.com/usama/datamine/", JavaCCWordBasedParserImplConstants.URL),
            new TokenImage(".", JavaCCWordBasedParserImplConstants.SENTENCEMARKER)
        };

        compareTokenArrays(test, tokens);
    }


    public void test_Tokenizer_TYPE_TERM_acronyms()
    {
        String test = " acronyms I.B.M. S.C. z o.o. AT&T garey&johnson&willet";
        TokenImage [] tokens = 
        {
            new TokenImage("acronyms", JavaCCWordBasedParserImplConstants.TERM),
            new TokenImage("I.B.M.", JavaCCWordBasedParserImplConstants.ACRONYM),
            new TokenImage("S.C.", JavaCCWordBasedParserImplConstants.ACRONYM),
            
            new TokenImage("z", JavaCCWordBasedParserImplConstants.TERM), new TokenImage("o.o.", JavaCCWordBasedParserImplConstants.ACRONYM),
            
            new TokenImage("AT&T", JavaCCWordBasedParserImplConstants.ACRONYM),
            new TokenImage("garey&johnson&willet", JavaCCWordBasedParserImplConstants.ACRONYM),
        };

        compareTokenArrays(test, tokens);
    }

    public void test_Tokenizer_TYPE_NUMERIC()
    {
        String test = " numeric 127 0 12.87 12,12 12-2003/23 term2003 2003term ";
        TokenImage [] tokens = 
        {
            new TokenImage("numeric", JavaCCWordBasedParserImplConstants.TERM),

            new TokenImage("127", JavaCCWordBasedParserImplConstants.NUMERIC),
            new TokenImage("0", JavaCCWordBasedParserImplConstants.NUMERIC),
            new TokenImage("12.87", JavaCCWordBasedParserImplConstants.NUMERIC),
            new TokenImage("12,12", JavaCCWordBasedParserImplConstants.NUMERIC),
            new TokenImage("12-2003/23", JavaCCWordBasedParserImplConstants.NUMERIC),
            new TokenImage("term2003", JavaCCWordBasedParserImplConstants.TERM),
            new TokenImage("2003term", JavaCCWordBasedParserImplConstants.TERM)
            
        };

        compareTokenArrays(test, tokens);
    }
    
    /*
     * Convert token types to generic TypedToken values and recompare.
     */
    private static void compareTokenArrays(String test, TokenImage [] expectedTokens) {
        JavaCCWordBasedParserImpl parser = new JavaCCWordBasedParserImpl(new StringReader(test)); 

        int i = 0;
        while (true) {
            Token t;
            t = parser.getNextToken();
            if (t.kind == JavaCCWordBasedParserImplConstants.EOF)
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
