/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2020, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.language;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.carrot2.TestBase;
import org.junit.Test;

public class GlobDictionaryTest extends TestBase {
  @Test
  public void patternParser() {
    GlobDictionary.PatternParser parser = new GlobDictionary.PatternParser();
    Function<String, String> toString =
        (in) -> {
          try {
            return parser.parse(in).tokens().stream()
                .map(Objects::toString)
                .collect(Collectors.joining(","));
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        };

    // regular token.
    Assertions.assertThat(toString.apply("foo")).isEqualTo("foo");
    Assertions.assertThat(toString.apply("foo ")).isEqualTo("foo");
    Assertions.assertThat(toString.apply(" foo")).isEqualTo("foo");

    // verbatim token.
    Assertions.assertThat(toString.apply("\"Foo\"")).isEqualTo("'Foo'");
    Assertions.assertThat(toString.apply(" \"Foo\"")).isEqualTo("'Foo'");
    Assertions.assertThat(toString.apply("\"Foo\" ")).isEqualTo("'Foo'");

    // multiple tokens.
    Assertions.assertThat(toString.apply("foo bar")).isEqualTo("foo,bar");
    Assertions.assertThat(toString.apply("foo bar \"Bar\"")).isEqualTo("foo,bar,'Bar'");

    // wildcards.
    Assertions.assertThat(toString.apply("* bar")).isEqualTo("*,bar");

    // multiple wildcards in a sequence get optimized into a single one.
    Assertions.assertThat(toString.apply("* * bar * *")).isEqualTo("*,bar,*");

    // Quoted wildcard (verbatim match for the symbol).
    Assertions.assertThat(toString.apply("bar \"*\"")).isEqualTo("bar,'*'");

    // Whitespace inside the quoted token.
    Assertions.assertThat(toString.apply("\"foo bar\"")).isEqualTo("'foo bar'");

    // Quote inside token.
    Assertions.assertThat(toString.apply("foo\\\"BAR")).isEqualTo("foo\"BAR");

    // Neither of these should match.
    for (String invalidPattern :
        Arrays.asList(
            "\"unbalancedleft",
            " \"unbalancedleft",
            "unbalancedright\"",
            "unbalancedright\" ",
            "\"internal\"nquote\" ",
            "'unbalanced quote\"",
            "\"unbalanced quote'",
            "foo\"bar",
            "*foo",
            "foo*",
            "*",
            "")) {
      try {
        parser.parse(invalidPattern);
        Assertions.fail("Shouldn't parse: " + invalidPattern);
      } catch (ParseException e) {
        // Expected.
        System.out.println(e);
      }
    }
  }

  @Test
  public void empty() throws ParseException {
    dictionaryOf().doesNotMatchAny("missing");
  }

  @Test
  public void normalizedTerms() throws ParseException {
    dictionaryOf("foo bar")
        .matchesAll("foo bar", "FOO BAR", "Foo Bar")
        .doesNotMatchAny("foo", "bar");

    dictionaryOf("foo bar", "foo")
        .matchesAll("foo bar", "FOO BAR", "Foo Bar")
        .matchesAll("foo", "Foo", "FOO")
        .doesNotMatchAny("bar");

    dictionaryOf("yoga intervention").matchesAll("yoga intervention");

    dictionaryOf("* intervention").matchesAll("yoga intervention");
  }

  @Test
  public void verbatimTerms() throws ParseException {
    dictionaryOf("\"Upper\" Case")
        .matchesAll("Upper Case", "Upper case", "Upper CASE")
        .doesNotMatchAny("upper case", "UPPER CASE");

    dictionaryOf("\"Upper\" \"Case\"")
        .matchesAll("Upper Case")
        .doesNotMatchAny("upper case", "UPPER CASE", "Upper case");

    dictionaryOf("\"Upper\" \"Case\"", "case")
        .matchesAll("Upper Case", "case", "CASE")
        .doesNotMatchAny("upper case", "UPPER CASE", "Upper case");
  }

  @Test
  public void verbatimTermsSingleQuotes() throws ParseException {
    dictionaryOf("'Upper' Case")
        .matchesAll("Upper Case", "Upper case", "Upper CASE")
        .doesNotMatchAny("upper case", "UPPER CASE");

    dictionaryOf("'Upper' 'Case'")
        .matchesAll("Upper Case")
        .doesNotMatchAny("upper case", "UPPER CASE", "Upper case");

    dictionaryOf("'Upper' 'Case'", "case")
        .matchesAll("Upper Case", "case", "CASE")
        .doesNotMatchAny("upper case", "UPPER CASE", "Upper case");

    dictionaryOf("'odd\"quote'").matchesAll("odd\"quote").doesNotMatchAny("ODD\"quote");
    dictionaryOf("\"odd'quote\"").matchesAll("odd'quote").doesNotMatchAny("ODD'quote");
  }

  @Test
  public void leadingGlob() throws ParseException {
    dictionaryOf("* foo")
        .matchesAll("foo", "bar foo", "bar bar foo")
        .matchesAll("Foo", "bar Foo", "bar bar FOO");

    dictionaryOf("* \"FOO\"")
        .matchesAll("FOO", "bar FOO", "bar bar FOO")
        .doesNotMatchAny("foo", "bar foo", "bar bar Foo");
  }

  @Test
  public void trailingGlob() throws ParseException {
    dictionaryOf("foo *")
        .matchesAll("foo", "foo bar", "foo bar bar")
        .matchesAll("Foo", "Foo bar ", "FOO bar bar");

    dictionaryOf("\"FOO\" *")
        .matchesAll("FOO", "FOO bar", "FOO bar bar")
        .doesNotMatchAny("foo", "foo bar", "Foo bar bar");
  }

  @Test
  public void insideGlob() throws ParseException {
    dictionaryOf("foo * bar").matchesAll("foo bar", "foo xyz bar", "FOO xyz Bar");

    // Reluctant matching.
    dictionaryOf("foo * bar").doesNotMatchAny("foo bar bar");
    dictionaryOf("foo * bar bar").matchesAll("foo bar bar", "foo xyz bar bar");

    dictionaryOf("foo * bar * var")
        .matchesAll("foo bar var", "foo x bar y var")
        .doesNotMatchAny("foo bar var zzz");
  }

  @Test
  public void globInToken() throws ParseException {
    dictionaryOf("\"foo*\" bar").matchesAll("foo* bar", "foo* BAR");
  }

  @Test
  public void quoteInToken() throws ParseException {
    dictionaryOf("\\\"foo\\\" bar").matchesAll("\"foo\" bar", "\"FOO\" BAR");
  }

  private Stream<GlobDictionary.WordPattern> patternStream(String... patterns) {
    GlobDictionary.PatternParser p = new GlobDictionary.PatternParser();
    return Arrays.stream(patterns)
        .map(
            pat -> {
              try {
                return p.parse(pat);
              } catch (ParseException e) {
                throw new RuntimeException(e);
              }
            });
  }

  @Test
  public void tokenNormalization() throws Exception {
    Function<String, String> f = GlobDictionary.defaultTokenNormalization();
    Assertions.assertThat(f.apply("ABC")).isEqualTo("abc");
    Assertions.assertThat(f.apply("ŁÓDŹ żółw")).isEqualTo("łódź żółw");

    // We don't follow the rules for Turkish, so this isn't correct, but we require consistency.
    // Here's what the output of normalizing "Iiİı" looks like: "iii̇ı", but it contains combining
    // letters, which would require additional unicode canonization/ normalization; see
    // http://unicode.org/reports/tr15/
    Assertions.assertThat(f.apply("Iiİı"))
        .isEqualTo(
            new String(
                new int[] {'i', 'i', 'i', /* combining dot above */ 0x307, /* dotless i */ 0x131},
                0,
                5));
  }

  private DictionaryAssert<GlobDictionary> dictionaryOf(String... entries) throws ParseException {
    return DictionaryAssert.assertThat(new GlobDictionary(parse(entries)));
  }

  private static Stream<GlobDictionary.WordPattern> parse(String[] patterns) {
    GlobDictionary.PatternParser parser = new GlobDictionary.PatternParser();
    return Arrays.stream(patterns)
        .map((line) -> line.trim())
        .filter((line) -> !line.isEmpty() && !line.startsWith("#"))
        .map(
            (pattern) -> {
              try {
                return parser.parse(pattern);
              } catch (ParseException e) {
                throw new RuntimeException(e);
              }
            });
  }
}
