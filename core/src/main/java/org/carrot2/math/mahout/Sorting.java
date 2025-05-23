/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2025, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.math.mahout;

import java.util.Arrays;
import java.util.Comparator;
import org.carrot2.math.mahout.function.ByteComparator;
import org.carrot2.math.mahout.function.CharComparator;
import org.carrot2.math.mahout.function.DoubleComparator;
import org.carrot2.math.mahout.function.FloatComparator;
import org.carrot2.math.mahout.function.IntComparator;
import org.carrot2.math.mahout.function.LongComparator;
import org.carrot2.math.mahout.function.ShortComparator;

public final class Sorting {

  /* Specifies when to switch to insertion sort */
  private static final int SIMPLE_LENGTH = 7;
  static final int SMALL = 7;

  private Sorting() {
    /* empty */
  }

  public static int binarySearchFromTo(byte[] array, byte value, int from, int to) {
    int mid = -1;
    while (from <= to) {
      mid = (from + to) >>> 1;
      if (value > array[mid]) {
        from = mid + 1;
      } else if (value == array[mid]) {
        return mid;
      } else {
        to = mid - 1;
      }
    }
    if (mid < 0) {
      return -1;
    }

    return -mid - (value < array[mid] ? 1 : 2);
  }

  public static int binarySearchFromTo(char[] array, char value, int from, int to) {
    int mid = -1;
    while (from <= to) {
      mid = (from + to) >>> 1;
      if (value > array[mid]) {
        from = mid + 1;
      } else if (value == array[mid]) {
        return mid;
      } else {
        to = mid - 1;
      }
    }
    if (mid < 0) {
      return -1;
    }
    return -mid - (value < array[mid] ? 1 : 2);
  }

  public static int binarySearchFromTo(double[] array, double value, int from, int to) {
    long longBits = Double.doubleToLongBits(value);
    int mid = -1;
    while (from <= to) {
      mid = (from + to) >>> 1;
      if (lessThan(array[mid], value)) {
        from = mid + 1;
      } else if (longBits == Double.doubleToLongBits(array[mid])) {
        return mid;
      } else {
        to = mid - 1;
      }
    }
    if (mid < 0) {
      return -1;
    }
    return -mid - (lessThan(value, array[mid]) ? 1 : 2);
  }

  public static int binarySearchFromTo(float[] array, float value, int from, int to) {
    int intBits = Float.floatToIntBits(value);
    int mid = -1;
    while (from <= to) {
      mid = (from + to) >>> 1;
      if (lessThan(array[mid], value)) {
        from = mid + 1;
      } else if (intBits == Float.floatToIntBits(array[mid])) {
        return mid;
      } else {
        to = mid - 1;
      }
    }
    if (mid < 0) {
      return -1;
    }
    return -mid - (lessThan(value, array[mid]) ? 1 : 2);
  }

  public static int binarySearchFromTo(int[] array, int value, int from, int to) {
    int mid = -1;
    while (from <= to) {
      mid = (from + to) >>> 1;
      if (value > array[mid]) {
        from = mid + 1;
      } else if (value == array[mid]) {
        return mid;
      } else {
        to = mid - 1;
      }
    }
    if (mid < 0) {
      return -1;
    }
    return -mid - (value < array[mid] ? 1 : 2);
  }

  public static int binarySearchFromTo(long[] array, long value, int from, int to) {
    int mid = -1;
    while (from <= to) {
      mid = (from + to) >>> 1;
      if (value > array[mid]) {
        from = mid + 1;
      } else if (value == array[mid]) {
        return mid;
      } else {
        to = mid - 1;
      }
    }
    if (mid < 0) {
      return -1;
    }
    return -mid - (value < array[mid] ? 1 : 2);
  }

  public static <T extends Comparable<T>> int binarySearchFromTo(
      T[] array, T object, int from, int to) {
    if (array.length == 0) {
      return -1;
    }

    int mid = 0, result = 0;
    while (from <= to) {
      mid = (from + to) >>> 1;
      if ((result = array[mid].compareTo(object)) < 0) {
        from = mid + 1;
      } else if (result == 0) {
        return mid;
      } else {
        to = mid - 1;
      }
    }
    return -mid - (result >= 0 ? 1 : 2);
  }

  public static <T> int binarySearchFromTo(
      T[] array, T object, int from, int to, Comparator<? super T> comparator) {
    int mid = 0, result = 0;
    while (from <= to) {
      mid = (from + to) >>> 1;
      if ((result = comparator.compare(array[mid], object)) < 0) {
        from = mid + 1;
      } else if (result == 0) {
        return mid;
      } else {
        to = mid - 1;
      }
    }
    return -mid - (result >= 0 ? 1 : 2);
  }

  public static int binarySearchFromTo(short[] array, short value, int from, int to) {
    int mid = -1;
    while (from <= to) {
      mid = (from + to) >>> 1;
      if (value > array[mid]) {
        from = mid + 1;
      } else if (value == array[mid]) {
        return mid;
      } else {
        to = mid - 1;
      }
    }
    if (mid < 0) {
      return -1;
    }
    return -mid - (value < array[mid] ? 1 : 2);
  }

  private static boolean lessThan(double double1, double double2) {
    // A slightly specialized version of
    // Double.compare(double1, double2) < 0.

    // Non-zero and non-NaN checking.
    if (double1 < double2) {
      return true;
    }
    if (double1 > double2) {
      return false;
    }
    if (double1 == double2 && double1 != 0.0) {
      return false;
    }

    // NaNs are equal to other NaNs and larger than any other double.
    if (Double.isNaN(double1)) {
      return false;
    } else if (Double.isNaN(double2)) {
      return true;
    }

    // Deal with +0.0 and -0.0.
    long d1 = Double.doubleToRawLongBits(double1);
    long d2 = Double.doubleToRawLongBits(double2);
    return d1 < d2;
  }

  private static boolean lessThan(float float1, float float2) {
    // A slightly specialized version of Float.compare(float1, float2) < 0.

    // Non-zero and non-NaN checking.
    if (float1 < float2) {
      return true;
    }
    if (float1 > float2) {
      return false;
    }
    if (float1 == float2 && float1 != 0.0f) {
      return false;
    }

    // NaNs are equal to other NaNs and larger than any other float
    if (Float.isNaN(float1)) {
      return false;
    } else if (Float.isNaN(float2)) {
      return true;
    }

    // Deal with +0.0 and -0.0
    int f1 = Float.floatToRawIntBits(float1);
    int f2 = Float.floatToRawIntBits(float2);
    return f1 < f2;
  }

  private static <T> int med3(T[] array, int a, int b, int c, Comparator<T> comp) {
    T x = array[a], y = array[b], z = array[c];
    int comparisonxy = comp.compare(x, y);
    int comparisonxz = comp.compare(x, z);
    int comparisonyz = comp.compare(y, z);
    return comparisonxy < 0
        ? (comparisonyz < 0 ? b : (comparisonxz < 0 ? c : a))
        : (comparisonyz > 0 ? b : (comparisonxz > 0 ? c : a));
  }

  private static int med3(byte[] array, int a, int b, int c, ByteComparator comp) {
    byte x = array[a], y = array[b], z = array[c];
    int comparisonxy = comp.compare(x, y);
    int comparisonxz = comp.compare(x, z);
    int comparisonyz = comp.compare(y, z);
    return comparisonxy < 0
        ? (comparisonyz < 0 ? b : (comparisonxz < 0 ? c : a))
        : (comparisonyz > 0 ? b : (comparisonxz > 0 ? c : a));
  }

  private static int med3(char[] array, int a, int b, int c, CharComparator comp) {
    char x = array[a], y = array[b], z = array[c];
    int comparisonxy = comp.compare(x, y);
    int comparisonxz = comp.compare(x, z);
    int comparisonyz = comp.compare(y, z);
    return comparisonxy < 0
        ? (comparisonyz < 0 ? b : (comparisonxz < 0 ? c : a))
        : (comparisonyz > 0 ? b : (comparisonxz > 0 ? c : a));
  }

  private static int med3(double[] array, int a, int b, int c, DoubleComparator comp) {
    double x = array[a], y = array[b], z = array[c];
    int comparisonxy = comp.compare(x, y);
    int comparisonxz = comp.compare(x, z);
    int comparisonyz = comp.compare(y, z);
    return comparisonxy < 0
        ? (comparisonyz < 0 ? b : (comparisonxz < 0 ? c : a))
        : (comparisonyz > 0 ? b : (comparisonxz > 0 ? c : a));
  }

  private static int med3(float[] array, int a, int b, int c, FloatComparator comp) {
    float x = array[a], y = array[b], z = array[c];
    int comparisonxy = comp.compare(x, y);
    int comparisonxz = comp.compare(x, z);
    int comparisonyz = comp.compare(y, z);
    return comparisonxy < 0
        ? (comparisonyz < 0 ? b : (comparisonxz < 0 ? c : a))
        : (comparisonyz > 0 ? b : (comparisonxz > 0 ? c : a));
  }

  private static int med3(int[] array, int a, int b, int c, IntComparator comp) {
    int x = array[a], y = array[b], z = array[c];
    int comparisonxy = comp.compare(x, y);
    int comparisonxz = comp.compare(x, z);
    int comparisonyz = comp.compare(y, z);
    return comparisonxy < 0
        ? (comparisonyz < 0 ? b : (comparisonxz < 0 ? c : a))
        : (comparisonyz > 0 ? b : (comparisonxz > 0 ? c : a));
  }

  private static int med3(int a, int b, int c, IntComparator comp) {
    int comparisonab = comp.compare(a, b);
    int comparisonac = comp.compare(a, c);
    int comparisonbc = comp.compare(b, c);
    return comparisonab < 0
        ? (comparisonbc < 0 ? b : (comparisonac < 0 ? c : a))
        : (comparisonbc > 0 ? b : (comparisonac > 0 ? c : a));
  }

  private static int med3(long[] array, int a, int b, int c, LongComparator comp) {
    long x = array[a], y = array[b], z = array[c];
    int comparisonxy = comp.compare(x, y);
    int comparisonxz = comp.compare(x, z);
    int comparisonyz = comp.compare(y, z);
    return comparisonxy < 0
        ? (comparisonyz < 0 ? b : (comparisonxz < 0 ? c : a))
        : (comparisonyz > 0 ? b : (comparisonxz > 0 ? c : a));
  }

  private static int med3(short[] array, int a, int b, int c, ShortComparator comp) {
    short x = array[a], y = array[b], z = array[c];
    int comparisonxy = comp.compare(x, y);
    int comparisonxz = comp.compare(x, z);
    int comparisonyz = comp.compare(y, z);
    return comparisonxy < 0
        ? (comparisonyz < 0 ? b : (comparisonxz < 0 ? c : a))
        : (comparisonyz > 0 ? b : (comparisonxz > 0 ? c : a));
  }

  public static void quickSort(byte[] array, int start, int end, ByteComparator comp) {
    if (array == null) {
      throw new NullPointerException();
    }
    checkBounds(array.length, start, end);
    quickSort0(start, end, array, comp);
  }

  private static void checkBounds(int arrLength, int start, int end) {
    if (start > end) {
      // K0033=Start index ({0}) is greater than end index ({1})
      throw new IllegalArgumentException(
          "Start index " + start + " is greater than end index " + end);
    }
    if (start < 0) {
      throw new ArrayIndexOutOfBoundsException("Array index out of range " + start);
    }
    if (end > arrLength) {
      throw new ArrayIndexOutOfBoundsException("Array index out of range " + end);
    }
  }

  private static void quickSort0(int start, int end, byte[] array, ByteComparator comp) {
    byte temp;
    int length = end - start;
    if (length < 7) {
      for (int i = start + 1; i < end; i++) {
        for (int j = i; j > start && comp.compare(array[j - 1], array[j]) > 0; j--) {
          temp = array[j];
          array[j] = array[j - 1];
          array[j - 1] = temp;
        }
      }
      return;
    }
    int middle = (start + end) / 2;
    if (length > 7) {
      int bottom = start;
      int top = end - 1;
      if (length > 40) {
        length /= 8;
        bottom = med3(array, bottom, bottom + length, bottom + (2 * length), comp);
        middle = med3(array, middle - length, middle, middle + length, comp);
        top = med3(array, top - (2 * length), top - length, top, comp);
      }
      middle = med3(array, bottom, middle, top, comp);
    }
    byte partionValue = array[middle];
    int a, b, c, d;
    a = b = start;
    c = d = end - 1;
    while (true) {
      int comparison;
      while (b <= c && (comparison = comp.compare(array[b], partionValue)) <= 0) {
        if (comparison == 0) {
          temp = array[a];
          array[a++] = array[b];
          array[b] = temp;
        }
        b++;
      }
      while (c >= b && (comparison = comp.compare(array[c], partionValue)) >= 0) {
        if (comparison == 0) {
          temp = array[c];
          array[c] = array[d];
          array[d--] = temp;
        }
        c--;
      }
      if (b > c) {
        break;
      }
      temp = array[b];
      array[b++] = array[c];
      array[c--] = temp;
    }
    length = a - start < b - a ? a - start : b - a;
    int l = start;
    int h = b - length;
    while (length-- > 0) {
      temp = array[l];
      array[l++] = array[h];
      array[h++] = temp;
    }
    length = d - c < end - 1 - d ? d - c : end - 1 - d;
    l = b;
    h = end - length;
    while (length-- > 0) {
      temp = array[l];
      array[l++] = array[h];
      array[h++] = temp;
    }
    if ((length = b - a) > 0) {
      quickSort0(start, start + length, array, comp);
    }
    if ((length = d - c) > 0) {
      quickSort0(end - length, end, array, comp);
    }
  }

  public static void quickSort(int start, int end, IntComparator comp, Swapper swap) {
    checkBounds(end + 1, start, end);
    quickSort0(start, end, comp, swap);
  }

  private static void quickSort0(int start, int end, IntComparator comp, Swapper swap) {
    int length = end - start;
    if (length < 7) {
      insertionSort(start, end, comp, swap);
      return;
    }
    int middle = (start + end) / 2;
    if (length > 7) {
      int bottom = start;
      int top = end - 1;
      if (length > 40) {
        // for lots of data, bottom, middle and top are medians near the beginning, middle or end of
        // the data
        int skosh = length / 8;
        bottom = med3(bottom, bottom + skosh, bottom + (2 * skosh), comp);
        middle = med3(middle - skosh, middle, middle + skosh, comp);
        top = med3(top - (2 * skosh), top - skosh, top, comp);
      }
      middle = med3(bottom, middle, top, comp);
    }

    int partitionIndex = middle; // an index, not a value.

    // regions from a to b and from c to d are what we will recursively sort
    int a, b, c, d;
    a = b = start;
    c = d = end - 1;
    while (b <= c) {
      // copy all values equal to the partition value to before a..b.  In the process, advance b
      // as long as values less than the partition or equal are found, also stop when a..b collides
      // with c..d
      int comparison;
      while (b <= c && (comparison = comp.compare(b, partitionIndex)) <= 0) {
        if (comparison == 0) {
          if (a == partitionIndex) {
            partitionIndex = b;
          } else if (b == partitionIndex) {
            partitionIndex = a;
          }
          swap.swap(a, b);
          a++;
        }
        b++;
      }
      // at this point [start..a) has partition values, [a..b) has values < partition
      // also, either b>c or v[b] > partition value

      while (c >= b && (comparison = comp.compare(c, partitionIndex)) >= 0) {
        if (comparison == 0) {
          if (c == partitionIndex) {
            partitionIndex = d;
          } else if (d == partitionIndex) {
            partitionIndex = c;
          }
          swap.swap(c, d);

          d--;
        }
        c--;
      }
      // now we also know that [d..end] contains partition values,
      // [c..d) contains values > partition value
      // also, either b>c or (v[b] > partition OR v[c] < partition)

      if (b <= c) {
        // v[b] > partition OR v[c] < partition
        // swapping will let us continue to grow the two regions
        if (c == partitionIndex) {
          partitionIndex = b;
        } else if (b == partitionIndex) {
          partitionIndex = d;
        }
        swap.swap(b, c);
        b++;
        c--;
      }
    }
    // now we know
    // b = c+1
    // [start..a) and [d..end) contain partition value
    // all of [a..b) are less than partition
    // all of [c..d) are greater than partition

    // shift [a..b) to beginning
    length = Math.min(a - start, b - a);
    int l = start;
    int h = b - length;
    while (length-- > 0) {
      swap.swap(l, h);
      l++;
      h++;
    }

    // shift [c..d) to end
    length = Math.min(d - c, end - 1 - d);
    l = b;
    h = end - length;
    while (length-- > 0) {
      swap.swap(l, h);
      l++;
      h++;
    }

    // recurse left and right
    length = b - a;
    if (length > 0) {
      quickSort0(start, start + length, comp, swap);
    }

    length = d - c;
    if (length > 0) {
      quickSort0(end - length, end, comp, swap);
    }
  }

  private static void insertionSort(int start, int end, IntComparator comp, Swapper swap) {
    for (int i = start + 1; i < end; i++) {
      for (int j = i; j > start && comp.compare(j - 1, j) > 0; j--) {
        swap.swap(j - 1, j);
      }
    }
  }

  public static void quickSort(char[] array, int start, int end, CharComparator comp) {
    if (array == null) {
      throw new NullPointerException();
    }
    checkBounds(array.length, start, end);
    quickSort0(start, end, array, comp);
  }

  private static void quickSort0(int start, int end, char[] array, CharComparator comp) {
    char temp;
    int length = end - start;
    if (length < 7) {
      for (int i = start + 1; i < end; i++) {
        for (int j = i; j > start && comp.compare(array[j - 1], array[j]) > 0; j--) {
          temp = array[j];
          array[j] = array[j - 1];
          array[j - 1] = temp;
        }
      }
      return;
    }
    int middle = (start + end) / 2;
    if (length > 7) {
      int bottom = start;
      int top = end - 1;
      if (length > 40) {
        length /= 8;
        bottom = med3(array, bottom, bottom + length, bottom + (2 * length), comp);
        middle = med3(array, middle - length, middle, middle + length, comp);
        top = med3(array, top - (2 * length), top - length, top, comp);
      }
      middle = med3(array, bottom, middle, top, comp);
    }
    char partionValue = array[middle];
    int a, b, c, d;
    a = b = start;
    c = d = end - 1;
    while (true) {
      int comparison;
      while (b <= c && (comparison = comp.compare(array[b], partionValue)) <= 0) {
        if (comparison == 0) {
          temp = array[a];
          array[a++] = array[b];
          array[b] = temp;
        }
        b++;
      }
      while (c >= b && (comparison = comp.compare(array[c], partionValue)) >= 0) {
        if (comparison == 0) {
          temp = array[c];
          array[c] = array[d];
          array[d--] = temp;
        }
        c--;
      }
      if (b > c) {
        break;
      }
      temp = array[b];
      array[b++] = array[c];
      array[c--] = temp;
    }
    length = a - start < b - a ? a - start : b - a;
    int l = start;
    int h = b - length;
    while (length-- > 0) {
      temp = array[l];
      array[l++] = array[h];
      array[h++] = temp;
    }
    length = d - c < end - 1 - d ? d - c : end - 1 - d;
    l = b;
    h = end - length;
    while (length-- > 0) {
      temp = array[l];
      array[l++] = array[h];
      array[h++] = temp;
    }
    if ((length = b - a) > 0) {
      quickSort0(start, start + length, array, comp);
    }
    if ((length = d - c) > 0) {
      quickSort0(end - length, end, array, comp);
    }
  }

  public static void quickSort(double[] array, int start, int end, DoubleComparator comp) {
    if (array == null) {
      throw new NullPointerException();
    }
    checkBounds(array.length, start, end);
    quickSort0(start, end, array, comp);
  }

  private static void quickSort0(int start, int end, double[] array, DoubleComparator comp) {
    double temp;
    int length = end - start;
    if (length < 7) {
      for (int i = start + 1; i < end; i++) {
        for (int j = i; j > start && comp.compare(array[j], array[j - 1]) < 0; j--) {
          temp = array[j];
          array[j] = array[j - 1];
          array[j - 1] = temp;
        }
      }
      return;
    }
    int middle = (start + end) / 2;
    if (length > 7) {
      int bottom = start;
      int top = end - 1;
      if (length > 40) {
        length /= 8;
        bottom = med3(array, bottom, bottom + length, bottom + (2 * length), comp);
        middle = med3(array, middle - length, middle, middle + length, comp);
        top = med3(array, top - (2 * length), top - length, top, comp);
      }
      middle = med3(array, bottom, middle, top, comp);
    }
    double partionValue = array[middle];
    int a, b, c, d;
    a = b = start;
    c = d = end - 1;
    while (true) {
      int comparison;
      while (b <= c && (comparison = comp.compare(partionValue, array[b])) >= 0) {
        if (comparison == 0) {
          temp = array[a];
          array[a++] = array[b];
          array[b] = temp;
        }
        b++;
      }
      while (c >= b && (comparison = comp.compare(array[c], partionValue)) >= 0) {
        if (comparison == 0) {
          temp = array[c];
          array[c] = array[d];
          array[d--] = temp;
        }
        c--;
      }
      if (b > c) {
        break;
      }
      temp = array[b];
      array[b++] = array[c];
      array[c--] = temp;
    }
    length = a - start < b - a ? a - start : b - a;
    int l = start;
    int h = b - length;
    while (length-- > 0) {
      temp = array[l];
      array[l++] = array[h];
      array[h++] = temp;
    }
    length = d - c < end - 1 - d ? d - c : end - 1 - d;
    l = b;
    h = end - length;
    while (length-- > 0) {
      temp = array[l];
      array[l++] = array[h];
      array[h++] = temp;
    }
    if ((length = b - a) > 0) {
      quickSort0(start, start + length, array, comp);
    }
    if ((length = d - c) > 0) {
      quickSort0(end - length, end, array, comp);
    }
  }

  public static void quickSort(float[] array, int start, int end, FloatComparator comp) {
    if (array == null) {
      throw new NullPointerException();
    }
    checkBounds(array.length, start, end);
    quickSort0(start, end, array, comp);
  }

  private static void quickSort0(int start, int end, float[] array, FloatComparator comp) {
    float temp;
    int length = end - start;
    if (length < 7) {
      for (int i = start + 1; i < end; i++) {
        for (int j = i; j > start && comp.compare(array[j], array[j - 1]) < 0; j--) {
          temp = array[j];
          array[j] = array[j - 1];
          array[j - 1] = temp;
        }
      }
      return;
    }
    int middle = (start + end) / 2;
    if (length > 7) {
      int bottom = start;
      int top = end - 1;
      if (length > 40) {
        length /= 8;
        bottom = med3(array, bottom, bottom + length, bottom + (2 * length), comp);
        middle = med3(array, middle - length, middle, middle + length, comp);
        top = med3(array, top - (2 * length), top - length, top, comp);
      }
      middle = med3(array, bottom, middle, top, comp);
    }
    float partionValue = array[middle];
    int a, b, c, d;
    a = b = start;
    c = d = end - 1;
    while (true) {
      int comparison;
      while (b <= c && (comparison = comp.compare(partionValue, array[b])) >= 0) {
        if (comparison == 0) {
          temp = array[a];
          array[a++] = array[b];
          array[b] = temp;
        }
        b++;
      }
      while (c >= b && (comparison = comp.compare(array[c], partionValue)) >= 0) {
        if (comparison == 0) {
          temp = array[c];
          array[c] = array[d];
          array[d--] = temp;
        }
        c--;
      }
      if (b > c) {
        break;
      }
      temp = array[b];
      array[b++] = array[c];
      array[c--] = temp;
    }
    length = a - start < b - a ? a - start : b - a;
    int l = start;
    int h = b - length;
    while (length-- > 0) {
      temp = array[l];
      array[l++] = array[h];
      array[h++] = temp;
    }
    length = d - c < end - 1 - d ? d - c : end - 1 - d;
    l = b;
    h = end - length;
    while (length-- > 0) {
      temp = array[l];
      array[l++] = array[h];
      array[h++] = temp;
    }
    if ((length = b - a) > 0) {
      quickSort0(start, start + length, array, comp);
    }
    if ((length = d - c) > 0) {
      quickSort0(end - length, end, array, comp);
    }
  }

  public static void quickSort(int[] array, int start, int end, IntComparator comp) {
    if (array == null) {
      throw new NullPointerException();
    }
    checkBounds(array.length, start, end);
    quickSort0(start, end, array, comp);
  }

  private static void quickSort0(int start, int end, int[] array, IntComparator comp) {
    int temp;
    int length = end - start;
    if (length < 7) {
      for (int i = start + 1; i < end; i++) {
        for (int j = i; j > start && comp.compare(array[j - 1], array[j]) > 0; j--) {
          temp = array[j];
          array[j] = array[j - 1];
          array[j - 1] = temp;
        }
      }
      return;
    }
    int middle = (start + end) / 2;
    if (length > 7) {
      int bottom = start;
      int top = end - 1;
      if (length > 40) {
        length /= 8;
        bottom = med3(array, bottom, bottom + length, bottom + (2 * length), comp);
        middle = med3(array, middle - length, middle, middle + length, comp);
        top = med3(array, top - (2 * length), top - length, top, comp);
      }
      middle = med3(array, bottom, middle, top, comp);
    }
    int partionValue = array[middle];
    int a, b, c, d;
    a = b = start;
    c = d = end - 1;
    while (true) {
      int comparison;
      while (b <= c && (comparison = comp.compare(array[b], partionValue)) <= 0) {
        if (comparison == 0) {
          temp = array[a];
          array[a++] = array[b];
          array[b] = temp;
        }
        b++;
      }
      while (c >= b && (comparison = comp.compare(array[c], partionValue)) >= 0) {
        if (comparison == 0) {
          temp = array[c];
          array[c] = array[d];
          array[d--] = temp;
        }
        c--;
      }
      if (b > c) {
        break;
      }
      temp = array[b];
      array[b++] = array[c];
      array[c--] = temp;
    }
    length = a - start < b - a ? a - start : b - a;
    int l = start;
    int h = b - length;
    while (length-- > 0) {
      temp = array[l];
      array[l++] = array[h];
      array[h++] = temp;
    }
    length = d - c < end - 1 - d ? d - c : end - 1 - d;
    l = b;
    h = end - length;
    while (length-- > 0) {
      temp = array[l];
      array[l++] = array[h];
      array[h++] = temp;
    }
    if ((length = b - a) > 0) {
      quickSort0(start, start + length, array, comp);
    }
    if ((length = d - c) > 0) {
      quickSort0(end - length, end, array, comp);
    }
  }

  public static void quickSort(long[] array, int start, int end, LongComparator comp) {
    if (array == null) {
      throw new NullPointerException();
    }
    checkBounds(array.length, start, end);
    quickSort0(start, end, array, comp);
  }

  private static void quickSort0(int start, int end, long[] array, LongComparator comp) {
    long temp;
    int length = end - start;
    if (length < 7) {
      for (int i = start + 1; i < end; i++) {
        for (int j = i; j > start && comp.compare(array[j - 1], array[j]) > 0; j--) {
          temp = array[j];
          array[j] = array[j - 1];
          array[j - 1] = temp;
        }
      }
      return;
    }
    int middle = (start + end) / 2;
    if (length > 7) {
      int bottom = start;
      int top = end - 1;
      if (length > 40) {
        length /= 8;
        bottom = med3(array, bottom, bottom + length, bottom + (2 * length), comp);
        middle = med3(array, middle - length, middle, middle + length, comp);
        top = med3(array, top - (2 * length), top - length, top, comp);
      }
      middle = med3(array, bottom, middle, top, comp);
    }
    long partionValue = array[middle];
    int a, b, c, d;
    a = b = start;
    c = d = end - 1;
    while (true) {
      int comparison;
      while (b <= c && (comparison = comp.compare(array[b], partionValue)) <= 0) {
        if (comparison == 0) {
          temp = array[a];
          array[a++] = array[b];
          array[b] = temp;
        }
        b++;
      }
      while (c >= b && (comparison = comp.compare(array[c], partionValue)) >= 0) {
        if (comparison == 0) {
          temp = array[c];
          array[c] = array[d];
          array[d--] = temp;
        }
        c--;
      }
      if (b > c) {
        break;
      }
      temp = array[b];
      array[b++] = array[c];
      array[c--] = temp;
    }
    length = a - start < b - a ? a - start : b - a;
    int l = start;
    int h = b - length;
    while (length-- > 0) {
      temp = array[l];
      array[l++] = array[h];
      array[h++] = temp;
    }
    length = d - c < end - 1 - d ? d - c : end - 1 - d;
    l = b;
    h = end - length;
    while (length-- > 0) {
      temp = array[l];
      array[l++] = array[h];
      array[h++] = temp;
    }
    if ((length = b - a) > 0) {
      quickSort0(start, start + length, array, comp);
    }
    if ((length = d - c) > 0) {
      quickSort0(end - length, end, array, comp);
    }
  }

  public static <T> void quickSort(T[] array, int start, int end, Comparator<T> comp) {
    if (array == null) {
      throw new NullPointerException();
    }
    checkBounds(array.length, start, end);
    quickSort0(start, end, array, comp);
  }

  private static final class ComparableAdaptor<T extends Comparable<? super T>>
      implements Comparator<T> {

    @Override
    public int compare(T o1, T o2) {
      return o1.compareTo(o2);
    }
  }

  public static <T extends Comparable<? super T>> void quickSort(T[] array, int start, int end) {
    quickSort(array, start, end, new ComparableAdaptor<T>());
  }

  private static <T> void quickSort0(int start, int end, T[] array, Comparator<T> comp) {
    T temp;
    int length = end - start;
    if (length < 7) {
      for (int i = start + 1; i < end; i++) {
        for (int j = i; j > start && comp.compare(array[j - 1], array[j]) > 0; j--) {
          temp = array[j];
          array[j] = array[j - 1];
          array[j - 1] = temp;
        }
      }
      return;
    }
    int middle = (start + end) / 2;
    if (length > 7) {
      int bottom = start;
      int top = end - 1;
      if (length > 40) {
        length /= 8;
        bottom = med3(array, bottom, bottom + length, bottom + (2 * length), comp);
        middle = med3(array, middle - length, middle, middle + length, comp);
        top = med3(array, top - (2 * length), top - length, top, comp);
      }
      middle = med3(array, bottom, middle, top, comp);
    }
    T partionValue = array[middle];
    int a, b, c, d;
    a = b = start;
    c = d = end - 1;
    while (true) {
      int comparison;
      while (b <= c && (comparison = comp.compare(array[b], partionValue)) <= 0) {
        if (comparison == 0) {
          temp = array[a];
          array[a++] = array[b];
          array[b] = temp;
        }
        b++;
      }
      while (c >= b && (comparison = comp.compare(array[c], partionValue)) >= 0) {
        if (comparison == 0) {
          temp = array[c];
          array[c] = array[d];
          array[d--] = temp;
        }
        c--;
      }
      if (b > c) {
        break;
      }
      temp = array[b];
      array[b++] = array[c];
      array[c--] = temp;
    }
    length = a - start < b - a ? a - start : b - a;
    int l = start;
    int h = b - length;
    while (length-- > 0) {
      temp = array[l];
      array[l++] = array[h];
      array[h++] = temp;
    }
    length = d - c < end - 1 - d ? d - c : end - 1 - d;
    l = b;
    h = end - length;
    while (length-- > 0) {
      temp = array[l];
      array[l++] = array[h];
      array[h++] = temp;
    }
    if ((length = b - a) > 0) {
      quickSort0(start, start + length, array, comp);
    }
    if ((length = d - c) > 0) {
      quickSort0(end - length, end, array, comp);
    }
  }

  public static void quickSort(short[] array, int start, int end, ShortComparator comp) {
    if (array == null) {
      throw new NullPointerException();
    }
    checkBounds(array.length, start, end);
    quickSort0(start, end, array, comp);
  }

  private static void quickSort0(int start, int end, short[] array, ShortComparator comp) {
    short temp;
    int length = end - start;
    if (length < 7) {
      for (int i = start + 1; i < end; i++) {
        for (int j = i; j > start && comp.compare(array[j - 1], array[j]) > 0; j--) {
          temp = array[j];
          array[j] = array[j - 1];
          array[j - 1] = temp;
        }
      }
      return;
    }
    int middle = (start + end) / 2;
    if (length > 7) {
      int bottom = start;
      int top = end - 1;
      if (length > 40) {
        length /= 8;
        bottom = med3(array, bottom, bottom + length, bottom + (2 * length), comp);
        middle = med3(array, middle - length, middle, middle + length, comp);
        top = med3(array, top - (2 * length), top - length, top, comp);
      }
      middle = med3(array, bottom, middle, top, comp);
    }
    short partionValue = array[middle];
    int a, b, c, d;
    a = b = start;
    c = d = end - 1;
    while (true) {
      int comparison;
      while (b <= c && (comparison = comp.compare(array[b], partionValue)) < 0) {
        if (comparison == 0) {
          temp = array[a];
          array[a++] = array[b];
          array[b] = temp;
        }
        b++;
      }
      while (c >= b && (comparison = comp.compare(array[c], partionValue)) > 0) {
        if (comparison == 0) {
          temp = array[c];
          array[c] = array[d];
          array[d--] = temp;
        }
        c--;
      }
      if (b > c) {
        break;
      }
      temp = array[b];
      array[b++] = array[c];
      array[c--] = temp;
    }
    length = a - start < b - a ? a - start : b - a;
    int l = start;
    int h = b - length;
    while (length-- > 0) {
      temp = array[l];
      array[l++] = array[h];
      array[h++] = temp;
    }
    length = d - c < end - 1 - d ? d - c : end - 1 - d;
    l = b;
    h = end - length;
    while (length-- > 0) {
      temp = array[l];
      array[l++] = array[h];
      array[h++] = temp;
    }
    if ((length = b - a) > 0) {
      quickSort0(start, start + length, array, comp);
    }
    if ((length = d - c) > 0) {
      quickSort0(end - length, end, array, comp);
    }
  }

  @SuppressWarnings("unchecked") // required to make the temp array work, afaict.
  public static <T> void mergeSort(T[] array, int start, int end, Comparator<T> comp) {
    checkBounds(array.length, start, end);
    int length = end - start;
    if (length <= 0) {
      return;
    }

    T[] out = (T[]) new Object[array.length];
    System.arraycopy(array, start, out, start, length);
    mergeSort(out, array, start, end, comp);
  }

  public static <T extends Comparable<? super T>> void mergeSort(T[] array, int start, int end) {
    mergeSort(array, start, end, new ComparableAdaptor<T>());
  }

  private static <T> void mergeSort(T[] in, T[] out, int start, int end, Comparator<T> c) {
    int len = end - start;
    // use insertion sort for small arrays
    if (len <= SIMPLE_LENGTH) {
      for (int i = start + 1; i < end; i++) {
        T current = out[i];
        T prev = out[i - 1];
        if (c.compare(prev, current) > 0) {
          int j = i;
          do {
            out[j--] = prev;
          } while (j > start && (c.compare(prev = out[j - 1], current) > 0));
          out[j] = current;
        }
      }
      return;
    }
    int med = (end + start) >>> 1;
    mergeSort(out, in, start, med, c);
    mergeSort(out, in, med, end, c);

    // merging

    // if arrays are already sorted - no merge
    if (c.compare(in[med - 1], in[med]) <= 0) {
      System.arraycopy(in, start, out, start, len);
      return;
    }
    int r = med, i = start;

    // use merging with exponential search
    do {
      T fromVal = in[start];
      T rVal = in[r];
      if (c.compare(fromVal, rVal) <= 0) {
        int l_1 = find(in, rVal, -1, start + 1, med - 1, c);
        int toCopy = l_1 - start + 1;
        System.arraycopy(in, start, out, i, toCopy);
        i += toCopy;
        out[i++] = rVal;
        r++;
        start = l_1 + 1;
      } else {
        int r_1 = find(in, fromVal, 0, r + 1, end - 1, c);
        int toCopy = r_1 - r + 1;
        System.arraycopy(in, r, out, i, toCopy);
        i += toCopy;
        out[i++] = fromVal;
        start++;
        r = r_1 + 1;
      }
    } while ((end - r) > 0 && (med - start) > 0);

    // copy rest of array
    if ((end - r) <= 0) {
      System.arraycopy(in, start, out, i, med - start);
    } else {
      System.arraycopy(in, r, out, i, end - r);
    }
  }

  private static <T> int find(T[] arr, T val, int bnd, int l, int r, Comparator<T> c) {
    int m = l;
    int d = 1;
    while (m <= r) {
      if (c.compare(val, arr[m]) > bnd) {
        l = m + 1;
      } else {
        r = m - 1;
        break;
      }
      m += d;
      d <<= 1;
    }
    while (l <= r) {
      m = (l + r) >>> 1;
      if (c.compare(val, arr[m]) > bnd) {
        l = m + 1;
      } else {
        r = m - 1;
      }
    }
    return l - 1;
  }

  private static final ByteComparator naturalByteComparison =
      new ByteComparator() {
        @Override
        public int compare(byte o1, byte o2) {
          return o1 - o2;
        }
      };

  public static void mergeSort(byte[] array, int start, int end) {
    mergeSort(array, start, end, naturalByteComparison);
  }

  public static void mergeSort(byte[] array, int start, int end, ByteComparator comp) {
    checkBounds(array.length, start, end);
    byte[] out = Arrays.copyOf(array, array.length);
    mergeSort(out, array, start, end, comp);
  }

  private static void mergeSort(byte[] in, byte[] out, int start, int end, ByteComparator c) {
    int len = end - start;
    // use insertion sort for small arrays
    if (len <= SIMPLE_LENGTH) {
      for (int i = start + 1; i < end; i++) {
        byte current = out[i];
        byte prev = out[i - 1];
        if (c.compare(prev, current) > 0) {
          int j = i;
          do {
            out[j--] = prev;
          } while (j > start && (c.compare(prev = out[j - 1], current) > 0));
          out[j] = current;
        }
      }
      return;
    }
    int med = (end + start) >>> 1;
    mergeSort(out, in, start, med, c);
    mergeSort(out, in, med, end, c);

    // merging

    // if arrays are already sorted - no merge
    if (c.compare(in[med - 1], in[med]) <= 0) {
      System.arraycopy(in, start, out, start, len);
      return;
    }
    int r = med, i = start;

    // use merging with exponential search
    do {
      byte fromVal = in[start];
      byte rVal = in[r];
      if (c.compare(fromVal, rVal) <= 0) {
        int l_1 = find(in, rVal, -1, start + 1, med - 1, c);
        int toCopy = l_1 - start + 1;
        System.arraycopy(in, start, out, i, toCopy);
        i += toCopy;
        out[i++] = rVal;
        r++;
        start = l_1 + 1;
      } else {
        int r_1 = find(in, fromVal, 0, r + 1, end - 1, c);
        int toCopy = r_1 - r + 1;
        System.arraycopy(in, r, out, i, toCopy);
        i += toCopy;
        out[i++] = fromVal;
        start++;
        r = r_1 + 1;
      }
    } while ((end - r) > 0 && (med - start) > 0);

    // copy rest of array
    if ((end - r) <= 0) {
      System.arraycopy(in, start, out, i, med - start);
    } else {
      System.arraycopy(in, r, out, i, end - r);
    }
  }

  private static int find(byte[] arr, byte val, int bnd, int l, int r, ByteComparator c) {
    int m = l;
    int d = 1;
    while (m <= r) {
      if (c.compare(val, arr[m]) > bnd) {
        l = m + 1;
      } else {
        r = m - 1;
        break;
      }
      m += d;
      d <<= 1;
    }
    while (l <= r) {
      m = (l + r) >>> 1;
      if (c.compare(val, arr[m]) > bnd) {
        l = m + 1;
      } else {
        r = m - 1;
      }
    }
    return l - 1;
  }

  private static final CharComparator naturalCharComparison =
      new CharComparator() {
        @Override
        public int compare(char o1, char o2) {
          return o1 - o2;
        }
      };

  public static void mergeSort(char[] array, int start, int end) {
    mergeSort(array, start, end, naturalCharComparison);
  }

  public static void mergeSort(char[] array, int start, int end, CharComparator comp) {
    checkBounds(array.length, start, end);
    char[] out = Arrays.copyOf(array, array.length);
    mergeSort(out, array, start, end, comp);
  }

  private static void mergeSort(char[] in, char[] out, int start, int end, CharComparator c) {
    int len = end - start;
    // use insertion sort for small arrays
    if (len <= SIMPLE_LENGTH) {
      for (int i = start + 1; i < end; i++) {
        char current = out[i];
        char prev = out[i - 1];
        if (c.compare(prev, current) > 0) {
          int j = i;
          do {
            out[j--] = prev;
          } while (j > start && (c.compare(prev = out[j - 1], current) > 0));
          out[j] = current;
        }
      }
      return;
    }
    int med = (end + start) >>> 1;
    mergeSort(out, in, start, med, c);
    mergeSort(out, in, med, end, c);

    // merging

    // if arrays are already sorted - no merge
    if (c.compare(in[med - 1], in[med]) <= 0) {
      System.arraycopy(in, start, out, start, len);
      return;
    }
    int r = med, i = start;

    // use merging with exponential search
    do {
      char fromVal = in[start];
      char rVal = in[r];
      if (c.compare(fromVal, rVal) <= 0) {
        int l_1 = find(in, rVal, -1, start + 1, med - 1, c);
        int toCopy = l_1 - start + 1;
        System.arraycopy(in, start, out, i, toCopy);
        i += toCopy;
        out[i++] = rVal;
        r++;
        start = l_1 + 1;
      } else {
        int r_1 = find(in, fromVal, 0, r + 1, end - 1, c);
        int toCopy = r_1 - r + 1;
        System.arraycopy(in, r, out, i, toCopy);
        i += toCopy;
        out[i++] = fromVal;
        start++;
        r = r_1 + 1;
      }
    } while ((end - r) > 0 && (med - start) > 0);

    // copy rest of array
    if ((end - r) <= 0) {
      System.arraycopy(in, start, out, i, med - start);
    } else {
      System.arraycopy(in, r, out, i, end - r);
    }
  }

  private static int find(char[] arr, char val, int bnd, int l, int r, CharComparator c) {
    int m = l;
    int d = 1;
    while (m <= r) {
      if (c.compare(val, arr[m]) > bnd) {
        l = m + 1;
      } else {
        r = m - 1;
        break;
      }
      m += d;
      d <<= 1;
    }
    while (l <= r) {
      m = (l + r) >>> 1;
      if (c.compare(val, arr[m]) > bnd) {
        l = m + 1;
      } else {
        r = m - 1;
      }
    }
    return l - 1;
  }

  private static final ShortComparator naturalShortComparison =
      new ShortComparator() {
        @Override
        public int compare(short o1, short o2) {
          return o1 - o2;
        }
      };

  public static void mergeSort(short[] array, int start, int end) {
    mergeSort(array, start, end, naturalShortComparison);
  }

  public static void mergeSort(short[] array, int start, int end, ShortComparator comp) {
    checkBounds(array.length, start, end);
    short[] out = Arrays.copyOf(array, array.length);
    mergeSort(out, array, start, end, comp);
  }

  private static void mergeSort(short[] in, short[] out, int start, int end, ShortComparator c) {
    int len = end - start;
    // use insertion sort for small arrays
    if (len <= SIMPLE_LENGTH) {
      for (int i = start + 1; i < end; i++) {
        short current = out[i];
        short prev = out[i - 1];
        if (c.compare(prev, current) > 0) {
          int j = i;
          do {
            out[j--] = prev;
          } while (j > start && (c.compare(prev = out[j - 1], current) > 0));
          out[j] = current;
        }
      }
      return;
    }
    int med = (end + start) >>> 1;
    mergeSort(out, in, start, med, c);
    mergeSort(out, in, med, end, c);

    // merging

    // if arrays are already sorted - no merge
    if (c.compare(in[med - 1], in[med]) <= 0) {
      System.arraycopy(in, start, out, start, len);
      return;
    }
    int r = med, i = start;

    // use merging with exponential search
    do {
      short fromVal = in[start];
      short rVal = in[r];
      if (c.compare(fromVal, rVal) <= 0) {
        int l_1 = find(in, rVal, -1, start + 1, med - 1, c);
        int toCopy = l_1 - start + 1;
        System.arraycopy(in, start, out, i, toCopy);
        i += toCopy;
        out[i++] = rVal;
        r++;
        start = l_1 + 1;
      } else {
        int r_1 = find(in, fromVal, 0, r + 1, end - 1, c);
        int toCopy = r_1 - r + 1;
        System.arraycopy(in, r, out, i, toCopy);
        i += toCopy;
        out[i++] = fromVal;
        start++;
        r = r_1 + 1;
      }
    } while ((end - r) > 0 && (med - start) > 0);

    // copy rest of array
    if ((end - r) <= 0) {
      System.arraycopy(in, start, out, i, med - start);
    } else {
      System.arraycopy(in, r, out, i, end - r);
    }
  }

  private static int find(short[] arr, short val, int bnd, int l, int r, ShortComparator c) {
    int m = l;
    int d = 1;
    while (m <= r) {
      if (c.compare(val, arr[m]) > bnd) {
        l = m + 1;
      } else {
        r = m - 1;
        break;
      }
      m += d;
      d <<= 1;
    }
    while (l <= r) {
      m = (l + r) >>> 1;
      if (c.compare(val, arr[m]) > bnd) {
        l = m + 1;
      } else {
        r = m - 1;
      }
    }
    return l - 1;
  }

  private static final IntComparator naturalIntComparison =
      new IntComparator() {
        @Override
        public int compare(int o1, int o2) {
          return o1 < o2 ? -1 : o1 > o2 ? 1 : 0;
        }
      };

  public static void mergeSort(int[] array, int start, int end) {
    mergeSort(array, start, end, naturalIntComparison);
  }

  public static void mergeSort(int[] array, int start, int end, IntComparator comp) {
    checkBounds(array.length, start, end);
    int[] out = Arrays.copyOf(array, array.length);
    mergeSort(out, array, start, end, comp);
  }

  private static void mergeSort(int[] in, int[] out, int start, int end, IntComparator c) {
    int len = end - start;
    // use insertion sort for small arrays
    if (len <= SIMPLE_LENGTH) {
      for (int i = start + 1; i < end; i++) {
        int current = out[i];
        int prev = out[i - 1];
        if (c.compare(prev, current) > 0) {
          int j = i;
          do {
            out[j--] = prev;
          } while (j > start && (c.compare(prev = out[j - 1], current) > 0));
          out[j] = current;
        }
      }
      return;
    }
    int med = (end + start) >>> 1;
    mergeSort(out, in, start, med, c);
    mergeSort(out, in, med, end, c);

    // merging

    // if arrays are already sorted - no merge
    if (c.compare(in[med - 1], in[med]) <= 0) {
      System.arraycopy(in, start, out, start, len);
      return;
    }
    int r = med, i = start;

    // use merging with exponential search
    do {
      int fromVal = in[start];
      int rVal = in[r];
      if (c.compare(fromVal, rVal) <= 0) {
        int l_1 = find(in, rVal, -1, start + 1, med - 1, c);
        int toCopy = l_1 - start + 1;
        System.arraycopy(in, start, out, i, toCopy);
        i += toCopy;
        out[i++] = rVal;
        r++;
        start = l_1 + 1;
      } else {
        int r_1 = find(in, fromVal, 0, r + 1, end - 1, c);
        int toCopy = r_1 - r + 1;
        System.arraycopy(in, r, out, i, toCopy);
        i += toCopy;
        out[i++] = fromVal;
        start++;
        r = r_1 + 1;
      }
    } while ((end - r) > 0 && (med - start) > 0);

    // copy rest of array
    if ((end - r) <= 0) {
      System.arraycopy(in, start, out, i, med - start);
    } else {
      System.arraycopy(in, r, out, i, end - r);
    }
  }

  private static int find(int[] arr, int val, int bnd, int l, int r, IntComparator c) {
    int m = l;
    int d = 1;
    while (m <= r) {
      if (c.compare(val, arr[m]) > bnd) {
        l = m + 1;
      } else {
        r = m - 1;
        break;
      }
      m += d;
      d <<= 1;
    }
    while (l <= r) {
      m = (l + r) >>> 1;
      if (c.compare(val, arr[m]) > bnd) {
        l = m + 1;
      } else {
        r = m - 1;
      }
    }
    return l - 1;
  }

  private static final LongComparator naturalLongComparison =
      new LongComparator() {
        @Override
        public int compare(long o1, long o2) {
          return o1 < o2 ? -1 : o1 > o2 ? 1 : 0;
        }
      };

  public static void mergeSort(long[] array, int start, int end) {
    mergeSort(array, start, end, naturalLongComparison);
  }

  public static void mergeSort(long[] array, int start, int end, LongComparator comp) {
    checkBounds(array.length, start, end);
    long[] out = Arrays.copyOf(array, array.length);
    mergeSort(out, array, start, end, comp);
  }

  private static void mergeSort(long[] in, long[] out, int start, int end, LongComparator c) {
    int len = end - start;
    // use insertion sort for small arrays
    if (len <= SIMPLE_LENGTH) {
      for (int i = start + 1; i < end; i++) {
        long current = out[i];
        long prev = out[i - 1];
        if (c.compare(prev, current) > 0) {
          int j = i;
          do {
            out[j--] = prev;
          } while (j > start && (c.compare(prev = out[j - 1], current) > 0));
          out[j] = current;
        }
      }
      return;
    }
    int med = (end + start) >>> 1;
    mergeSort(out, in, start, med, c);
    mergeSort(out, in, med, end, c);

    // merging

    // if arrays are already sorted - no merge
    if (c.compare(in[med - 1], in[med]) <= 0) {
      System.arraycopy(in, start, out, start, len);
      return;
    }
    int r = med, i = start;

    // use merging with exponential search
    do {
      long fromVal = in[start];
      long rVal = in[r];
      if (c.compare(fromVal, rVal) <= 0) {
        int l_1 = find(in, rVal, -1, start + 1, med - 1, c);
        int toCopy = l_1 - start + 1;
        System.arraycopy(in, start, out, i, toCopy);
        i += toCopy;
        out[i++] = rVal;
        r++;
        start = l_1 + 1;
      } else {
        int r_1 = find(in, fromVal, 0, r + 1, end - 1, c);
        int toCopy = r_1 - r + 1;
        System.arraycopy(in, r, out, i, toCopy);
        i += toCopy;
        out[i++] = fromVal;
        start++;
        r = r_1 + 1;
      }
    } while ((end - r) > 0 && (med - start) > 0);

    // copy rest of array
    if ((end - r) <= 0) {
      System.arraycopy(in, start, out, i, med - start);
    } else {
      System.arraycopy(in, r, out, i, end - r);
    }
  }

  private static int find(long[] arr, long val, int bnd, int l, int r, LongComparator c) {
    int m = l;
    int d = 1;
    while (m <= r) {
      if (c.compare(val, arr[m]) > bnd) {
        l = m + 1;
      } else {
        r = m - 1;
        break;
      }
      m += d;
      d <<= 1;
    }
    while (l <= r) {
      m = (l + r) >>> 1;
      if (c.compare(val, arr[m]) > bnd) {
        l = m + 1;
      } else {
        r = m - 1;
      }
    }
    return l - 1;
  }

  private static final FloatComparator naturalFloatComparison =
      new FloatComparator() {
        @Override
        public int compare(float o1, float o2) {
          return Float.compare(o1, o2);
        }
      };

  public static void mergeSort(float[] array, int start, int end) {
    mergeSort(array, start, end, naturalFloatComparison);
  }

  public static void mergeSort(float[] array, int start, int end, FloatComparator comp) {
    checkBounds(array.length, start, end);
    float[] out = Arrays.copyOf(array, array.length);
    mergeSort(out, array, start, end, comp);
  }

  private static void mergeSort(float[] in, float[] out, int start, int end, FloatComparator c) {
    int len = end - start;
    // use insertion sort for small arrays
    if (len <= SIMPLE_LENGTH) {
      for (int i = start + 1; i < end; i++) {
        float current = out[i];
        float prev = out[i - 1];
        if (c.compare(prev, current) > 0) {
          int j = i;
          do {
            out[j--] = prev;
          } while (j > start && (c.compare(prev = out[j - 1], current) > 0));
          out[j] = current;
        }
      }
      return;
    }
    int med = (end + start) >>> 1;
    mergeSort(out, in, start, med, c);
    mergeSort(out, in, med, end, c);

    // merging

    // if arrays are already sorted - no merge
    if (c.compare(in[med - 1], in[med]) <= 0) {
      System.arraycopy(in, start, out, start, len);
      return;
    }
    int r = med, i = start;

    // use merging with exponential search
    do {
      float fromVal = in[start];
      float rVal = in[r];
      if (c.compare(fromVal, rVal) <= 0) {
        int l_1 = find(in, rVal, -1, start + 1, med - 1, c);
        int toCopy = l_1 - start + 1;
        System.arraycopy(in, start, out, i, toCopy);
        i += toCopy;
        out[i++] = rVal;
        r++;
        start = l_1 + 1;
      } else {
        int r_1 = find(in, fromVal, 0, r + 1, end - 1, c);
        int toCopy = r_1 - r + 1;
        System.arraycopy(in, r, out, i, toCopy);
        i += toCopy;
        out[i++] = fromVal;
        start++;
        r = r_1 + 1;
      }
    } while ((end - r) > 0 && (med - start) > 0);

    // copy rest of array
    if ((end - r) <= 0) {
      System.arraycopy(in, start, out, i, med - start);
    } else {
      System.arraycopy(in, r, out, i, end - r);
    }
  }

  private static int find(float[] arr, float val, int bnd, int l, int r, FloatComparator c) {
    int m = l;
    int d = 1;
    while (m <= r) {
      if (c.compare(val, arr[m]) > bnd) {
        l = m + 1;
      } else {
        r = m - 1;
        break;
      }
      m += d;
      d <<= 1;
    }
    while (l <= r) {
      m = (l + r) >>> 1;
      if (c.compare(val, arr[m]) > bnd) {
        l = m + 1;
      } else {
        r = m - 1;
      }
    }
    return l - 1;
  }

  private static final DoubleComparator naturalDoubleComparison =
      new DoubleComparator() {
        @Override
        public int compare(double o1, double o2) {
          return Double.compare(o1, o2);
        }
      };

  public static void mergeSort(double[] array, int start, int end) {
    mergeSort(array, start, end, naturalDoubleComparison);
  }

  public static void mergeSort(double[] array, int start, int end, DoubleComparator comp) {
    checkBounds(array.length, start, end);
    double[] out = Arrays.copyOf(array, array.length);
    mergeSort(out, array, start, end, comp);
  }

  private static void mergeSort(double[] in, double[] out, int start, int end, DoubleComparator c) {
    int len = end - start;
    // use insertion sort for small arrays
    if (len <= SIMPLE_LENGTH) {
      for (int i = start + 1; i < end; i++) {
        double current = out[i];
        double prev = out[i - 1];
        if (c.compare(prev, current) > 0) {
          int j = i;
          do {
            out[j--] = prev;
          } while (j > start && (c.compare(prev = out[j - 1], current) > 0));
          out[j] = current;
        }
      }
      return;
    }
    int med = (end + start) >>> 1;
    mergeSort(out, in, start, med, c);
    mergeSort(out, in, med, end, c);

    // merging

    // if arrays are already sorted - no merge
    if (c.compare(in[med - 1], in[med]) <= 0) {
      System.arraycopy(in, start, out, start, len);
      return;
    }
    int r = med, i = start;

    // use merging with exponential search
    do {
      double fromVal = in[start];
      double rVal = in[r];
      if (c.compare(fromVal, rVal) <= 0) {
        int l_1 = find(in, rVal, -1, start + 1, med - 1, c);
        int toCopy = l_1 - start + 1;
        System.arraycopy(in, start, out, i, toCopy);
        i += toCopy;
        out[i++] = rVal;
        r++;
        start = l_1 + 1;
      } else {
        int r_1 = find(in, fromVal, 0, r + 1, end - 1, c);
        int toCopy = r_1 - r + 1;
        System.arraycopy(in, r, out, i, toCopy);
        i += toCopy;
        out[i++] = fromVal;
        start++;
        r = r_1 + 1;
      }
    } while ((end - r) > 0 && (med - start) > 0);

    // copy rest of array
    if ((end - r) <= 0) {
      System.arraycopy(in, start, out, i, med - start);
    } else {
      System.arraycopy(in, r, out, i, end - r);
    }
  }

  private static int find(double[] arr, double val, int bnd, int l, int r, DoubleComparator c) {
    int m = l;
    int d = 1;
    while (m <= r) {
      if (c.compare(val, arr[m]) > bnd) {
        l = m + 1;
      } else {
        r = m - 1;
        break;
      }
      m += d;
      d <<= 1;
    }
    while (l <= r) {
      m = (l + r) >>> 1;
      if (c.compare(val, arr[m]) > bnd) {
        l = m + 1;
      } else {
        r = m - 1;
      }
    }
    return l - 1;
  }

  static void inplace_merge(int first, int middle, int last, IntComparator comp, Swapper swapper) {
    if (first >= middle || middle >= last) {
      return;
    }
    if (last - first == 2) {
      if (comp.compare(middle, first) < 0) {
        swapper.swap(first, middle);
      }
      return;
    }
    int firstCut;
    int secondCut;
    if (middle - first > last - middle) {
      firstCut = first + (middle - first) / 2;
      secondCut = lower_bound(middle, last, firstCut, comp);
    } else {
      secondCut = middle + (last - middle) / 2;
      firstCut = upper_bound(first, middle, secondCut, comp);
    }

    // rotate(firstCut, middle, secondCut, swapper);
    // is manually inlined for speed (jitter inlining seems to work only for small call depths, even
    // if methods are "static private")
    // speedup = 1.7
    // begin inline
    int first2 = firstCut;
    int middle2 = middle;
    int last2 = secondCut;
    if (middle2 != first2 && middle2 != last2) {
      int first1 = first2;
      int last1 = middle2;
      while (first1 < --last1) {
        swapper.swap(first1++, last1);
      }
      first1 = middle2;
      last1 = last2;
      while (first1 < --last1) {
        swapper.swap(first1++, last1);
      }
      first1 = first2;
      last1 = last2;
      while (first1 < --last1) {
        swapper.swap(first1++, last1);
      }
    }
    // end inline

    middle = firstCut + (secondCut - middle);
    inplace_merge(first, firstCut, middle, comp, swapper);
    inplace_merge(middle, secondCut, last, comp, swapper);
  }

  static int lower_bound(int first, int last, int x, IntComparator comp) {
    // if (comp==null) throw new NullPointerException();
    int len = last - first;
    while (len > 0) {
      int half = len / 2;
      int middle = first + half;
      if (comp.compare(middle, x) < 0) {
        first = middle + 1;
        len -= half + 1;
      } else {
        len = half;
      }
    }
    return first;
  }

  public static void mergeSort(int fromIndex, int toIndex, IntComparator c, Swapper swapper) {
    /*
      We retain the same method signature as quickSort.
      Given only a comparator and swapper we do not know how to copy and move elements from/to temporary arrays.
      Hence, in contrast to the JDK mergesorts this is an "in-place" mergesort, i.e. does not allocate any temporary arrays.
      A non-inplace mergesort would perhaps be faster in most cases, but would require non-intuitive delegate objects...
    */
    int length = toIndex - fromIndex;

    // Insertion sort on smallest arrays
    if (length < SMALL) {
      for (int i = fromIndex; i < toIndex; i++) {
        for (int j = i; j > fromIndex && (c.compare(j - 1, j) > 0); j--) {
          swapper.swap(j, j - 1);
        }
      }
      return;
    }

    // Recursively sort halves
    int mid = (fromIndex + toIndex) / 2;
    mergeSort(fromIndex, mid, c, swapper);
    mergeSort(mid, toIndex, c, swapper);

    // If list is already sorted, nothing left to do.  This is an
    // optimization that results in faster sorts for nearly ordered lists.
    if (c.compare(mid - 1, mid) <= 0) {
      return;
    }

    // Merge sorted halves
    inplace_merge(fromIndex, mid, toIndex, c, swapper);
  }

  static int upper_bound(int first, int last, int x, IntComparator comp) {
    // if (comp==null) throw new NullPointerException();
    int len = last - first;
    while (len > 0) {
      int half = len / 2;
      int middle = first + half;
      if (comp.compare(x, middle) < 0) {
        len = half;
      } else {
        first = middle + 1;
        len -= half + 1;
      }
    }
    return first;
  }
}
