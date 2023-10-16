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
package org.carrot2.text.preprocessing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.carrot2.TestBase;
import org.junit.Assert;
import org.junit.Test;

/** Test cases for {@link SubstringComparator}. */
public class SubstringComparatorTest extends TestBase {
  /**
   * @see "http://issues.carrot2.org/browse/CARROT-778"
   */
  @Test
  public void testCarrot778() {
    int[] tokensWordIndex = new int[1000];
    int[] wordsStemIndex = new int[2];

    for (int i = 0; i < tokensWordIndex.length; i++)
      tokensWordIndex[i] = randomIntBetween(0, wordsStemIndex.length - 1);

    for (int i = 0; i < wordsStemIndex.length; i++) wordsStemIndex[i] = i;

    final int substrLength = 3;
    final int maxFrom = tokensWordIndex.length - substrLength;
    List<Substring> substrings = new ArrayList<>();
    for (int i = 0; i < iterations(500, 2000); i++)
      substrings.add(new Substring(i, i % maxFrom, (i + substrLength) % maxFrom, 1));

    Collections.sort(substrings, new SubstringComparator(tokensWordIndex, wordsStemIndex));
  }

  @Test
  public void testComparatorContract() {
    int[] tokensWordIndex = new int[2];
    int[] wordsStemIndex = new int[1];
    Substring a = new Substring(0, 0, 1, 0);

    Assert.assertEquals(0, new SubstringComparator(tokensWordIndex, wordsStemIndex).compare(a, a));
  }
}
