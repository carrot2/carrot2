
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.util;

import org.assertj.core.api.Assertions;
import org.carrot2.AbstractTest;
import org.carrot2.util.CharArrayComparators;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Test cases for char array comparators.
 */
public class CharArrayComparatorsTest extends AbstractTest {
  @Test
  public void testNormalizingComparatorPL() {
    char[][] testWords = Stream.of(
        "\u0142an",
        "demo",
        "demos",
        "DEMO",
        "\u0141AN",
        "Demos",
        "demo",
        "\u0141an",
        "DEMOS")
        .map(v -> v.toCharArray()).toArray(char[][]::new);

    char[][] expectedOrderedWords = Stream.of(
        "\u0142an",
        "\u0141an",
        "\u0141AN",
        "demo",
        "demo",
        "DEMO",
        "demos",
        "Demos",
        "DEMOS")
        .map(v -> v.toCharArray()).toArray(char[][]::new);

    check(testWords, expectedOrderedWords);
  }

  @Test
  public void testNormalizingComparator() {
    char[][] testWords = Stream.of(
        "use",
        "UAE",
        "Use")
        .map(v -> v.toCharArray()).toArray(char[][]::new);

    char[][] expectedOrderedWords = Stream.of(
        "UAE",
        "use",
        "Use")
        .map(v -> v.toCharArray()).toArray(char[][]::new);

    check(testWords, expectedOrderedWords);
  }

  @Test
  public void testNullsAreEqual() {
    Assert.assertTrue(0 == CharArrayComparators.CASE_INSENSITIVE_CHAR_ARRAY_COMPARATOR.compare(null, null));
    Assert.assertTrue(0 == CharArrayComparators.FAST_CHAR_ARRAY_COMPARATOR.compare(null, null));
    Assert.assertTrue(0 == CharArrayComparators.NORMALIZING_CHAR_ARRAY_COMPARATOR.compare(null, null));
  }

  private void check(char[][] testWords, char[][] expectedOrderedWords) {
    Arrays.sort(testWords, CharArrayComparators.NORMALIZING_CHAR_ARRAY_COMPARATOR);
    Assertions.assertThat(testWords).isEqualTo(expectedOrderedWords);
  }
}
