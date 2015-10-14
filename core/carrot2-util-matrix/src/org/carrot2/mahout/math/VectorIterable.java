/* removed */

package org.carrot2.mahout.math;

import java.util.Iterator;

public interface VectorIterable extends Iterable<MatrixSlice> {

  Iterator<MatrixSlice> iterateAll();

  int numSlices();

  int numRows();

  int numCols();

  /* removed */
  Vector times(Vector v);

  /* removed */
  Vector timesSquared(Vector v);

}
