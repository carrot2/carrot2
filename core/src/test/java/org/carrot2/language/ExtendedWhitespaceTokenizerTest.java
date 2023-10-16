/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2023, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.language;

import org.junit.Test;

/** Test {@link ExtendedWhitespaceTokenizer}. */
public class ExtendedWhitespaceTokenizerTest extends TokenizerTestBase {
  @Override
  protected Tokenizer createTokenStream() {
    return new ExtendedWhitespaceTokenizer();
  }

  @Test
  public void testTermTokens() {
    String test = " simple simple's simples` terms simpleterm 9numterm numerm99x \"quoted string\"";
    TokenImage[] tokens = {
      new TokenImage("simple", Tokenizer.TT_TERM),
      new TokenImage("simple's", Tokenizer.TT_TERM),
      new TokenImage("simples`", Tokenizer.TT_TERM),
      new TokenImage("terms", Tokenizer.TT_TERM),
      new TokenImage("simpleterm", Tokenizer.TT_TERM),
      new TokenImage("9numterm", Tokenizer.TT_TERM),
      new TokenImage("numerm99x", Tokenizer.TT_TERM),
      new TokenImage("quoted", Tokenizer.TT_TERM),
      new TokenImage("string", Tokenizer.TT_TERM)
    };

    assertEqualTokens(test, tokens);
  }

  @Test
  public void testSymbolTokens() {
    String test = " ...  S_NI_P token";
    TokenImage[] tokens = {
      new TokenImage("...", Tokenizer.TT_PUNCTUATION | Tokenizer.TF_SEPARATOR_SENTENCE),
      new TokenImage("S_NI_P", Tokenizer.TT_FILE),
      new TokenImage("token", Tokenizer.TT_TERM)
    };

    assertEqualTokens(test, tokens);
  }

  @Test
  public void testEmailTokens() {
    String test =
        "e-mails dweiss@go2.pl dawid.weiss@go2.com.pl bubu@some-host.com me@me.org bubu99@yahoo.com";
    TokenImage[] tokens = {
      new TokenImage("e-mails", Tokenizer.TT_HYPHTERM),
      new TokenImage("dweiss@go2.pl", Tokenizer.TT_EMAIL),
      new TokenImage("dawid.weiss@go2.com.pl", Tokenizer.TT_EMAIL),
      new TokenImage("bubu@some-host.com", Tokenizer.TT_EMAIL),
      new TokenImage("me@me.org", Tokenizer.TT_EMAIL),
      new TokenImage("bubu99@yahoo.com", Tokenizer.TT_EMAIL)
    };

    assertEqualTokens(test, tokens);
  }

  @Test
  public void testUrlTokens() {
    final String allCharsUrl =
        "http://url.with.all.allowed.characters/abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ/!*'();:@&=+$,/?%#[]-_.~";
    String test =
        " urls http://www.google.com http://www.cs.put.poznan.pl/index.jsp?query=term&query2=term "
            + " ftp://ftp.server.pl www.google.com   not.an.url   go2.pl/mail http://www.digimine.com/usama/datamine/."
            + " http://www.herold.at/gelbe-seiten/krems-an-der-donau/lDk8q/yoga-krems-wachau-j%C3%BCrgen-ullrich/"
            + " "
            + allCharsUrl;
    TokenImage[] tokens = {
      new TokenImage("urls", Tokenizer.TT_TERM),
      new TokenImage("http://www.google.com", Tokenizer.TT_FULL_URL),
      new TokenImage(
          "http://www.cs.put.poznan.pl/index.jsp?query=term&query2=term", Tokenizer.TT_FULL_URL),
      new TokenImage("ftp://ftp.server.pl", Tokenizer.TT_FULL_URL),
      new TokenImage("www.google.com", Tokenizer.TT_BARE_URL),
      new TokenImage("not.an.url", Tokenizer.TT_FILE),
      new TokenImage("go2.pl/mail", Tokenizer.TT_FULL_URL),
      new TokenImage("http://www.digimine.com/usama/datamine/.", Tokenizer.TT_FULL_URL),
      new TokenImage(
          "http://www.herold.at/gelbe-seiten/krems-an-der-donau/lDk8q/yoga-krems-wachau-j%C3%BCrgen-ullrich/",
          Tokenizer.TT_FULL_URL),
      new TokenImage(allCharsUrl, Tokenizer.TT_FULL_URL),
    };

    assertEqualTokens(test, tokens);
  }

  @Test
  public void testAcronymTokens() {
    String test = " acronyms I.B.M. S.C. z o.o. AT&T garey&johnson&willet";
    TokenImage[] tokens = {
      new TokenImage("acronyms", Tokenizer.TT_TERM),
      new TokenImage("I.B.M.", Tokenizer.TT_ACRONYM),
      new TokenImage("S.C.", Tokenizer.TT_ACRONYM),
      new TokenImage("z", Tokenizer.TT_TERM),
      new TokenImage("o.o.", Tokenizer.TT_ACRONYM),
      new TokenImage("AT&T", Tokenizer.TT_ACRONYM),
      new TokenImage("garey&johnson&willet", Tokenizer.TT_ACRONYM),
    };

    assertEqualTokens(test, tokens);
  }

  @Test
  public void testNumericTokens() {
    String test = " numeric 127 0 12.87 12,12 12-2003/23 term2003 2003term ";
    TokenImage[] tokens = {
      new TokenImage("numeric", Tokenizer.TT_TERM),
      new TokenImage("127", Tokenizer.TT_NUMERIC),
      new TokenImage("0", Tokenizer.TT_NUMERIC),
      new TokenImage("12.87", Tokenizer.TT_NUMERIC),
      new TokenImage("12,12", Tokenizer.TT_NUMERIC),
      new TokenImage("12-2003/23", Tokenizer.TT_NUMERIC),
      new TokenImage("term2003", Tokenizer.TT_TERM),
      new TokenImage("2003term", Tokenizer.TT_TERM)
    };

    assertEqualTokens(test, tokens);
  }

  @Test
  public void testNastyUrlTokens() {
    String test =
        "http://r.office.microsoft.com/r/rlidLiveMeeting?p1=7&amp;p2=en_US&amp;p3=LMInfo&amp;p4=DownloadWindowsConsole "
            + "https://www.livemeeting.com/cc/askme/join?id=58937J&amp;role=present&amp;pw=mNjC%27%25%3D%218";
    TokenImage[] tokens = {
      new TokenImage(
          "http://r.office.microsoft.com/r/rlidLiveMeeting?p1=7&amp;p2=en_US&amp;p3=LMInfo&amp;p4=DownloadWindowsConsole",
          Tokenizer.TT_FULL_URL),
      new TokenImage(
          "https://www.livemeeting.com/cc/askme/join?id=58937J&amp;role=present&amp;pw=mNjC%27%25%3D%218",
          Tokenizer.TT_FULL_URL),
    };

    assertEqualTokens(test, tokens);
  }

  @Test
  public void testKoreanWordSplit() {
    String test = "안녕하세요 한글입니다";
    TokenImage[] tokens = {
      new TokenImage("안녕하세요", Tokenizer.TT_TERM), new TokenImage("한글입니다", Tokenizer.TT_TERM),
    };

    assertEqualTokens(test, tokens);
  }

  @Test
  public void punctuationAndSentenceMarkers() {
    String test = "Dawid Weiss, Data Mining!";
    TokenImage[] tokens = {
      new TokenImage("Dawid", Tokenizer.TT_TERM),
      new TokenImage("Weiss", Tokenizer.TT_TERM),
      new TokenImage(",", Tokenizer.TT_PUNCTUATION),
      new TokenImage("Data", Tokenizer.TT_TERM),
      new TokenImage("Mining", Tokenizer.TT_TERM),
      new TokenImage("!", Tokenizer.TT_PUNCTUATION | Tokenizer.TF_SEPARATOR_SENTENCE)
    };

    assertEqualTokens(test, tokens);
  }

  @Test
  public void uncommonPunctuation() {
    String test = "foo·bar";
    TokenImage[] tokens = {
      new TokenImage("foo", Tokenizer.TT_TERM),
      new TokenImage("·", Tokenizer.TT_PUNCTUATION | Tokenizer.TF_SEPARATOR_SENTENCE),
      new TokenImage("bar", Tokenizer.TT_TERM),
    };

    assertEqualTokens(test, tokens);
  }
}
