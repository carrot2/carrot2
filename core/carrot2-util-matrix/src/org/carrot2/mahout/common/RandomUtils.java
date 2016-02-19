
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.mahout.common;




public final class RandomUtils {

  private RandomUtils() { }
  
  
  public static int hashDouble(double value) {
    long v = Double.doubleToLongBits(value);
    return (int) (v ^ (v >>> 32));
  }

  
  public static int hashFloat(float value) {
    return Float.floatToIntBits(value);
  }
  
}
