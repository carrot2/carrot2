
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

package org.carrot2.mahout.math;

import java.util.Arrays;


public class DenseMatrix extends AbstractMatrix {

  private double[][] values;

  
  public DenseMatrix(double[][] values) {
    super(values.length, values[0].length);
    // clone the rows
    this.values = new double[values.length][];
    // be careful, need to clone the columns too
    for (int i = 0; i < values.length; i++) {
      this.values[i] = values[i].clone();
    }
  }
  
  
  public DenseMatrix(int rows, int columns) {
    super(rows, columns);
    this.values = new double[rows][columns];
  }

  @Override
  public Matrix clone() {
    DenseMatrix clone = (DenseMatrix) super.clone();
    clone.values = new double[values.length][];
    for (int i = 0; i < values.length; i++) {
      clone.values[i] = values[i].clone();
    }
    return clone;
  }
  
  @Override
  public double getQuick(int row, int column) {
    return values[row][column];
  }
  
  @Override
  public Matrix like() {
    return like(rowSize(), columnSize());
  }
  
  @Override
  public Matrix like(int rows, int columns) {
    return new DenseMatrix(rows, columns);
  }
  
  @Override
  public void setQuick(int row, int column, double value) {
    values[row][column] = value;
  }

  @Override
  public Matrix viewPart(int[] offset, int[] size) {
    int rowOffset = offset[ROW];
    int rowsRequested = size[ROW];
    int columnOffset = offset[COL];
    int columnsRequested = size[COL];

    return viewPart(rowOffset, rowsRequested, columnOffset, columnsRequested);
  }

  @Override
  public Matrix viewPart(int rowOffset, int rowsRequested, int columnOffset, int columnsRequested) {
    if (rowOffset < 0) {
      throw new IndexException(rowOffset, rowSize());
    }
    if (rowOffset + rowsRequested > rowSize()) {
      throw new IndexException(rowOffset + rowsRequested, rowSize());
    }
    if (columnOffset < 0) {
      throw new IndexException(columnOffset, columnSize());
    }
    if (columnOffset + columnsRequested > columnSize()) {
      throw new IndexException(columnOffset + columnsRequested, columnSize());
    }
    return new MatrixView(this, new int[]{rowOffset, columnOffset}, new int[]{rowsRequested, columnsRequested});
  }

  @Override
  public Matrix assign(double value) {
    for (int row = 0; row < rowSize(); row++) {
      Arrays.fill(values[row], value);
    }
    return this;
  }
  
  public Matrix assign(DenseMatrix matrix) {
    // make sure the data field has the correct length
    if (matrix.values[0].length != this.values[0].length || matrix.values.length != this.values.length) {
      this.values = new double[matrix.values.length][matrix.values[0].length];
    }
    // now copy the values
    for (int i = 0; i < this.values.length; i++) {
      System.arraycopy(matrix.values[i], 0, this.values[i], 0, this.values[0].length);
    }
    return this;
  }
  
  @Override
  public Matrix assignColumn(int column, Vector other) {
    if (rowSize() != other.size()) {
      throw new CardinalityException(rowSize(), other.size());
    }
    if (column < 0 || column >= columnSize()) {
      throw new IndexException(column, columnSize());
    }
    for (int row = 0; row < rowSize(); row++) {
      values[row][column] = other.getQuick(row);
    }
    return this;
  }
  
  @Override
  public Matrix assignRow(int row, Vector other) {
    if (columnSize() != other.size()) {
      throw new CardinalityException(columnSize(), other.size());
    }
    if (row < 0 || row >= rowSize()) {
      throw new IndexException(row, rowSize());
    }
    for (int col = 0; col < columnSize(); col++) {
      values[row][col] = other.getQuick(col);
    }
    return this;
  }
  
  @Override
  public Vector viewRow(int row) {
    if (row < 0 || row >= rowSize()) {
      throw new IndexException(row, rowSize());
    }
    return new DenseVector(values[row], true);
  }
  
}
