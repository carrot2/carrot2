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

import com.carrotsearch.randomizedtesting.RandomizedTest;
import com.carrotsearch.randomizedtesting.annotations.Seed;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.BreakIterator;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.carrot2.TestBase;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

@Seed("deadbeef")
public class GlobDictionaryPerfTest extends TestBase {
  private static final int maxLines = 50_000;

  private static List<List<String>> lines;
  private static GlobDictionary dictionary;

  @BeforeClass
  public static void prepareData() throws IOException {
    String enwiki = System.getProperty("enwiki.path");

    Assume.assumeTrue("Skipping, no test data.", enwiki != null);

    String content;
    try (Stream<String> ss = Files.lines(Paths.get(enwiki), StandardCharsets.UTF_8)) {
      content = ss.limit(maxLines).collect(Collectors.joining(" . "));
    }

    lines = new ArrayList<>();
    final Locale locale = Locale.ROOT;
    final BreakIterator sentenceIterator = BreakIterator.getSentenceInstance(locale);
    final BreakIterator wordIterator = BreakIterator.getWordInstance(locale);
    HashMap<String, String> unique = new HashMap<>();
    for (String sentence : tokenize(content, sentenceIterator)) {
      List<String> lineTokens = new ArrayList<>();
      for (String token : tokenize(sentence, wordIterator)) {
        lineTokens.add(unique.computeIfAbsent(token, (t) -> t));
      }
      if (!lineTokens.isEmpty()) {
        lines.add(lineTokens);
      }
    }

    System.out.println("Max lines: " + maxLines);
    System.out.println("Lines: " + lines.size());

    // Create a random dictionary of filters.
    List<String> rules = new ArrayList<>();
    var tokenRules = addFrequentTokenRules(500);
    System.out.println(
        "Token rules: "
            + tokenRules.size()
            + " [hash: "
            + Long.toHexString(tokenRules.hashCode())
            + "]");
    rules.addAll(tokenRules);

    var longRules = addLongRules(10000);
    System.out.println(
        "Long rules: "
            + longRules.size()
            + " [hash: "
            + Long.toHexString(longRules.hashCode())
            + "]");
    rules.addAll(longRules);

    GlobDictionary.PatternParser parser = new GlobDictionary.PatternParser();
    List<GlobDictionary.WordPattern> patterns =
        rules.stream()
            .map(
                p -> {
                  try {
                    return parser.parse(p);
                  } catch (ParseException e) {
                    throw new RuntimeException(e);
                  }
                })
            .collect(Collectors.toList());

    dictionary = new GlobDictionary(patterns.stream());
  }

  private static Set<String> addFrequentTokenRules(int howMany) {
    Set<String> rules = new HashSet<>();

    var frequentTokens =
        lines.stream()
            .flatMap(Collection::stream)
            .collect(Collectors.groupingBy(token -> token, Collectors.counting()))
            .entrySet()
            .stream()
            .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
            .map(Map.Entry::getKey)
            .limit(1000)
            .collect(Collectors.toList());

    int weightExact = 20;
    int weightLeading = 10;
    int weightTrailing = 10;
    int weightMiddle = 10;
    int weightOneAnd = 10;
    int weightAndOne = 10;
    int total =
        weightExact + weightLeading + weightTrailing + weightMiddle + weightOneAnd + weightAndOne;

    for (int i = 0; i < howMany; i++) {
      int v = randomIntBetween(0, total);

      v -= weightExact;
      if (v < 0) {
        rules.add(RandomizedTest.randomFrom(frequentTokens));
        continue;
      }

      v -= weightLeading;
      if (v < 0) {
        rules.add(RandomizedTest.randomFrom(frequentTokens) + " *");
        continue;
      }

      v -= weightTrailing;
      if (v < 0) {
        rules.add("* " + RandomizedTest.randomFrom(frequentTokens));
        continue;
      }

      v -= weightMiddle;
      if (v < 0) {
        rules.add("* " + RandomizedTest.randomFrom(frequentTokens) + " *");
        continue;
      }

      v -= weightAndOne;
      if (v < 0) {
        rules.add(RandomizedTest.randomFrom(frequentTokens) + " ?");
        continue;
      }

      v -= weightOneAnd;
      if (v < 0) {
        rules.add("? " + RandomizedTest.randomFrom(frequentTokens));
        continue;
      }
    }

    return rules;
  }

  private static Set<String> addLongRules(int howMany) {
    GlobDictionary.PatternParser parser = new GlobDictionary.PatternParser();

    Set<String> rules = new HashSet<>();

    var allTokens =
        lines.stream()
            .flatMap(Collection::stream)
            .collect(Collectors.groupingBy(token -> token, Collectors.counting()))
            .entrySet()
            .stream()
            .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

    int top = allTokens.size() * 3 / 100;
    System.out.println("Top 3%: " + top);

    var tokens = allTokens.stream().skip(top).limit(5000).collect(Collectors.toList());
    var frequentTokens = allTokens.stream().limit(top).collect(Collectors.toList());

    int weightZeroOrMore = 5;
    int weightAny = 5;
    int weightOneOrMore = 5;
    int weightFrequentToken = 10;
    int weightNormalToken = 50;

    int total =
        weightZeroOrMore + weightAny + weightOneOrMore + weightFrequentToken + weightNormalToken;

    while (rules.size() < howMany) {
      List<String> rule = new ArrayList<>();
      for (int length = randomIntBetween(1, 5); --length >= 0; ) {
        int v = randomIntBetween(0, total);

        v -= weightZeroOrMore;
        if (v < 0) {
          rule.add("*");
          continue;
        }

        v -= weightAny;
        if (v < 0) {
          rule.add("?");
          continue;
        }

        v -= weightOneOrMore;
        if (v < 0) {
          rule.add("+");
          continue;
        }

        v -= weightFrequentToken;
        if (v < 0) {
          rule.add(RandomizedTest.randomFrom(frequentTokens));
          continue;
        }

        rule.add(RandomizedTest.randomFrom(tokens));
      }

      var ruleString = String.join(" ", rule);
      try {
        parser.parse(ruleString);
        rules.add(ruleString);
      } catch (ParseException e) {
        // Ignore, invalid rule.
      }
    }

    return rules;
  }

  @AfterClass
  public static void cleanup() {
    lines = null;
    dictionary = null;
  }

  /**
   * Returns tokens indicated by a given {@link BreakIterator} and starting with a letter or digit.
   */
  private static List<String> tokenize(final String content, final BreakIterator iterator) {
    final ArrayList<String> result = new ArrayList<>();

    iterator.setText(content);
    int start = iterator.first();
    int end = iterator.next();
    while (end != BreakIterator.DONE) {
      if (Character.isLetterOrDigit(content.charAt(start))) {
        result.add(content.substring(start, end).intern());
      }
      start = end;
      end = iterator.next();
    }

    return result;
  }

  @Test
  public void speedTest() {
    for (int round = 0; round < 5; round++) {
      long start = System.currentTimeMillis();
      long rejected = 0;
      long tests = 0;

      int maxTokens = 6;
      for (var line : lines) {
        String[] tokens = line.toArray(String[]::new);
        String[] normalized = dictionary.normalize(tokens);

        if (line.size() < maxTokens) {
          tests++;

          if (dictionary.find(tokens, normalized, null, e -> true)) {
            rejected++;
          }
        } else {
          for (int i = 0, max = line.size() - maxTokens; i < max; i++) {
            tests++;
            String[] tokenSublist = Arrays.copyOfRange(tokens, i, i + maxTokens);
            String[] normalizedSublist = Arrays.copyOfRange(normalized, i, i + maxTokens);
            if (dictionary.find(tokenSublist, normalizedSublist, null, e -> true)) {
              rejected++;
            }
          }
        }
      }
      long end = System.currentTimeMillis();

      System.out.printf(
          Locale.ROOT,
          "Round: %2d, Tests: %2d, Time: %.3f, Rejected: %s%n",
          round,
          tests,
          (end - start) / 1000d,
          rejected);
    }
  }
}
