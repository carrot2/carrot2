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

import java.util.Comparator;

/** A set of specific comparators for <code>char []</code> arrays. */
public class CharArrayComparators {
  /** A case-insensitive lexicographic comparator. */
  public static final Comparator<char[]> CASE_INSENSITIVE_CHAR_ARRAY_COMPARATOR =
      (a1, a2) -> {
        if (a1 == null) {
          return a2 == null ? 0 : 1;
        } else if (a2 == null) {
          return -1;
        }

        final int l1 = a1.length;
        final int l2 = a2.length;
        final int n = l1 < l2 ? l1 : l2;

        for (int i = 0, max = n; i < max; ) {
          int chr1 = Character.codePointAt(a1, i);
          int chr2 = Character.codePointAt(a2, i);
          int cp = Integer.compare(Character.toLowerCase(chr1), Character.toLowerCase(chr2));
          if (cp != 0) {
            return cp;
          }

          i += Character.charCount(chr1);
        }

        return l1 - l2;
      };

  /** A case-sensitive lexicographic comparator. */
  public static final Comparator<char[]> FAST_CHAR_ARRAY_COMPARATOR =
      (a1, a2) -> {
        if (a1 == null) {
          return a2 == null ? 0 : 1;
        } else if (a2 == null) {
          return -1;
        }

        final int l1 = a1.length;
        final int l2 = a2.length;
        final int n = l1 < l2 ? l1 : l2;

        // Quiet assumption that the numbers here won't cause an overflow.
        for (int i = 0; i < n; i++) {
          final char a1I = a1[i];
          final char a2I = a2[i];

          if (a1I != a2I) {
            return a1I - a2I;
          }
        }

        return l1 - l2;
      };

  /**
   * A comparator that groups different strings into different buckets (case-insensitive) and
   * strings within these buckets (case-sensitive). The comparator that applies the following rules
   * in the following order:
   *
   * <ol>
   *   <li>A <code>null</code> string is greater than a non-<code>null</code> one
   *   <li>A longer string is greater
   *   <li>Strings are first compared in case-insensitive mode
   *   <li>Finally, strings are compared in case-sensitive mode
   * </ol>
   *
   * This comparator does not provide a lexicographic order, which makes it much faster, but not
   * suitable for general purpose sorting.
   */
  public static final Comparator<char[]> NORMALIZING_CHAR_ARRAY_COMPARATOR =
      (a1, a2) -> {
        if (a1 == null) {
          return a2 == null ? 0 : 1;
        } else if (a2 == null) {
          return -1;
        }

        final int l1 = a1.length;
        final int l2 = a2.length;

        // Not crucial, but speeds things up
        if (l1 != l2) {
          return l1 - l2;
        }

        /*
         * The condition below is perfectly ok here. It is
         * used to calculate word occurrence statistics, which is essentially a "count
         * unique strings by sorting" problem. Therefore, the semantic meaning of the
         * order produced by this comparator doesn't matter at all as long as it: a)
         * groups equal (case sensitive) strings together, b) groups equal (case
         * insensitive) strings into one block, c) null string is always greater than
         * a non-null string. See tests for this comparator for examples.
         *
         * In comparison-based sorting algorithms crucial is the speed of comparisons,
         * so declaring that e.g. shorter strings are always smaller (regardless of
         * contents) saves us calls to Character.toLowerCase(), which are very costly.
         * For CaseNormalizer it doesn't matter at all, and makes sorting way faster.
         */

        // Compare whole strings in case insensitive mode first
        for (int i = 0, max = l1; i < max; ) {
          int chr1 = Character.codePointAt(a1, i);
          int chr2 = Character.codePointAt(a2, i);
          int cp = Integer.compare(Character.toLowerCase(chr1), Character.toLowerCase(chr2));
          if (cp != 0) {
            return cp;
          }

          i += Character.charCount(chr1);
        }

        // Only if strings are case-insensitive equal, go case sensitive
        for (int i = 0; i < l1; i++) {
          char a1I = a1[i];
          char a2I = a2[i];

          if (a1I != a2I) {
            // Put lower case first
            return a2I - a1I;
          }
        }

        return 0;
      };

  /** No instantiation. */
  private CharArrayComparators() {}
}
