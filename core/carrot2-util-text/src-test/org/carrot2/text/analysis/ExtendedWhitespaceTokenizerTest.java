package org.carrot2.text.analysis;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;
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
            final String rawType = "0x" + Integer.toHexString(type);
            return "[" + rawType + "] " + this.image;
        }
    }

    @Test
    public void TERM()
    {
        String test = " simple terms simpleterm 9numterm numerm99x \"quoted string\"";
        TokenImage [] tokens =
        {
            new TokenImage("simple", TokenType.TT_TERM),
            new TokenImage("terms", TokenType.TT_TERM),
            new TokenImage("simpleterm", TokenType.TT_TERM),
            new TokenImage("9numterm", TokenType.TT_TERM),
            new TokenImage("numerm99x", TokenType.TT_TERM),
            new TokenImage("quoted", TokenType.TT_TERM),
            new TokenImage("string", TokenType.TT_TERM)
        };

        assertEqualTokens(test, tokens);
    }

    @Test
    public void SYMBOL()
    {
        String test = " ...  S_NI_P token";
        TokenImage [] tokens =
        {
            new TokenImage("...", TokenType.TT_PUNCTUATION | TokenType.TF_SEPARATOR_SENTENCE),
            new TokenImage("S_NI_P", TokenType.TT_FILE),
            new TokenImage("token", TokenType.TT_TERM)
        };

        assertEqualTokens(test, tokens);
    }

    @Test
    public void EMAIL()
    {
        String test = "e-mails dweiss@go2.pl dawid.weiss@go2.com.pl bubu@some-host.com me@me.org bubu99@yahoo.com";
        TokenImage [] tokens =
        {
            new TokenImage("e-mails", TokenType.TT_HYPHTERM),
            new TokenImage("dweiss@go2.pl", TokenType.TT_EMAIL),
            new TokenImage("dawid.weiss@go2.com.pl", TokenType.TT_EMAIL),
            new TokenImage("bubu@some-host.com", TokenType.TT_EMAIL),
            new TokenImage("me@me.org", TokenType.TT_EMAIL),
            new TokenImage("bubu99@yahoo.com", TokenType.TT_EMAIL)
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
            new TokenImage("urls", TokenType.TT_TERM),
            new TokenImage("http://www.google.com", TokenType.TT_FULL_URL),
            new TokenImage(
                "http://www.cs.put.poznan.pl/index.jsp?query=term&query2=term",
                TokenType.TT_FULL_URL),
            new TokenImage("ftp://ftp.server.pl", TokenType.TT_FULL_URL),
            new TokenImage("www.google.com", TokenType.TT_BARE_URL),

            new TokenImage("not.an.url", TokenType.TT_FILE),

            new TokenImage("go2.pl/mail", TokenType.TT_FULL_URL),

            new TokenImage("http://www.digimine.com/usama/datamine/.", TokenType.TT_FULL_URL),
        };

        assertEqualTokens(test, tokens);
    }

    @Test
    public void ACRONYM()
    {
        String test = " acronyms I.B.M. S.C. z o.o. AT&T garey&johnson&willet";
        TokenImage [] tokens =
        {
            new TokenImage("acronyms", TokenType.TT_TERM),
            new TokenImage("I.B.M.", TokenType.TT_ACRONYM),
            new TokenImage("S.C.", TokenType.TT_ACRONYM),

            new TokenImage("z", TokenType.TT_TERM),
            new TokenImage("o.o.", TokenType.TT_ACRONYM),

            new TokenImage("AT&T", TokenType.TT_ACRONYM),
            new TokenImage("garey&johnson&willet", TokenType.TT_ACRONYM),
        };

        assertEqualTokens(test, tokens);
    }

    @Test
    public void NUMERIC()
    {
        String test = " numeric 127 0 12.87 12,12 12-2003/23 term2003 2003term ";
        TokenImage [] tokens =
        {
            new TokenImage("numeric", TokenType.TT_TERM),

            new TokenImage("127", TokenType.TT_NUMERIC),
            new TokenImage("0", TokenType.TT_NUMERIC),
            new TokenImage("12.87", TokenType.TT_NUMERIC),
            new TokenImage("12,12", TokenType.TT_NUMERIC),
            new TokenImage("12-2003/23", TokenType.TT_NUMERIC),
            new TokenImage("term2003", TokenType.TT_TERM),
            new TokenImage("2003term", TokenType.TT_TERM)

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
                TokenType.TT_FULL_URL),
            new TokenImage(
                "https://www.livemeeting.com/cc/askme/join?id=58937J&amp;role=present&amp;pw=mNjC%27%25%3D%218",
                TokenType.TT_FULL_URL),
        };

        assertEqualTokens(test, tokens);
    }

    @Test
    public void testKoreanWordSplit()
    {
        String test = "안녕하세요 한글입니다";
        TokenImage [] tokens =
        {
            new TokenImage("안녕하세요", TokenType.TT_TERM),
            new TokenImage("한글입니다", TokenType.TT_TERM),
        };

        assertEqualTokens(test, tokens);
    }

    @Test
    public void punctuationAndSentenceMarkers()
    {
        String test = "Dawid Weiss, Data Mining!";
        TokenImage [] tokens =
        {
            new TokenImage("Dawid", TokenType.TT_TERM),
            new TokenImage("Weiss", TokenType.TT_TERM),
            new TokenImage(",", TokenType.TT_PUNCTUATION),
            new TokenImage("Data", TokenType.TT_TERM),
            new TokenImage("Mining", TokenType.TT_TERM),
            new TokenImage("!", TokenType.TT_PUNCTUATION | TokenType.TF_SEPARATOR_SENTENCE)
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
            final Tokenizer tokenizer = new ExtendedWhitespaceTokenizer(
                new StringReader(testString));

            final ArrayList<TokenImage> tokens = new ArrayList<TokenImage>();
            Token token;
            while ((token = tokenizer.next()) != null)
            {
                final String image = new String(token.termBuffer(), 0, token.termLength());
                final TokenType payload = (TokenType) token.getPayload();

                tokens.add(new TokenImage(image, payload.getRawFlags()));
            }

            org.junit.Assert.assertArrayEquals(expectedTokens, tokens.toArray());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
