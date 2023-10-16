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
package org.carrot2.util;

import java.util.Arrays;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.carrot2.TestBase;
import org.junit.Test;

/** Test cases for char array comparators. */
public class CharArrayComparatorsTest extends TestBase {
  @Test
  public void testNormalizingComparatorPL() {
    char[][] testWords =
        Stream.of(
                "\u0142an",
                "demo",
                "demos",
                "DEMO",
                "\u0141AN",
                "Demos",
                "demo",
                "\u0141an",
                "DEMOS")
            .map(v -> v.toCharArray())
            .toArray(char[][]::new);

    char[][] expectedOrderedWords =
        Stream.of(
                "\u0142an",
                "\u0141an",
                "\u0141AN",
                "demo",
                "demo",
                "DEMO",
                "demos",
                "Demos",
                "DEMOS")
            .map(v -> v.toCharArray())
            .toArray(char[][]::new);

    check(testWords, expectedOrderedWords);
  }

  @Test
  public void testNormalizingComparator() {
    char[][] testWords =
        Stream.of("use", "UAE", "Use").map(v -> v.toCharArray()).toArray(char[][]::new);

    char[][] expectedOrderedWords =
        Stream.of("UAE", "use", "Use").map(v -> v.toCharArray()).toArray(char[][]::new);

    check(testWords, expectedOrderedWords);
  }

  private void check(char[][] testWords, char[][] expectedOrderedWords) {
    Arrays.sort(testWords, CharArrayComparators.NORMALIZING_CHAR_ARRAY_COMPARATOR);
    Assertions.assertThat(testWords).isEqualTo(expectedOrderedWords);
  }
}
