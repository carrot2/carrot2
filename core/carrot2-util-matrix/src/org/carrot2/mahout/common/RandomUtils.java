/* removed */

package org.carrot2.mahout.common;



/* removed */
public final class RandomUtils {

  private RandomUtils() { }
  
  /* removed */
  public static int hashDouble(double value) {
    long v = Double.doubleToLongBits(value);
    return (int) (v ^ (v >>> 32));
  }

  /* removed */
  public static int hashFloat(float value) {
    return Float.floatToIntBits(value);
  }
  
}
