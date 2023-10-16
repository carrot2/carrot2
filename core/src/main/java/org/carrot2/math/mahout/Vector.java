/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2023, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.math.mahout;

import java.util.Iterator;
import org.carrot2.math.mahout.function.DoubleDoubleFunction;
import org.carrot2.math.mahout.function.DoubleFunction;

public interface Vector extends Cloneable, Iterable<Vector.Element> {

  String asFormatString();

  Vector assign(double value);

  Vector assign(double[] values);

  Vector assign(Vector other);

  Vector assign(DoubleFunction function);

  Vector assign(Vector other, DoubleDoubleFunction function);

  Vector assign(DoubleDoubleFunction f, double y);

  int size();

  boolean isDense();

  boolean isSequentialAccess();

  Vector clone();

  @Override
  Iterator<Element> iterator();

  Iterator<Element> iterateNonZero();

  Element getElement(int index);

  interface Element {

    double get();

    int index();

    void set(double value);
  }

  Vector divide(double x);

  double dot(Vector x);

  double get(int index);

  double getQuick(int index);

  Vector like();

  Vector minus(Vector x);

  Vector normalize();

  Vector normalize(double power);

  Vector logNormalize();

  Vector logNormalize(double power);

  double norm(double power);

  double minValue();

  int minValueIndex();

  double maxValue();

  int maxValueIndex();

  Vector plus(double x);

  Vector plus(Vector x);

  void set(int index, double value);

  void setQuick(int index, double value);

  int getNumNondefaultElements();

  Vector times(double x);

  Vector times(Vector x);

  Vector viewPart(int offset, int length);

  double zSum();

  Matrix cross(Vector other);

  /*
   * Need stories for these but keeping them here for now.
   */
  // void getNonZeros(IntArrayList jx, DoubleArrayList values);
  // void foreachNonZero(IntDoubleFunction f);
  // DoubleDoubleFunction map);
  // NewVector assign(Vector y, DoubleDoubleFunction function, IntArrayList
  // nonZeroIndexes);

  double aggregate(DoubleDoubleFunction aggregator, DoubleFunction map);

  double aggregate(Vector other, DoubleDoubleFunction aggregator, DoubleDoubleFunction combiner);

  double getLengthSquared();

  double getDistanceSquared(Vector v);
}
