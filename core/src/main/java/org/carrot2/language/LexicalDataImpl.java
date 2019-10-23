/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.language;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import org.carrot2.util.ResourceLookup;

/**
 * {@link LexicalData} implemented on top of a hash set (stopwords) and a regular expression pattern
 * (stoplabels).
 */
public final class LexicalDataImpl implements LexicalData {
  private final HashSet<String> stopwords;
  private final Pattern stoplabelPattern;

  public LexicalDataImpl(HashSet<String> stopwords, Pattern stoplabelPattern) {
    this.stopwords = stopwords;
    this.stoplabelPattern = stoplabelPattern;
  }

  public LexicalDataImpl(ResourceLookup loader, String stopwordsResource, String stoplabelsResource)
      throws IOException {
    this(loadStopwords(loader, stopwordsResource), loadStoplabels(loader, stoplabelsResource));
  }

  /*
   *
   */
  @Override
  public boolean ignoreWord(CharSequence word) {
    return stopwords.contains(word.toString());
  }

  /*
   *
   */
  @Override
  public boolean ignoreLabel(CharSequence label) {
    if (this.stoplabelPattern == null) return false;

    return stoplabelPattern.matcher(label).matches();
  }

  private static Pattern loadStoplabels(ResourceLookup loader, String stoplabelsResource)
      throws IOException {
    List<Pattern> stoplabels;
    try (InputStream is = loader.open(stoplabelsResource);
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
      stoplabels = compile(readLines(reader));
    }
    return union(stoplabels);
  }

  private static HashSet<String> loadStopwords(ResourceLookup loader, String stopwordsResource)
      throws IOException {
    HashSet<String> stopwords = new HashSet<>();
    try (InputStream is = loader.open(stopwordsResource);
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
      readLines(reader).forEach(word -> stopwords.add(word.toLowerCase(Locale.ROOT)));
    }
    return stopwords;
  }

  /**
   * Loads words from a given resource (UTF-8, one word per line, #-starting lines are considered
   * comments).
   */
  private static HashSet<String> readLines(BufferedReader reader) throws IOException {
    final HashSet<String> words = new HashSet<>();
    String line;
    while ((line = reader.readLine()) != null) {
      line = line.trim();
      if (!line.startsWith("#") && !line.isEmpty()) {
        words.add(line);
      }
    }
    return words;
  }

  private static List<Pattern> compile(Set<String> patterns) {
    ArrayList<Pattern> compiled = new ArrayList<>();
    for (String p : patterns) {
      compiled.add(Pattern.compile(p));
    }
    return compiled;
  }

  /**
   * Combines a number of patterns into a single pattern with a union of all of them. With
   * automata-based pattern engines, this should be faster and memory-friendly.
   */
  private static Pattern union(List<Pattern> patterns) {
    final StringBuilder union = new StringBuilder();
    if (patterns.size() > 0) {
      union.append("(");
      for (int i = 0; i < patterns.size(); i++) {
        if (i > 0) union.append(")|(");
        union.append(patterns.get(i).toString());
      }
      union.append(")");
      return Pattern.compile(union.toString());
    } else {
      return null;
    }
  }
}
