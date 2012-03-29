
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2011, Dawid Weiss, Stanislaw Osinski.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core;

import java.lang.reflect.Array;

public class Jdk15 {
  public static <T> T[] copyOf(T[] original, int newLength) {
    Class<?> t = original.getClass().getComponentType();
    T [] newArray = (T[]) Array.newInstance(t, newLength);
    System.arraycopy(original, 0, newArray, 0, Math.min(original.length, newLength));
    return newArray;
  }  
  
  public static double[] copyOf(double[] original, int newLength) {
    double [] newArray = new double [newLength];
    System.arraycopy(original, 0, newArray, 0, Math.min(original.length, newLength));
    return newArray;
  }  

  public static char[] copyOf(char[] original, int newLength) {
    char [] newArray = new char [newLength];
    System.arraycopy(original, 0, newArray, 0, Math.min(original.length, newLength));
    return newArray;
  }  

}