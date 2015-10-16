/* Imported from Mahout. */package org.carrot2.mahout.math.matrix.impl;

import org.carrot2.mahout.math.matrix.DoubleMatrix1D;
import org.carrot2.mahout.math.matrix.DoubleMatrix2D;


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

  
  @Override
  public double getQuick(int index) {
    return content.getQuick(row, index);
  }

  
  @Override
  public DoubleMatrix1D like(int size) {
    return content.like1D(size);
  }

  
  @Override
  public DoubleMatrix2D like2D(int rows, int columns) {
    return content.like(rows, columns);
  }

  
  @Override
  public void setQuick(int index, double value) {
    content.setQuick(row, index, value);
  }
}
