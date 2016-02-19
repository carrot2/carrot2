
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.linguistic;


import org.carrot2.text.analysis.ExtendedWhitespaceTokenizer;
import org.carrot2.text.analysis.ITokenizer;
import org.junit.Test;

/**
 * Test {@link ExtendedWhitespaceTokenizer}.
 */
public class ExtendedWhitespaceTokenizerTest extends TokenizerTestBase
{
    @Override
    protected ITokenizer createTokenStream()
    {
        return new ExtendedWhitespaceTokenizer();
    }

    @Test
    public void testTermTokens()
    {
        String test = " simple simple's simples` terms simpleterm 9numterm numerm99x \"quoted string\"";
        TokenImage [] tokens =
        {
            new TokenImage("simple", ITokenizer.TT_TERM),
            new TokenImage("simple's", ITokenizer.TT_TERM),
            new TokenImage("simples`", ITokenizer.TT_TERM),
            new TokenImage("terms", ITokenizer.TT_TERM),
            new TokenImage("simpleterm", ITokenizer.TT_TERM),
            new TokenImage("9numterm", ITokenizer.TT_TERM),
            new TokenImage("numerm99x", ITokenizer.TT_TERM),
            new TokenImage("quoted", ITokenizer.TT_TERM),
            new TokenImage("string", ITokenizer.TT_TERM)
        };

        assertEqualTokens(test, tokens);
    }

    @Test
    public void testSymbolTokens()
    {
        String test = " ...  S_NI_P token";
        TokenImage [] tokens =
        {
            new TokenImage("...", ITokenizer.TT_PUNCTUATION
                | ITokenizer.TF_SEPARATOR_SENTENCE),
            new TokenImage("S_NI_P", ITokenizer.TT_FILE),
            new TokenImage("token", ITokenizer.TT_TERM)
        };

        assertEqualTokens(test, tokens);
    }

    @Test
    public void testEmailTokens()
    {
        String test = "e-mails dweiss@go2.pl dawid.weiss@go2.com.pl bubu@some-host.com me@me.org bubu99@yahoo.com";
        TokenImage [] tokens =
        {
            new TokenImage("e-mails", ITokenizer.TT_HYPHTERM),
            new TokenImage("dweiss@go2.pl", ITokenizer.TT_EMAIL),
            new TokenImage("dawid.weiss@go2.com.pl", ITokenizer.TT_EMAIL),
            new TokenImage("bubu@some-host.com", ITokenizer.TT_EMAIL),
            new TokenImage("me@me.org", ITokenizer.TT_EMAIL),
            new TokenImage("bubu99@yahoo.com", ITokenizer.TT_EMAIL)
        };

        assertEqualTokens(test, tokens);
    }

    @Test
    public void testUrlTokens()
    {
        final String allCharsUrl = "http://url.with.all.allowed.characters/abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ/!*'();:@&=+$,/?%#[]-_.~";
        String test = " urls http://www.google.com http://www.cs.put.poznan.pl/index.jsp?query=term&query2=term "
            + " ftp://ftp.server.pl www.google.com   not.an.url   go2.pl/mail http://www.digimine.com/usama/datamine/."
            + " http://www.herold.at/gelbe-seiten/krems-an-der-donau/lDk8q/yoga-krems-wachau-j%C3%BCrgen-ullrich/"
            + " " + allCharsUrl;
        TokenImage [] tokens =
        {
            new TokenImage("urls", ITokenizer.TT_TERM),
            new TokenImage("http://www.google.com", ITokenizer.TT_FULL_URL),
            new TokenImage(
                "http://www.cs.put.poznan.pl/index.jsp?query=term&query2=term",
                ITokenizer.TT_FULL_URL),
            new TokenImage("ftp://ftp.server.pl", ITokenizer.TT_FULL_URL),
            new TokenImage("www.google.com", ITokenizer.TT_BARE_URL),

            new TokenImage("not.an.url", ITokenizer.TT_FILE),

            new TokenImage("go2.pl/mail", ITokenizer.TT_FULL_URL),

            new TokenImage("http://www.digimine.com/usama/datamine/.",
                ITokenizer.TT_FULL_URL),
            
            new TokenImage("http://www.herold.at/gelbe-seiten/krems-an-der-donau/lDk8q/yoga-krems-wachau-j%C3%BCrgen-ullrich/",
                ITokenizer.TT_FULL_URL),
                
            new TokenImage(allCharsUrl,
                ITokenizer.TT_FULL_URL),
        };

        assertEqualTokens(test, tokens);
    }

    @Test
    public void testAcronymTokens()
    {
        String test = " acronyms I.B.M. S.C. z o.o. AT&T garey&johnson&willet";
        TokenImage [] tokens =
        {
            new TokenImage("acronyms", ITokenizer.TT_TERM),
            new TokenImage("I.B.M.", ITokenizer.TT_ACRONYM),
            new TokenImage("S.C.", ITokenizer.TT_ACRONYM),

            new TokenImage("z", ITokenizer.TT_TERM),
            new TokenImage("o.o.", ITokenizer.TT_ACRONYM),

            new TokenImage("AT&T", ITokenizer.TT_ACRONYM),
            new TokenImage("garey&johnson&willet", ITokenizer.TT_ACRONYM),
        };

        assertEqualTokens(test, tokens);
    }

    @Test
    public void testNumericTokens()
    {
        String test = " numeric 127 0 12.87 12,12 12-2003/23 term2003 2003term ";
        TokenImage [] tokens =
        {
            new TokenImage("numeric", ITokenizer.TT_TERM),

            new TokenImage("127", ITokenizer.TT_NUMERIC),
            new TokenImage("0", ITokenizer.TT_NUMERIC),
            new TokenImage("12.87", ITokenizer.TT_NUMERIC),
            new TokenImage("12,12", ITokenizer.TT_NUMERIC),
            new TokenImage("12-2003/23", ITokenizer.TT_NUMERIC),
            new TokenImage("term2003", ITokenizer.TT_TERM),
            new TokenImage("2003term", ITokenizer.TT_TERM)

        };

        assertEqualTokens(test, tokens);
    }

    @Test
    public void testNastyUrlTokens()
    {
        String test = "http://r.office.microsoft.com/r/rlidLiveMeeting?p1=7&amp;p2=en_US&amp;p3=LMInfo&amp;p4=DownloadWindowsConsole "
            + "https://www.livemeeting.com/cc/askme/join?id=58937J&amp;role=present&amp;pw=mNjC%27%25%3D%218";
        TokenImage [] tokens =
        {
            new TokenImage(
                "http://r.office.microsoft.com/r/rlidLiveMeeting?p1=7&amp;p2=en_US&amp;p3=LMInfo&amp;p4=DownloadWindowsConsole",
                ITokenizer.TT_FULL_URL),
            new TokenImage(
                "https://www.livemeeting.com/cc/askme/join?id=58937J&amp;role=present&amp;pw=mNjC%27%25%3D%218",
                ITokenizer.TT_FULL_URL),
        };

        assertEqualTokens(test, tokens);
    }

    @Test
    public void testKoreanWordSplit()
    {
        String test = "안녕하세요 한글입니다";
        TokenImage [] tokens =
        {
            new TokenImage("안녕하세요", ITokenizer.TT_TERM),
            new TokenImage("한글입니다", ITokenizer.TT_TERM),
        };

        assertEqualTokens(test, tokens);
    }

    @Test
    public void punctuationAndSentenceMarkers()
    {
        String test = "Dawid Weiss, Data Mining!";
        TokenImage [] tokens =
        {
            new TokenImage("Dawid", ITokenizer.TT_TERM),
            new TokenImage("Weiss", ITokenizer.TT_TERM),
            new TokenImage(",", ITokenizer.TT_PUNCTUATION),
            new TokenImage("Data", ITokenizer.TT_TERM),
            new TokenImage("Mining", ITokenizer.TT_TERM),
            new TokenImage("!", ITokenizer.TT_PUNCTUATION
                | ITokenizer.TF_SEPARATOR_SENTENCE)
        };

        assertEqualTokens(test, tokens);
    }
}
