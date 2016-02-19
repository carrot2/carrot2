
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

import org.carrot2.mahout.math.function.DoubleDoubleFunction;
import org.carrot2.mahout.math.function.DoubleFunction;
import org.carrot2.mahout.math.function.VectorFunction;

import java.util.Map;


public interface Matrix extends Cloneable, VectorIterable {

  
  String asFormatString();

  
  Matrix assign(double value);

  
  Matrix assign(double[][] values);

  
  Matrix assign(Matrix other);

  
  Matrix assign(DoubleFunction function);

  
  Matrix assign(Matrix other, DoubleDoubleFunction function);

  
  Matrix assignColumn(int column, Vector other);

  
  Matrix assignRow(int row, Vector other);

  
  Vector aggregateRows(VectorFunction f);

  
  Vector aggregateColumns(VectorFunction f);

  
  double aggregate(DoubleDoubleFunction combiner, DoubleFunction mapper);

  
  int columnSize();

  
  int rowSize();

  
  Matrix clone();

  
  double determinant();

  
  Matrix divide(double x);

  
  double get(int row, int column);

  
  double getQuick(int row, int column);

  
  Matrix like();

  
  Matrix like(int rows, int columns);

  
  Matrix minus(Matrix x);

  
  Matrix plus(double x);

  
  Matrix plus(Matrix x);

  
  void set(int row, int column, double value);

  void set(int row, double[] data);

  
  void setQuick(int row, int column, double value);

  
  int[] getNumNondefaultElements();

  
  Matrix times(double x);

  
  Matrix times(Matrix x);

  
  Matrix transpose();

  
  double zSum();

  
  Map<String, Integer> getColumnLabelBindings();

  
  Map<String, Integer> getRowLabelBindings();

  
  void setColumnLabelBindings(Map<String, Integer> bindings);

  
  void setRowLabelBindings(Map<String, Integer> bindings);

  
  double get(String rowLabel, String columnLabel);

  
  void set(String rowLabel, String columnLabel, double value);

  
  void set(String rowLabel, String columnLabel, int row, int column, double value);

  
  void set(String rowLabel, double[] rowData);

  
  void set(String rowLabel, int row, double[] rowData);

  /*
   * Need stories for these but keeping them here for now.
   * 
   */
  // void getNonZeros(IntArrayList jx, DoubleArrayList values);
  // void foreachNonZero(IntDoubleFunction f);
  // double aggregate(DoubleDoubleFunction aggregator, DoubleFunction map);
  // double aggregate(Matrix other, DoubleDoubleFunction aggregator,
  // DoubleDoubleFunction map);
  // NewMatrix assign(Matrix y, DoubleDoubleFunction function, IntArrayList
  // nonZeroIndexes);

  
  Matrix viewPart(int[] offset, int[] size);

  
  Matrix viewPart(int rowOffset, int rowsRequested, int columnOffset, int columnsRequested);

  
  Vector viewRow(int row);

  
  Vector viewColumn(int column);

  
  Vector viewDiagonal();
}
