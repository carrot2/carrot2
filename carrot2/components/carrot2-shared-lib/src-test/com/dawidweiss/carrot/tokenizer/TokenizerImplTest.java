

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.dawidweiss.carrot.tokenizer;


import junit.framework.TestCase;


/**
 * Test JavaCC tokenizer definition and the Carrot2 wrapper.
 */
public class TokenizerImplTest
    extends TestCase
{
    public TokenizerImplTest(String s)
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
                case Tokenizer.TYPE_EMAIL:
                    typeName = "EMAIL";

                    break;

                case Tokenizer.TYPE_TERM:
                    typeName = "TERM";

                    break;

                case Tokenizer.TYPE_URL:
                    typeName = "URL";

                    break;

                case Tokenizer.TYPE_SENTENCEMARKER:
                    typeName = "SENTENCEMARKER";

                    break;

                default:
                    typeName = "UNRECOGNIZED?!";
            }

            return "[" + typeName + "]" + this.image;
        }
    }

    public void test_Tokenizer_TYPE_TERM()
    {
        String test = " simple terms: simpleterm 9numterm numerm99x \"quoted string\"";
        TokenImage [] tokens = 
        {
            new TokenImage("simple", Tokenizer.TYPE_TERM),
            new TokenImage("terms", Tokenizer.TYPE_TERM),
            new TokenImage("simpleterm", Tokenizer.TYPE_TERM),
            new TokenImage("9numterm", Tokenizer.TYPE_TERM),
            new TokenImage("numerm99x", Tokenizer.TYPE_TERM),
            new TokenImage("quoted", Tokenizer.TYPE_TERM),
            new TokenImage("string", Tokenizer.TYPE_TERM)
        };

        compareTokenArrays(test, tokens);
    }


    public void test_Tokenizer_TYPE_EMAIL()
    {
        String test = "e-mails: dweiss@go2.pl dawid.weiss@go2.com.pl bubu@some-host.com me@me.org bubu99@yahoo.com";
        TokenImage [] tokens = 
        {
            new TokenImage("e-mails", Tokenizer.TYPE_TERM),
            new TokenImage("dweiss@go2.pl", Tokenizer.TYPE_EMAIL),
            new TokenImage("dawid.weiss@go2.com.pl", Tokenizer.TYPE_EMAIL),
            new TokenImage("bubu@some-host.com", Tokenizer.TYPE_EMAIL),
            new TokenImage("me@me.org", Tokenizer.TYPE_EMAIL),
            new TokenImage("bubu99@yahoo.com", Tokenizer.TYPE_EMAIL)
        };

        compareTokenArrays(test, tokens);
    }


    public void test_Tokenizer_TYPE_URL()
    {
        String test =
            " urls: http://www.google.com http://www.cs.put.poznan.pl/index.jsp?query=term&query2=term "
            + " ftp://ftp.server";
        TokenImage [] tokens = 
        {
            new TokenImage("urls", Tokenizer.TYPE_TERM),
            new TokenImage("http://www.google.com", Tokenizer.TYPE_URL),
            new TokenImage(
                "http://www.cs.put.poznan.pl/index.jsp?query=term&query2=term", Tokenizer.TYPE_URL
            ), new TokenImage("ftp://ftp.server", Tokenizer.TYPE_URL)
        };

        compareTokenArrays(test, tokens);
    }


    public void test_Tokenizer_TYPE_PERSON()
    {
        String test = " O'J'Simpson and D.Weiss and D. Weiss and E.A.Bloober";
        TokenImage [] tokens = 
        {
            new TokenImage("O'J'Simpson", Tokenizer.TYPE_PERSON),
            new TokenImage("and", Tokenizer.TYPE_TERM),
            new TokenImage("D.Weiss", Tokenizer.TYPE_PERSON),
            new TokenImage("and", Tokenizer.TYPE_TERM),
            new TokenImage("D. Weiss", Tokenizer.TYPE_PERSON),
            new TokenImage("and", Tokenizer.TYPE_TERM),
            new TokenImage("E.A.Bloober", Tokenizer.TYPE_PERSON)
        };

        compareTokenArrays(test, tokens);
    }


    public void test_Tokenizer_TYPE_TERM_acronyms()
    {
        String test = " acronyms: I.B.M. S.C. z o.o. AT&T garey&johnson&willet";
        TokenImage [] tokens = 
        {
            new TokenImage("acronyms", Tokenizer.TYPE_TERM),
            new TokenImage("I.B.M.", Tokenizer.TYPE_TERM),
            new TokenImage("S.C.", Tokenizer.TYPE_TERM),
            
            new TokenImage("z", Tokenizer.TYPE_TERM), new TokenImage("o.o.", Tokenizer.TYPE_TERM),
            
            new TokenImage("AT&T", Tokenizer.TYPE_TERM),
            new TokenImage("garey&johnson&willet", Tokenizer.TYPE_TERM),
        };

        compareTokenArrays(test, tokens);
    }


    private static void compareTokenArrays(String test, TokenImage [] expectedTokens)
    {
        Tokenizer t = Tokenizer.getTokenizer();
        int i = 0;
        t.restartTokenizerOn(test);

        int [] type = { 0 };

        while (true)
        {
            String timage = t.getNextToken(type);

            if (timage == null)
            {
                if (i == expectedTokens.length)
                {
                    return;
                }
                else
                {
                    fail("Not all tokens recognized, stopped at: " + expectedTokens[i]);
                }
            }
            else
            {
                assertEquals(expectedTokens[i], new TokenImage(timage, type[0]));
            }

            i++;
        }
    }
}
