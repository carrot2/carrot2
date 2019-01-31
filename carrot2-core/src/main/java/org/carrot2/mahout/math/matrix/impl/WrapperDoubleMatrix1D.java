/* Imported from Mahout. */package org.carrot2.mahout.math.matrix.impl;

import org.carrot2.mahout.math.matrix.DoubleMatrix1D;
import org.carrot2.mahout.math.matrix.DoubleMatrix2D;


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

  
  @Override
  protected DoubleMatrix1D getContent() {
    return this.content;
  }

  
  @Override
  public double getQuick(int index) {
    return content.getQuick(index);
  }

  
  @Override
  public DoubleMatrix1D like(int size) {
    return content.like(size);
  }

  
  @Override
  public DoubleMatrix2D like2D(int rows, int columns) {
    return content.like2D(rows, columns);
  }

  
  @Override
  public void setQuick(int index, double value) {
    content.setQuick(index, value);
  }

  
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

  
  @Override
  protected DoubleMatrix1D viewSelectionLike(int[] offsets) {
    throw new UnsupportedOperationException(); // should never get called
  }

}
