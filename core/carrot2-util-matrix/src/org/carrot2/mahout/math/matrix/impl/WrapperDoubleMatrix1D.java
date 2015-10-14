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
class WrapperDoubleMatrix1D extends DoubleMatrix1D {
  /*
   * The elements of the matrix.
   */
  private final DoubleMatrix1D content;

  WrapperDoubleMatrix1D(DoubleMatrix1D newContent) {
    if (newContent != null) {
      setUp(newContent.size());
    }
    this.content = newContent;
  }

  /* removed */
  @Override
  protected DoubleMatrix1D getContent() {
    return this.content;
  }

  /* removed */
  @Override
  public double getQuick(int index) {
    return content.getQuick(index);
  }

  /* removed */
  @Override
  public DoubleMatrix1D like(int size) {
    return content.like(size);
  }

  /* removed */
  @Override
  public DoubleMatrix2D like2D(int rows, int columns) {
    return content.like2D(rows, columns);
  }

  /* removed */
  @Override
  public void setQuick(int index, double value) {
    content.setQuick(index, value);
  }

  /* removed */
  @Override
  public DoubleMatrix1D viewPart(final int index, int width) {
    checkRange(index, width);
    DoubleMatrix1D view = new WrapperDoubleMatrix1D(this) {
      @Override
      public double getQuick(int i) {
        return content.get(index + i);
      }

      @Override
      public void setQuick(int i, double value) {
        content.set(index + i, value);
      }
    };
    view.size = width;
    return view;
  }

  /* removed */
  @Override
  protected DoubleMatrix1D viewSelectionLike(int[] offsets) {
    throw new UnsupportedOperationException(); // should never get called
  }

}
