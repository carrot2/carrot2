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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.carrot2.attrs.AttrComposite;
import org.carrot2.attrs.AttrStringArray;

/**
 * Default implementation of {@link StopwordFilterDictionary} and {@link LabelFilterDictionary}
 * interfaces. Provides support for exact string matching, globs (wildcard expressions) and regular
 * expression ({@link Pattern}) matching.
 */
public class DefaultDictionaryImpl extends AttrComposite
    implements StopwordFilterDictionary, LabelFilterDictionary {
  public AttrStringArray exact =
      attributes.register(
          "exact",
          AttrStringArray.builder()
              .label("Exact strings to filter out.")
              .defaultValue(new String[] {}));

  public AttrStringArray glob =
      attributes.register(
          "glob",
          AttrStringArray.builder()
              .label("Globbed expressions to filter out.")
              .defaultValue(new String[] {}));

  public AttrStringArray regexp =
      attributes.register(
          "regexp",
          AttrStringArray.builder()
              .label("Regular expression patterns.")
              .defaultValue(new String[] {}));

  @Override
  public StopwordFilter compileStopwordFilter() {
    Predicate<String> compiled = compile();
    return (t) -> !compiled.test(t.toString());
  }

  @Override
  public LabelFilter compileLabelFilter() {
    Predicate<String> compiled = compile();
    return (t) -> !compiled.test(t.toString());
  }

  private Predicate<String> compile() {
    ArrayList<Predicate<String>> predicates = new ArrayList<>();

    if (!exact.isEmpty()) {
      predicates.add(toSet(exact.get())::contains);
    }

    if (!glob.isEmpty()) {
      GlobDictionary glob = GlobDictionary.compilePatterns(toSet(this.glob.get()).stream());
      predicates.add(glob::test);
    }

    if (!regexp.isEmpty()) {
      Pattern compiled = union(compile(toSet(regexp.get())));
      predicates.add((label) -> compiled.matcher(label).find());
    }

    if (predicates.isEmpty()) {
      return (v) -> false;
    } else {
      var it = predicates.iterator();
      var p = it.next();
      while (it.hasNext()) {
        p = p.or(it.next());
      }
      return p;
    }
  }

  private Set<String> toSet(String[] strings) {
    return new LinkedHashSet<>(Arrays.asList(strings));
  }

  /** Convert a list of string into patterns. */
  private static List<Pattern> compile(Collection<String> patterns) {
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
    union.append("(");
    for (int i = 0; i < patterns.size(); i++) {
      if (i > 0) union.append(")|(");
      union.append(patterns.get(i).toString());
    }
    union.append(")");
    return Pattern.compile(union.toString());
  }
}
