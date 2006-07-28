
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.tokenizer.parser.jflex;

import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;

/**
 * Test JavaCC tokenizer definition and the Carrot2 wrapper.
 */
public class JFlexWordBasedParserImplTest
    extends TestCase
{
    public JFlexWordBasedParserImplTest(String s)
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
                case JFlexWordBasedParserImpl.EMAIL:
                    typeName = "EMAIL";
                    break;

                case JFlexWordBasedParserImpl.ACRONYM:
                    typeName = "ACRONYM";
                    break;

                case JFlexWordBasedParserImpl.TERM:
                    typeName = "TERM";
                    break;

                case JFlexWordBasedParserImpl.HYPHTERM:
                    typeName = "HYPHTERM";
                    break;

                case JFlexWordBasedParserImpl.BARE_URL:
                    typeName = "URL";

                    break;

                case JFlexWordBasedParserImpl.SENTENCEMARKER:
                    typeName = "SENTENCEMARKER";

                    break;
                case JFlexWordBasedParserImpl.PUNCTUATION:
                    typeName = "PUNCTUATION";
                    break;
                    
                case JFlexWordBasedParserImpl.NUMERIC:
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
            new TokenImage("simple", JFlexWordBasedParserImpl.TERM),
            new TokenImage("terms", JFlexWordBasedParserImpl.TERM),
            new TokenImage("simpleterm", JFlexWordBasedParserImpl.TERM),
            new TokenImage("9numterm", JFlexWordBasedParserImpl.TERM),
            new TokenImage("numerm99x", JFlexWordBasedParserImpl.TERM),
            new TokenImage("quoted", JFlexWordBasedParserImpl.TERM),
            new TokenImage("string", JFlexWordBasedParserImpl.TERM)
        };

        compareTokenArrays(test, tokens);
    }

    public void test_Tokenizer_SYMBOL()
    {
        String test = " ...  S_NI_P token";
        TokenImage [] tokens = 
        {
            new TokenImage("...", JFlexWordBasedParserImpl.SENTENCEMARKER),
            new TokenImage("S_NI_P", JFlexWordBasedParserImpl.FILE),
            new TokenImage("token", JFlexWordBasedParserImpl.TERM)
        };

        compareTokenArrays(test, tokens);
    }

      
    
    public void test_Tokenizer_TYPE_EMAIL()
    {
        String test = "e-mails dweiss@go2.pl dawid.weiss@go2.com.pl bubu@some-host.com me@me.org bubu99@yahoo.com";
        TokenImage [] tokens = 
        {
            new TokenImage("e-mails", JFlexWordBasedParserImpl.HYPHTERM),
            new TokenImage("dweiss@go2.pl", JFlexWordBasedParserImpl.EMAIL),
            new TokenImage("dawid.weiss@go2.com.pl", JFlexWordBasedParserImpl.EMAIL),
            new TokenImage("bubu@some-host.com", JFlexWordBasedParserImpl.EMAIL),
            new TokenImage("me@me.org", JFlexWordBasedParserImpl.EMAIL),
            new TokenImage("bubu99@yahoo.com", JFlexWordBasedParserImpl.EMAIL)
        };

        compareTokenArrays(test, tokens);
    }


    public void test_Tokenizer_TYPE_URL()
    {
        String test =
            " urls http://www.google.com http://www.cs.put.poznan.pl/index.jsp?query=term&query2=term "
            + " ftp://ftp.server.pl www.google.com   not.an.url   go2.pl/mail http://www.digimine.com/usama/datamine/.";
        TokenImage [] tokens = 
        {
            new TokenImage("urls", JFlexWordBasedParserImpl.TERM),
            new TokenImage("http://www.google.com", JFlexWordBasedParserImpl.FULL_URL),
            new TokenImage(
                "http://www.cs.put.poznan.pl/index.jsp?query=term&query2=term", JFlexWordBasedParserImpl.FULL_URL), 
            new TokenImage("ftp://ftp.server.pl", JFlexWordBasedParserImpl.FULL_URL),
            new TokenImage("www.google.com", JFlexWordBasedParserImpl.BARE_URL),
            
            new TokenImage("not.an.url", JFlexWordBasedParserImpl.FILE),
            
            new TokenImage("go2.pl/mail", JFlexWordBasedParserImpl.FULL_URL),
            
            new TokenImage("http://www.digimine.com/usama/datamine/.", JFlexWordBasedParserImpl.FULL_URL),
        };

        compareTokenArrays(test, tokens);
    }


    public void test_Tokenizer_TYPE_TERM_acronyms()
    {
        String test = " acronyms I.B.M. S.C. z o.o. AT&T garey&johnson&willet";
        TokenImage [] tokens = 
        {
            new TokenImage("acronyms", JFlexWordBasedParserImpl.TERM),
            new TokenImage("I.B.M.", JFlexWordBasedParserImpl.ACRONYM),
            new TokenImage("S.C.", JFlexWordBasedParserImpl.ACRONYM),
            
            new TokenImage("z", JFlexWordBasedParserImpl.TERM), new TokenImage("o.o.", JFlexWordBasedParserImpl.ACRONYM),
            
            new TokenImage("AT&T", JFlexWordBasedParserImpl.ACRONYM),
            new TokenImage("garey&johnson&willet", JFlexWordBasedParserImpl.ACRONYM),
        };

        compareTokenArrays(test, tokens);
    }

    public void test_Tokenizer_TYPE_NUMERIC()
    {
        String test = " numeric 127 0 12.87 12,12 12-2003/23 term2003 2003term ";
        TokenImage [] tokens = 
        {
            new TokenImage("numeric", JFlexWordBasedParserImpl.TERM),

            new TokenImage("127", JFlexWordBasedParserImpl.NUMERIC),
            new TokenImage("0", JFlexWordBasedParserImpl.NUMERIC),
            new TokenImage("12.87", JFlexWordBasedParserImpl.NUMERIC),
            new TokenImage("12,12", JFlexWordBasedParserImpl.NUMERIC),
            new TokenImage("12-2003/23", JFlexWordBasedParserImpl.NUMERIC),
            new TokenImage("term2003", JFlexWordBasedParserImpl.TERM),
            new TokenImage("2003term", JFlexWordBasedParserImpl.TERM)
            
        };

        compareTokenArrays(test, tokens);
    }
    
    public void test_Tokenizer_NASTY_URL_1()
    {
        String test = "http://r.office.microsoft.com/r/rlidLiveMeeting?p1=7&amp;p2=en_US&amp;p3=LMInfo&amp;p4=DownloadWindowsConsole " +
                      "https://www.livemeeting.com/cc/askme/join?id=58937J&amp;role=present&amp;pw=mNjC%27%25%3D%218";
        TokenImage [] tokens = 
        {
            new TokenImage("http://r.office.microsoft.com/r/rlidLiveMeeting?p1=7&amp;p2=en_US&amp;p3=LMInfo&amp;p4=DownloadWindowsConsole", JFlexWordBasedParserImpl.FULL_URL),
            new TokenImage("https://www.livemeeting.com/cc/askme/join?id=58937J&amp;role=present&amp;pw=mNjC%27%25%3D%218", JFlexWordBasedParserImpl.FULL_URL),
        };
        
        compareTokenArrays(test, tokens);
    }
    
    /*
     * Convert token types to generic TypedToken values and recompare.
     */
    private static void compareTokenArrays(String test, TokenImage [] expectedTokens) {
        JFlexWordBasedParserImpl parser = new JFlexWordBasedParserImpl(new StringReader(test)); 

        int i = 0;
        while (true) {
            int t = -2;
            try
            {
                t = parser.getNextToken();
            }
            catch (IOException e)
            {
                new RuntimeException(e);
            }
            
            if (t == JFlexWordBasedParserImpl.YYEOF)
                break;

            int expectedType = expectedTokens[i].type;
            // check if token type matches the expected type.
            assertEquals("TypedToken type mismatch (" + expectedTokens[i].image + ")",
                expectedType, t);

            assertEquals("TypedToken image mismatch.",
                expectedTokens[i].image, parser.yytext());

            i++;
        }
    }
}
