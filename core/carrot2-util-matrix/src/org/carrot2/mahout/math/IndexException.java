/* removed */

package org.carrot2.mahout.math;

/* removed */
@SuppressWarnings("serial")
public class IndexException extends IllegalArgumentException {

  public IndexException(int index, int cardinality) {
    super("Index " + index + " is outside allowable range of [0," + cardinality + ')');
  }

}
