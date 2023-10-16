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
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;

class GlobDictionaryAssert {
  private final GlobDictionary dictionary;
  private Map<String, Integer> tokenTypes;

  private GlobDictionaryAssert(GlobDictionary dictionary) {
    this.dictionary = dictionary;
  }

  public static GlobDictionaryAssert assertThat(GlobDictionary dictionary) {
    return new GlobDictionaryAssert(dictionary);
  }

  public GlobDictionaryAssert matchesAll(String... entries) {
    for (String entry : entries) {
      if (tokenTypes == null || tokenTypes.isEmpty()) {
        Assertions.assertThat(dictionary.test(entry))
            .as("Dictionary matches '" + entry + "'")
            .isTrue();
      } else {
        String[] raw = dictionary.split(entry);
        String[] tokens = Stream.of(raw).map(t -> t.replaceAll(":.+", "")).toArray(String[]::new);
        String[] normalized = dictionary.normalize(tokens);
        int[] types =
            Stream.of(raw)
                .mapToInt(
                    t -> {
                      int type = 0;
                      if (t.indexOf(":") > 0) {
                        for (String tok : t.substring(t.indexOf(":") + 1).split("&")) {
                          type |= tokenTypes.get(tok);
                        }
                      }
                      return type;
                    })
                .toArray();

        Assertions.assertThat(dictionary.find(tokens, normalized, types, p -> true))
            .as("Dictionary matches '" + entry + "'")
            .isTrue();
      }
    }
    return this;
  }

  public GlobDictionaryAssert doesNotMatchAny(String... entries) {
    for (String entry : entries) {
      Assertions.assertThat(dictionary.test(entry))
          .as("Dictionary does not match '" + entry + "'")
          .isFalse();
    }
    return this;
  }

  public GlobDictionary getDictionary() {
    return dictionary;
  }

  public GlobDictionaryAssert withTypes(Map<String, Integer> tokenTypes) {
    this.tokenTypes = tokenTypes;
    return this;
  }

  public ListAssert<Object> payloads(String input) {
    var tokens = dictionary.split(input);
    var normalized = dictionary.normalize(tokens);
    List<GlobDictionary.WordPattern> rules = new ArrayList<>();
    dictionary.find(
        tokens,
        normalized,
        null,
        (p) -> {
          rules.add(p);
          return false;
        });
    return Assertions.assertThat(rules.stream().map(GlobDictionary.WordPattern::getPayload));
  }
}
