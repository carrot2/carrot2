/*
Copyright � 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
is hereby granted without fee, provided that the above copyright notice appear in all copies and 
that both that copyright notice and this permission notice appear in supporting documentation. 
CERN makes no representations about the suitability of this software for any purpose. 
It is provided "as is" without expressed or implied warranty.
*/
package org.carrot2.mahout.math.matrix.impl;

import org.carrot2.mahout.math.matrix.DoubleMatrix1D;
import org.carrot2.mahout.math.matrix.DoubleMatrix2D;

/* removed */
class DelegateDoubleMatrix1D extends WrapperDoubleMatrix1D {
  /*
   * The elements of the matrix.
   */
  private final DoubleMatrix2D content;
  /*
  * The row this view is bound to.
  */
  private final int row;

  DelegateDoubleMatrix1D(DoubleMatrix2D newContent, int row) {
    super(null);
    if (row < 0 || row >= newContent.rows()) {
      throw new IllegalArgumentException();
    }
    setUp(newContent.columns());
    this.row = row;
    this.content = newContent;
  }

  /* removed */
  @Override
  public double getQuick(int index) {
    return content.getQuick(row, index);
  }

  /* removed */
  @Override
  public DoubleMatrix1D like(int size) {
    return content.like1D(size);
  }

  /* removed */
  @Override
  public DoubleMatrix2D like2D(int rows, int columns) {
    return content.like(rows, columns);
  }

  /* removed */
  @Override
  public void setQuick(int index, double value) {
    content.setQuick(row, index, value);
  }
}
