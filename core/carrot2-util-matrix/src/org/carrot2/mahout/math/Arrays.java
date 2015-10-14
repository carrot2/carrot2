/*
Copyright 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
is hereby granted without fee, provided that the above copyright notice appear in all copies and 
that both that copyright notice and this permission notice appear in supporting documentation. 
CERN makes no representations about the suitability of this software for any purpose. 
It is provided "as is" without expressed or implied warranty.
*/
package org.carrot2.mahout.math;

/* removed */
public final class Arrays {

  private Arrays() {
  }

  /* removed */
  public static byte[] ensureCapacity(byte[] array, int minCapacity) {
    int oldCapacity = array.length;
    byte[] newArray;
    if (minCapacity > oldCapacity) {
      int newCapacity = (oldCapacity * 3) / 2 + 1;
      if (newCapacity < minCapacity) {
        newCapacity = minCapacity;
      }

      newArray = new byte[newCapacity];
      System.arraycopy(array, 0, newArray, 0, oldCapacity);
    } else {
      newArray = array;
    }
    return newArray;
  }

  /* removed */
  public static char[] ensureCapacity(char[] array, int minCapacity) {
    int oldCapacity = array.length;
    char[] newArray;
    if (minCapacity > oldCapacity) {
      int newCapacity = (oldCapacity * 3) / 2 + 1;
      if (newCapacity < minCapacity) {
        newCapacity = minCapacity;
      }

      newArray = new char[newCapacity];
      System.arraycopy(array, 0, newArray, 0, oldCapacity);
    } else {
      newArray = array;
    }
    return newArray;
  }

  /* removed */
  public static double[] ensureCapacity(double[] array, int minCapacity) {
    int oldCapacity = array.length;
    double[] newArray;
    if (minCapacity > oldCapacity) {
      int newCapacity = (oldCapacity * 3) / 2 + 1;
      if (newCapacity < minCapacity) {
        newCapacity = minCapacity;
      }

      newArray = new double[newCapacity];
      //for (int i = oldCapacity; --i >= 0; ) newArray[i] = array[i];
      System.arraycopy(array, 0, newArray, 0, oldCapacity);
    } else {
      newArray = array;
    }
    return newArray;
  }

  /* removed */
  public static float[] ensureCapacity(float[] array, int minCapacity) {
    int oldCapacity = array.length;
    float[] newArray;
    if (minCapacity > oldCapacity) {
      int newCapacity = (oldCapacity * 3) / 2 + 1;
      if (newCapacity < minCapacity) {
        newCapacity = minCapacity;
      }

      newArray = new float[newCapacity];
      System.arraycopy(array, 0, newArray, 0, oldCapacity);
    } else {
      newArray = array;
    }
    return newArray;
  }

  /* removed */
  public static int[] ensureCapacity(int[] array, int minCapacity) {
    int oldCapacity = array.length;
    int[] newArray;
    if (minCapacity > oldCapacity) {
      int newCapacity = (oldCapacity * 3) / 2 + 1;
      if (newCapacity < minCapacity) {
        newCapacity = minCapacity;
      }

      newArray = new int[newCapacity];
      System.arraycopy(array, 0, newArray, 0, oldCapacity);
    } else {
      newArray = array;
    }
    return newArray;
  }

  /* removed */
  public static long[] ensureCapacity(long[] array, int minCapacity) {
    int oldCapacity = array.length;
    long[] newArray;
    if (minCapacity > oldCapacity) {
      int newCapacity = (oldCapacity * 3) / 2 + 1;
      if (newCapacity < minCapacity) {
        newCapacity = minCapacity;
      }

      newArray = new long[newCapacity];
      System.arraycopy(array, 0, newArray, 0, oldCapacity);
    } else {
      newArray = array;
    }
    return newArray;
  }

  /* removed */
  public static Object[] ensureCapacity(Object[] array, int minCapacity) {
    int oldCapacity = array.length;
    Object[] newArray;
    if (minCapacity > oldCapacity) {
      int newCapacity = (oldCapacity * 3) / 2 + 1;
      if (newCapacity < minCapacity) {
        newCapacity = minCapacity;
      }

      newArray = new Object[newCapacity];
      System.arraycopy(array, 0, newArray, 0, oldCapacity);
    } else {
      newArray = array;
    }
    return newArray;
  }

  /* removed */
  public static short[] ensureCapacity(short[] array, int minCapacity) {
    int oldCapacity = array.length;
    short[] newArray;
    if (minCapacity > oldCapacity) {
      int newCapacity = (oldCapacity * 3) / 2 + 1;
      if (newCapacity < minCapacity) {
        newCapacity = minCapacity;
      }

      newArray = new short[newCapacity];
      System.arraycopy(array, 0, newArray, 0, oldCapacity);
    } else {
      newArray = array;
    }
    return newArray;
  }

  /* removed */
  public static boolean[] ensureCapacity(boolean[] array, int minCapacity) {
    int oldCapacity = array.length;
    boolean[] newArray;
    if (minCapacity > oldCapacity) {
      int newCapacity = (oldCapacity * 3) / 2 + 1;
      if (newCapacity < minCapacity) {
        newCapacity = minCapacity;
      }

      newArray = new boolean[newCapacity];
      System.arraycopy(array, 0, newArray, 0, oldCapacity);
    } else {
      newArray = array;
    }
    return newArray;
  }

  /* removed */
  public static String toString(byte[] array) {
    StringBuilder buf = new StringBuilder();
    buf.append('[');
    int maxIndex = array.length - 1;
    for (int i = 0; i <= maxIndex; i++) {
      buf.append(array[i]);
      if (i < maxIndex) {
        buf.append(", ");
      }
    }
    buf.append(']');
    return buf.toString();
  }

  /* removed */
  public static String toString(char[] array) {
    StringBuilder buf = new StringBuilder();
    buf.append('[');
    int maxIndex = array.length - 1;
    for (int i = 0; i <= maxIndex; i++) {
      buf.append(array[i]);
      if (i < maxIndex) {
        buf.append(", ");
      }
    }
    buf.append(']');
    return buf.toString();
  }

  /* removed */
  public static String toString(double[] array) {
    StringBuilder buf = new StringBuilder();
    buf.append('[');
    int maxIndex = array.length - 1;
    for (int i = 0; i <= maxIndex; i++) {
      buf.append(array[i]);
      if (i < maxIndex) {
        buf.append(", ");
      }
    }
    buf.append(']');
    return buf.toString();
  }

  /* removed */
  public static String toString(float[] array) {
    StringBuilder buf = new StringBuilder();
    buf.append('[');
    int maxIndex = array.length - 1;
    for (int i = 0; i <= maxIndex; i++) {
      buf.append(array[i]);
      if (i < maxIndex) {
        buf.append(", ");
      }
    }
    buf.append(']');
    return buf.toString();
  }

  /* removed */
  public static String toString(int[] array) {
    StringBuilder buf = new StringBuilder();
    buf.append('[');
    int maxIndex = array.length - 1;
    for (int i = 0; i <= maxIndex; i++) {
      buf.append(array[i]);
      if (i < maxIndex) {
        buf.append(", ");
      }
    }
    buf.append(']');
    return buf.toString();
  }

  /* removed */
  public static String toString(long[] array) {
    StringBuilder buf = new StringBuilder();
    buf.append('[');
    int maxIndex = array.length - 1;
    for (int i = 0; i <= maxIndex; i++) {
      buf.append(array[i]);
      if (i < maxIndex) {
        buf.append(", ");
      }
    }
    buf.append(']');
    return buf.toString();
  }

  /* removed */
  public static String toString(Object[] array) {
    StringBuilder buf = new StringBuilder();
    buf.append('[');
    int maxIndex = array.length - 1;
    for (int i = 0; i <= maxIndex; i++) {
      buf.append(array[i]);
      if (i < maxIndex) {
        buf.append(", ");
      }
    }
    buf.append(']');
    return buf.toString();
  }

  /* removed */
  public static String toString(short[] array) {
    StringBuilder buf = new StringBuilder();
    buf.append('[');
    int maxIndex = array.length - 1;
    for (int i = 0; i <= maxIndex; i++) {
      buf.append(array[i]);
      if (i < maxIndex) {
        buf.append(", ");
      }
    }
    buf.append(']');
    return buf.toString();
  }

  /* removed */
  public static String toString(boolean[] array) {
    StringBuilder buf = new StringBuilder();
    buf.append('[');
    int maxIndex = array.length - 1;
    for (int i = 0; i <= maxIndex; i++) {
      buf.append(array[i]);
      if (i < maxIndex) {
        buf.append(", ");
      }
    }
    buf.append(']');
    return buf.toString();
  }

  /* removed */
  public static byte[] trimToCapacity(byte[] array, int maxCapacity) {
    if (array.length > maxCapacity) {
      byte[] oldArray = array;
      array = new byte[maxCapacity];
      System.arraycopy(oldArray, 0, array, 0, maxCapacity);
    }
    return array;
  }

  /* removed */
  public static char[] trimToCapacity(char[] array, int maxCapacity) {
    if (array.length > maxCapacity) {
      char[] oldArray = array;
      array = new char[maxCapacity];
      System.arraycopy(oldArray, 0, array, 0, maxCapacity);
    }
    return array;
  }

  /* removed */
  public static double[] trimToCapacity(double[] array, int maxCapacity) {
    if (array.length > maxCapacity) {
      double[] oldArray = array;
      array = new double[maxCapacity];
      System.arraycopy(oldArray, 0, array, 0, maxCapacity);
    }
    return array;
  }

  /* removed */
  public static float[] trimToCapacity(float[] array, int maxCapacity) {
    if (array.length > maxCapacity) {
      float[] oldArray = array;
      array = new float[maxCapacity];
      System.arraycopy(oldArray, 0, array, 0, maxCapacity);
    }
    return array;
  }

  /* removed */
  public static int[] trimToCapacity(int[] array, int maxCapacity) {
    if (array.length > maxCapacity) {
      int[] oldArray = array;
      array = new int[maxCapacity];
      System.arraycopy(oldArray, 0, array, 0, maxCapacity);
    }
    return array;
  }

  /* removed */
  public static long[] trimToCapacity(long[] array, int maxCapacity) {
    if (array.length > maxCapacity) {
      long[] oldArray = array;
      array = new long[maxCapacity];
      System.arraycopy(oldArray, 0, array, 0, maxCapacity);
    }
    return array;
  }

  /* removed */
  public static Object[] trimToCapacity(Object[] array, int maxCapacity) {
    if (array.length > maxCapacity) {
      Object[] oldArray = array;
      array = new Object[maxCapacity];
      System.arraycopy(oldArray, 0, array, 0, maxCapacity);
    }
    return array;
  }

  /* removed */
  public static short[] trimToCapacity(short[] array, int maxCapacity) {
    if (array.length > maxCapacity) {
      short[] oldArray = array;
      array = new short[maxCapacity];
      System.arraycopy(oldArray, 0, array, 0, maxCapacity);
    }
    return array;
  }

  /* removed */
  public static boolean[] trimToCapacity(boolean[] array, int maxCapacity) {
    if (array.length > maxCapacity) {
      boolean[] oldArray = array;
      array = new boolean[maxCapacity];
      System.arraycopy(oldArray, 0, array, 0, maxCapacity);
    }
    return array;
  }

  /* removed */
  public static byte[] copyOf(byte[] src, int length) {
      byte[] result = new byte [length];
      System.arraycopy(src, 0, result, 0, Math.min(length, src.length));
      return result;
  }
  
  /* removed */
  public static char[] copyOf(char[] src, int length) {
      char[] result = new char [length];
      System.arraycopy(src, 0, result, 0, Math.min(length, src.length));
      return result;
  }
  
  /* removed */
  public static short[] copyOf(short[] src, int length) {
      short[] result = new short [length];
      System.arraycopy(src, 0, result, 0, Math.min(length, src.length));
      return result;
  }
  
  /* removed */
  public static int[] copyOf(int[] src, int length) {
      int[] result = new int [length];
      System.arraycopy(src, 0, result, 0, Math.min(length, src.length));
      return result;
  }
  
  /* removed */
  public static float[] copyOf(float[] src, int length) {
      float[] result = new float [length];
      System.arraycopy(src, 0, result, 0, Math.min(length, src.length));
      return result;
  }

  /* removed */
  public static double[] copyOf(double[] src, int length) {
      double[] result = new double [length];
      System.arraycopy(src, 0, result, 0, Math.min(length, src.length));
      return result;
  }
  
  /* removed */
  public static long[] copyOf(long[] src, int length) {
      long[] result = new long [length];
      System.arraycopy(src, 0, result, 0, Math.min(length, src.length));
      return result;
  }
}
