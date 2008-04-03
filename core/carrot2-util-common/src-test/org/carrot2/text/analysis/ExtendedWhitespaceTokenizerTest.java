package org.carrot2.text.analysis;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.lucene.analysis.Token;
import org.junit.Test;

/**
 * Test {@link ExtendedWhitespaceTokenizer}.
 */
public class ExtendedWhitespaceTokenizerTest
{
    /**
     * Internal class for comparing sequences of tokens.
     */
    private static class TokenImage
    {
        final int type;
        final String image;

        public TokenImage(String image, int type)
        {
            this.type = type;
            this.image = image;
        }

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

        public String toString()
        {
            final String typeName = new TokenInfo(type).toString();
            return "[" + typeName + "] " + this.image;
        }
    }

    @Test
    public void TERM()
    {
        String test = " simple terms simpleterm 9numterm numerm99x \"quoted string\"";
        TokenImage [] tokens =
        {
            new TokenImage("simple", TokenInfo.TERM),
            new TokenImage("terms", TokenInfo.TERM),
            new TokenImage("simpleterm", TokenInfo.TERM),
            new TokenImage("9numterm", TokenInfo.TERM),
            new TokenImage("numerm99x", TokenInfo.TERM),
            new TokenImage("quoted", TokenInfo.TERM),
            new TokenImage("string", TokenInfo.TERM)
        };

        assertEqualTokens(test, tokens);
    }

    @Test
    public void SYMBOL()
    {
        String test = " ...  S_NI_P token";
        TokenImage [] tokens =
        {
            new TokenImage("...", TokenInfo.PUNCTUATION | TokenInfo.SENTENCEMARKER),
            new TokenImage("S_NI_P", TokenInfo.FILE),
            new TokenImage("token", TokenInfo.TERM)
        };

        assertEqualTokens(test, tokens);
    }

    @Test
    public void EMAIL()
    {
        String test = "e-mails dweiss@go2.pl dawid.weiss@go2.com.pl bubu@some-host.com me@me.org bubu99@yahoo.com";
        TokenImage [] tokens =
        {
            new TokenImage("e-mails", TokenInfo.HYPHTERM),
            new TokenImage("dweiss@go2.pl", TokenInfo.EMAIL),
            new TokenImage("dawid.weiss@go2.com.pl", TokenInfo.EMAIL),
            new TokenImage("bubu@some-host.com", TokenInfo.EMAIL),
            new TokenImage("me@me.org", TokenInfo.EMAIL),
            new TokenImage("bubu99@yahoo.com", TokenInfo.EMAIL)
        };

        assertEqualTokens(test, tokens);
    }

    @Test
    public void URL()
    {
        String test = " urls http://www.google.com http://www.cs.put.poznan.pl/index.jsp?query=term&query2=term "
            + " ftp://ftp.server.pl www.google.com   not.an.url   go2.pl/mail http://www.digimine.com/usama/datamine/.";
        TokenImage [] tokens =
        {
            new TokenImage("urls", TokenInfo.TERM),
            new TokenImage("http://www.google.com", TokenInfo.FULL_URL),
            new TokenImage(
                "http://www.cs.put.poznan.pl/index.jsp?query=term&query2=term",
                TokenInfo.FULL_URL),
            new TokenImage("ftp://ftp.server.pl", TokenInfo.FULL_URL),
            new TokenImage("www.google.com", TokenInfo.BARE_URL),

            new TokenImage("not.an.url", TokenInfo.FILE),

            new TokenImage("go2.pl/mail", TokenInfo.FULL_URL),

            new TokenImage("http://www.digimine.com/usama/datamine/.", TokenInfo.FULL_URL),
        };

        assertEqualTokens(test, tokens);
    }

    @Test
    public void ACRONYM()
    {
        String test = " acronyms I.B.M. S.C. z o.o. AT&T garey&johnson&willet";
        TokenImage [] tokens =
        {
            new TokenImage("acronyms", TokenInfo.TERM),
            new TokenImage("I.B.M.", TokenInfo.ACRONYM),
            new TokenImage("S.C.", TokenInfo.ACRONYM),

            new TokenImage("z", TokenInfo.TERM),
            new TokenImage("o.o.", TokenInfo.ACRONYM),

            new TokenImage("AT&T", TokenInfo.ACRONYM),
            new TokenImage("garey&johnson&willet", TokenInfo.ACRONYM),
        };

        assertEqualTokens(test, tokens);
    }

    @Test
    public void NUMERIC()
    {
        String test = " numeric 127 0 12.87 12,12 12-2003/23 term2003 2003term ";
        TokenImage [] tokens =
        {
            new TokenImage("numeric", TokenInfo.TERM),

            new TokenImage("127", TokenInfo.NUMERIC),
            new TokenImage("0", TokenInfo.NUMERIC),
            new TokenImage("12.87", TokenInfo.NUMERIC),
            new TokenImage("12,12", TokenInfo.NUMERIC),
            new TokenImage("12-2003/23", TokenInfo.NUMERIC),
            new TokenImage("term2003", TokenInfo.TERM),
            new TokenImage("2003term", TokenInfo.TERM)

        };

        assertEqualTokens(test, tokens);
    }

    @Test
    public void NASTY_URL_1()
    {
        String test = "http://r.office.microsoft.com/r/rlidLiveMeeting?p1=7&amp;p2=en_US&amp;p3=LMInfo&amp;p4=DownloadWindowsConsole "
            + "https://www.livemeeting.com/cc/askme/join?id=58937J&amp;role=present&amp;pw=mNjC%27%25%3D%218";
        TokenImage [] tokens =
        {
            new TokenImage(
                "http://r.office.microsoft.com/r/rlidLiveMeeting?p1=7&amp;p2=en_US&amp;p3=LMInfo&amp;p4=DownloadWindowsConsole",
                TokenInfo.FULL_URL),
            new TokenImage(
                "https://www.livemeeting.com/cc/askme/join?id=58937J&amp;role=present&amp;pw=mNjC%27%25%3D%218",
                TokenInfo.FULL_URL),
        };

        assertEqualTokens(test, tokens);
    }

    @Test
    public void testKoreanWordSplit()
    {
        String test = "안녕하세요 한글입니다";
        TokenImage [] tokens =
        {
            new TokenImage("안녕하세요", TokenInfo.TERM),
            new TokenImage("한글입니다", TokenInfo.TERM),
        };

        assertEqualTokens(test, tokens);
    }

    @Test
    public void punctuationAndSentenceMarkers()
    {
        String test = "Dawid Weiss, Data Mining!";
        TokenImage [] tokens =
        {
            new TokenImage("Dawid", TokenInfo.TERM),
            new TokenImage("Weiss", TokenInfo.TERM),
            new TokenImage(",", TokenInfo.PUNCTUATION),
            new TokenImage("Data", TokenInfo.TERM),
            new TokenImage("Mining", TokenInfo.TERM),
            new TokenImage("!", TokenInfo.PUNCTUATION | TokenInfo.SENTENCEMARKER)
        };

        assertEqualTokens(test, tokens);
    }    
    
    /**
     * Compare expected and produced token sequences.
     */
    private static void assertEqualTokens(String testString, TokenImage [] expectedTokens)
    {
        try
        {
            final ExtendedWhitespaceTokenizer tokenizer = new ExtendedWhitespaceTokenizer(
                new StringReader(testString));

            final ArrayList<TokenImage> tokens = new ArrayList<TokenImage>();
            Token token;
            while ((token = tokenizer.next()) != null)
            {
                final String image = new String(token.termBuffer(), 0, token.termLength());
                tokens.add(new TokenImage(image, tokenizer.getLastTokenInfo().getRawTokenInfo()));
            }

            org.junit.Assert.assertArrayEquals(expectedTokens, tokens.toArray());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
