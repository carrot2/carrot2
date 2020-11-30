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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import org.carrot2.attrs.AttrComposite;
import org.carrot2.attrs.AttrStringArray;
import org.carrot2.util.ResourceLookup;

public class RegExpLabelFilter extends AttrComposite implements LabelFilterAttr {
  public AttrStringArray patterns =
      attributes.register(
          "patterns",
          AttrStringArray.builder()
              .label("Regular expression patterns.")
              .defaultValue(new String[] {}));

  public RegExpLabelFilter() {}

  public RegExpLabelFilter(String... patterns) {
    this.patterns.set(patterns);
  }

  @Override
  public LabelFilter get() {
    List<Pattern> compiled =
        compile(
            new LinkedHashSet<>(
                Arrays.asList(
                    Objects.requireNonNullElseGet(this.patterns.get(), () -> new String[0]))));
    Pattern union = union(compiled);
    if (union == null) {
      return (word) -> false;
    } else {
      return (word) -> union.matcher(word).matches();
    }
  }

  static Pattern loadFromPlainText(ResourceLookup loader, String stoplabelsResource)
      throws IOException {
    List<Pattern> stoplabels;
    try (InputStream is = loader.open(stoplabelsResource);
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
      stoplabels = compile(readLines(reader));
    }
    return union(stoplabels);
  }

  /**
   * Loads words from a given resource (UTF-8, one word per line, #-starting lines are considered
   * comments).
   */
  public static HashSet<String> readLines(BufferedReader reader) throws IOException {
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

  /** Convert a list of string into patterns. */
  public static List<Pattern> compile(Collection<String> patterns) {
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
  public static Pattern union(List<Pattern> patterns) {
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
