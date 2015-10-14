/* removed */

package org.carrot2.mahout.math;

/* removed */
@SuppressWarnings("serial")
public class CardinalityException extends IllegalArgumentException {

  public CardinalityException(int expected, int cardinality) {
    super("Required cardinality " + expected + " but got " + cardinality);
  }

}
