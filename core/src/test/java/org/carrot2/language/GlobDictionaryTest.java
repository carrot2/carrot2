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

import com.carrotsearch.randomizedtesting.annotations.TestCaseOrdering;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.carrot2.TestBase;
import org.junit.Test;

@TestCaseOrdering(TestCaseOrdering.AlphabeticOrder.class)
public class GlobDictionaryTest extends TestBase {
  @Test
  public void patternParser() {
    GlobDictionary.PatternParser parser =
        new GlobDictionary.PatternParser(
            Map.of(
                "type1", 1,
                "type2", 2));

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
    Assertions.assertThat(toString.apply("? bar")).isEqualTo("?,bar");

    // Quoted wildcard (verbatim match for the symbol).
    Assertions.assertThat(toString.apply("bar \"*\"")).isEqualTo("bar,'*'");

    // Whitespace inside the quoted token.
    Assertions.assertThat(toString.apply("\"foo bar\"")).isEqualTo("'foo bar'");

    // Quote inside token.
    Assertions.assertThat(toString.apply("foo\\\"BAR")).isEqualTo("foo\"BAR");

    // Types
    Assertions.assertThat(toString.apply("foo {type1}")).isEqualTo("foo,{type1}");
    Assertions.assertThat(toString.apply("foo {type1&type2}")).isEqualTo("foo,{type1&type2}");

    // Neither of these should match.
    for (String invalidPattern :
        Arrays.asList(
            // multiple consecutive wildcards are invalid.
            "* * bar * *",
            // unknown type.
            "{unknownType}",
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
            "??",
            "?",
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
  public void empty() {
    dictionaryOf().doesNotMatchAny("missing");
  }

  @Test
  public void normalizedTerms() {
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
  public void verbatimTerms() {
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
  public void verbatimTermsSingleQuotes() {
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
  public void leadingZeroOrMore() {
    dictionaryOf("* foo")
        .matchesAll("foo", "bar foo", "bar bar foo")
        .matchesAll("Foo", "bar Foo", "bar bar FOO");

    dictionaryOf("* \"FOO\"")
        .matchesAll("FOO", "bar FOO", "bar bar FOO")
        .doesNotMatchAny("foo", "bar foo", "bar bar Foo");
  }

  @Test
  public void trailingZeroOrMoreReluctant() {
    dictionaryOf("foo *?")
        .matchesAll("foo", "foo bar", "foo bar bar")
        .matchesAll("Foo", "Foo bar ", "FOO bar bar");

    dictionaryOf("\"FOO\" *?")
        .matchesAll("FOO", "FOO bar", "FOO bar bar")
        .doesNotMatchAny("foo", "foo bar", "Foo bar bar");
  }

  @Test
  public void trailingZeroOrMorePossessive() {
    dictionaryOf("foo *")
        .matchesAll("foo", "foo bar", "foo bar bar")
        .matchesAll("Foo", "Foo bar ", "FOO bar bar");

    dictionaryOf("\"FOO\" *")
        .matchesAll("FOO", "FOO bar", "FOO bar bar")
        .doesNotMatchAny("foo", "foo bar", "Foo bar bar");
  }

  @Test
  public void insideGlobReluctant() {
    dictionaryOf("foo *? bar").matchesAll("foo bar", "foo xyz bar", "FOO xyz Bar");

    // Reluctant matching.
    dictionaryOf("foo *? bar").doesNotMatchAny("foo bar bar");
    dictionaryOf("foo *? bar bar").matchesAll("foo bar bar", "foo xyz bar bar");

    dictionaryOf("foo *? bar *? var")
        .matchesAll("foo bar var", "foo x bar y var")
        .doesNotMatchAny("foo bar var zzz");
  }

  @Test
  public void insideGlobPossesive() {
    dictionaryOf("foo * bar").matchesAll("foo bar", "foo xyz bar", "FOO xyz Bar");

    // Possessive matching.
    dictionaryOf("foo * bar").matchesAll("foo bar bar");
    dictionaryOf("foo * bar bar").doesNotMatchAny("foo bar bar", "foo xyz bar bar");

    dictionaryOf("foo * bar * var")
        .matchesAll("foo bar var", "foo x y bar y z var")
        .doesNotMatchAny("foo bar var zzz");
  }

  @Test
  public void anyTokenWildcard() {
    dictionaryOf("? foo")
        .matchesAll("bar foo", "baz foo")
        .doesNotMatchAny("foo", "bar baz foo", "bar foo baz");

    dictionaryOf("? ? foo")
        .matchesAll("bar baz foo", "foo foo foo")
        .doesNotMatchAny("foo", "bad bar baz foo", "bar baz foo baz");

    dictionaryOf("? foo ?")
        .matchesAll("bar foo baz", "foo foo foo")
        .doesNotMatchAny("foo", "baz foo", "foo baz", "bar doo baz bar");

    dictionaryOf("? foo ? bar")
        .matchesAll("xxx foo xxx bar", "xxx foo yyy bar")
        .doesNotMatchAny("xxx foo xxx baz", "xxx foo xxx bar foo");

    // these two are different because *? is reluctant.
    dictionaryOf("? *? foo")
        .matchesAll("bar foo", "baz bar foo")
        .doesNotMatchAny("foo", "foo baz", "foo foo foo");

    dictionaryOf("? * foo")
        .matchesAll("bar foo", "baz bar foo", "foo foo foo")
        .doesNotMatchAny("foo", "foo baz");

    dictionaryOf("foo ? *").matchesAll("foo bar", "foo baz bar").doesNotMatchAny("foo", "baz foo");

    dictionaryOf("*? ? foo")
        .matchesAll("bar foo")
        // *? is reluctant, ? consumes baz, * consumes nothing, and "baz bar foo" doesn't match.
        .doesNotMatchAny("baz bar foo")
        .doesNotMatchAny("foo", "foo baz", "foo foo foo");

    dictionaryOf("* ? foo")
        // * is possessive and the last matching token after * is ? which matches 'foo',
        // which is consumed as part of possessive *; then ? doesn't match anything.
        .doesNotMatchAny("bar foo")
        // Similar case.
        .doesNotMatchAny("foo foo foo")
        // * is possessive, ? consumes baz, * consumes nothing, and "baz bar foo" doesn't match.
        .doesNotMatchAny("baz bar foo")
        .doesNotMatchAny("foo", "foo baz");
  }

  @Test
  public void leadingOneOrMorePossesive() {
    dictionaryOf("+ foo")
        .matchesAll("bar foo", "bar bar foo")
        .matchesAll("bar Foo", "bar bar FOO")
        // + is possessive, everything until the last 'foo' is consumed.
        .matchesAll("foo foo foo")
        .doesNotMatchAny("foo");

    dictionaryOf("+ \"FOO\"")
        .matchesAll("bar FOO", "bar bar FOO")
        .doesNotMatchAny("FOO", "foo", "bar foo", "bar bar Foo");
  }

  @Test
  public void leadingOneOrMoreReluctant() {
    dictionaryOf("+? foo")
        .matchesAll("bar foo", "bar bar foo")
        .matchesAll("bar Foo", "bar bar FOO")
        // + is reluctant, the first 'foo' is consumed, the remaining won't match.
        .doesNotMatchAny("foo foo foo")
        .doesNotMatchAny("foo");

    dictionaryOf("+? \"FOO\"")
        .matchesAll("bar FOO", "bar bar FOO")
        .doesNotMatchAny("FOO", "foo", "bar foo", "bar bar Foo");
  }

  @Test
  public void trailingOneOrMorePossessive() {
    dictionaryOf("foo +")
        .matchesAll("foo bar", "foo bar bar")
        .matchesAll("Foo bar ", "FOO bar bar")
        .doesNotMatchAny("foo", "Foo");

    dictionaryOf("\"FOO\" +")
        .matchesAll("FOO bar", "FOO bar bar")
        .doesNotMatchAny("FOO", "foo", "foo bar", "Foo bar bar");
  }

  @Test
  public void trailingOneOrMoreReluctant() {
    dictionaryOf("foo +?")
        .matchesAll("foo bar", "foo bar bar")
        .matchesAll("Foo bar ", "FOO bar bar")
        .doesNotMatchAny("foo", "Foo");

    dictionaryOf("\"FOO\" +?")
        .matchesAll("FOO bar", "FOO bar bar")
        .doesNotMatchAny("FOO", "foo", "foo bar", "Foo bar bar");
  }

  @Test
  public void insideOneOrMoreReluctant() {
    dictionaryOf("foo +? bar")
        .matchesAll("foo xyz bar", "FOO xyz Bar")
        .matchesAll("foo baz baz bar")
        .doesNotMatchAny("foo bar");

    // Reluctant matching.
    dictionaryOf("foo +? bar").doesNotMatchAny("foo bar bar bar");
    dictionaryOf("foo +? bar").matchesAll("foo bar bar");
    dictionaryOf("foo +? bar bar").matchesAll("foo baz bar bar", "foo baz xyz bar bar");

    dictionaryOf("foo +? bar +? var")
        .matchesAll("foo baz bar baz baz var", "foo x bar y var")
        .doesNotMatchAny("foo x bar y var zzz");
  }

  @Test
  public void insideOneOrMorePossessive() {
    dictionaryOf("foo + bar bar")
        .doesNotMatchAny("foo xyz bar bar")
        .doesNotMatchAny("foo baz baz bar")
        .doesNotMatchAny("foo bar");

    dictionaryOf("foo + bar")
        .matchesAll("foo xyz bar", "FOO xyz Bar")
        .matchesAll("foo baz baz bar")
        .doesNotMatchAny("foo bar");

    dictionaryOf("foo + bar")
        .matchesAll("foo x bar", "foo bar bar", "foo bar bar bar")
        .doesNotMatchAny("foo bar");
    dictionaryOf("foo + bar bar")
        .doesNotMatchAny("foo bar", "foo baz bar bar", "foo baz xyz bar bar");

    dictionaryOf("foo + bar + var")
        .matchesAll("foo x x bar y y var", "foo x bar y var")
        .doesNotMatchAny("foo x bar y var zzz");
  }

  @Test
  public void globInToken() {
    dictionaryOf("\"foo*\" bar").matchesAll("foo* bar", "foo* BAR");
  }

  @Test
  public void quoteInToken() {
    dictionaryOf("\\\"foo\\\" bar").matchesAll("\"foo\" bar", "\"FOO\" BAR");
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

  @Test
  public void typeMatching() {
    Map<String, Integer> typeMap =
        Map.of(
            "type1", 1,
            "type2", 1 << 1,
            "type3", 1 << 2);

    dictionaryOf(typeMap, "foo {type1}")
        .matchesAll("foo word:type1", "foo word:type1&type2")
        .doesNotMatchAny("foo", "foo word:type2");

    dictionaryOf(typeMap, "{type1} foo").matchesAll("word:type1 foo");
    dictionaryOf(typeMap, "{type1} foo {type2}").matchesAll("word:type1 foo word:type2");
    dictionaryOf(typeMap, "foo {type1} bar").matchesAll("foo word:type1&type2 bar");

    dictionaryOf(typeMap, "foo {type1&type2}")
        .matchesAll("foo word:type1&type2")
        .doesNotMatchAny("foo word:type1", "foo word:type2", "foo word");

    dictionaryOf(typeMap, "{type1}").matchesAll("word:type1").doesNotMatchAny("word:type2", "foo");
    dictionaryOf(typeMap, "{type1&type2}")
        .matchesAll("word:type1&type2")
        .doesNotMatchAny("word:type2", "foo");
  }

  @Test
  public void testStemmingUsedForNormalization() {
    Map<String, String> lemmas =
        Map.of(
            "cows", "cow",
            "mice", "mouse");

    var normalizer =
        GlobDictionary.defaultTokenNormalization().andThen(t -> lemmas.getOrDefault(t, t));
    dictionaryOf(normalizer, "blue cow").matchesAll("blue cow", "blue cows");
    dictionaryOf(normalizer, "blue cows").matchesAll("blue cow", "blue cows");
    dictionaryOf(normalizer, "blue mice").matchesAll("blue mice", "blue mouse");
    dictionaryOf(normalizer, "blue mouse").matchesAll("blue mice", "blue mouse");
  }

  @Test
  public void testPayloads() {
    var parser = new GlobDictionary.PatternParser();
    var dict =
        new GlobDictionary(
            Map.of("one", 1, "two", "two").entrySet().stream()
                .map(
                    e -> {
                      try {
                        return parser.parse(e.getKey(), e.getValue());
                      } catch (ParseException x) {
                        throw new RuntimeException(x);
                      }
                    }));

    GlobDictionaryAssert.assertThat(dict).payloads("one").containsExactly(1);
    GlobDictionaryAssert.assertThat(dict).payloads("two").containsExactly("two");
  }

  private GlobDictionaryAssert dictionaryOf(String... entries) {
    return GlobDictionaryAssert.assertThat(new GlobDictionary(parse(entries)));
  }

  private GlobDictionaryAssert dictionaryOf(Map<String, Integer> tokenTypes, String... entries) {
    return GlobDictionaryAssert.assertThat(new GlobDictionary(parse(tokenTypes, entries)))
        .withTypes(tokenTypes);
  }

  private GlobDictionaryAssert dictionaryOf(
      Function<String, String> normalization, String... entries) {
    return GlobDictionaryAssert.assertThat(
        new GlobDictionary(parse(entries), normalization, GlobDictionary.defaultTermSplitter()));
  }

  private static Stream<GlobDictionary.WordPattern> parse(String... patterns) {
    return parse(Collections.emptyMap(), patterns);
  }

  private static Stream<GlobDictionary.WordPattern> parse(
      Map<String, Integer> typeMap, String... patterns) {
    GlobDictionary.PatternParser parser = new GlobDictionary.PatternParser(typeMap);
    return Arrays.stream(patterns)
        .map(String::trim)
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
