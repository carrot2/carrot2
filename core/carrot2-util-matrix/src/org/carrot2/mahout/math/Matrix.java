/* removed */

package org.carrot2.mahout.math;

import org.carrot2.mahout.math.function.DoubleDoubleFunction;
import org.carrot2.mahout.math.function.DoubleFunction;
import org.carrot2.mahout.math.function.VectorFunction;

import java.util.Map;

/* removed */
public interface Matrix extends Cloneable, VectorIterable {

  /* removed */
  String asFormatString();

  /* removed */
  Matrix assign(double value);

  /* removed */
  Matrix assign(double[][] values);

  /* removed */
  Matrix assign(Matrix other);

  /* removed */
  Matrix assign(DoubleFunction function);

  /* removed */
  Matrix assign(Matrix other, DoubleDoubleFunction function);

  /* removed */
  Matrix assignColumn(int column, Vector other);

  /* removed */
  Matrix assignRow(int row, Vector other);

  /* removed */
  Vector aggregateRows(VectorFunction f);

  /* removed */
  Vector aggregateColumns(VectorFunction f);

  /* removed */
  double aggregate(DoubleDoubleFunction combiner, DoubleFunction mapper);

  /* removed */
  int columnSize();

  /* removed */
  int rowSize();

  /* removed */
  Matrix clone();

  /* removed */
  double determinant();

  /* removed */
  Matrix divide(double x);

  /* removed */
  double get(int row, int column);

  /* removed */
  double getQuick(int row, int column);

  /* removed */
  Matrix like();

  /* removed */
  Matrix like(int rows, int columns);

  /* removed */
  Matrix minus(Matrix x);

  /* removed */
  Matrix plus(double x);

  /* removed */
  Matrix plus(Matrix x);

  /* removed */
  void set(int row, int column, double value);

  void set(int row, double[] data);

  /* removed */
  void setQuick(int row, int column, double value);

  /* removed */
  int[] getNumNondefaultElements();

  /* removed */
  Matrix times(double x);

  /* removed */
  Matrix times(Matrix x);

  /* removed */
  Matrix transpose();

  /* removed */
  double zSum();

  /* removed */
  Map<String, Integer> getColumnLabelBindings();

  /* removed */
  Map<String, Integer> getRowLabelBindings();

  /* removed */
  void setColumnLabelBindings(Map<String, Integer> bindings);

  /* removed */
  void setRowLabelBindings(Map<String, Integer> bindings);

  /* removed */
  double get(String rowLabel, String columnLabel);

  /* removed */
  void set(String rowLabel, String columnLabel, double value);

  /* removed */
  void set(String rowLabel, String columnLabel, int row, int column, double value);

  /* removed */
  void set(String rowLabel, double[] rowData);

  /* removed */
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

  /* removed */
  Matrix viewPart(int[] offset, int[] size);

  /* removed */
  Matrix viewPart(int rowOffset, int rowsRequested, int columnOffset, int columnsRequested);

  /* removed */
  Vector viewRow(int row);

  /* removed */
  Vector viewColumn(int column);

  /* removed */
  Vector viewDiagonal();
}
