/* Imported from Mahout. */package org.carrot2.mahout.math.map;


public final class HashFunctions {

  
  private HashFunctions() {
  }

  
  public static int hash(char value) {
    return (int) value;
  }

  
  public static int hash(double value) {
    long bits = Double.doubleToLongBits(value);
    return (int) (bits ^ (bits >>> 32));

    //return (int) Double.doubleToLongBits(value*663608941.737);
    // this avoids excessive hashCollisions in the case values are of the form (1.0, 2.0, 3.0, ...)
  }

  
  public static int hash(float value) {
    return Float.floatToIntBits(value * 663608941.737f);
    // this avoids excessive hashCollisions in the case values are of the form (1.0, 2.0, 3.0, ...)
  }

  
  public static int hash(int value) {
    return value;

    //return value * 0x278DDE6D; // see org.carrot2.mahout.math.jet.random.engine.DRand

    /*
    value &= 0x7FFFFFFF; // make it >=0
    int hashCode = 0;
    do hashCode = 31*hashCode + value%10;
    while ((value /= 10) > 0);

    return 28629151*hashCode; // spread even further; h*31^5
    */
  }

  
  public static int hash(long value) {
    return (int) (value ^ (value >> 32));
    /*
    value &= 0x7FFFFFFFFFFFFFFFL; // make it >=0 (0x7FFFFFFFFFFFFFFFL==Long.MAX_VALUE)
    int hashCode = 0;
    do hashCode = 31*hashCode + (int) (value%10);
    while ((value /= 10) > 0);

    return 28629151*hashCode; // spread even further; h*31^5
    */
  }

  
  public static int hash(Object object) {
    return object == null ? 0 : object.hashCode();
  }

  
  public static int hash(short value) {
    return (int) value;
  }

  
  public static int hash(boolean value) {
    return value ? 1231 : 1237;
  }
}
