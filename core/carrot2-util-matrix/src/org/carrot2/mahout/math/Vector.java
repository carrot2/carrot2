/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.carrot2.mahout.math;


import org.carrot2.mahout.math.function.DoubleDoubleFunction;
import org.carrot2.mahout.math.function.DoubleFunction;

import java.util.Iterator;

/* removed */
public interface Vector extends Cloneable, Iterable<Vector.Element> {

  /* removed */
  String asFormatString();

  /* removed */
  Vector assign(double value);

  /* removed */
  Vector assign(double[] values);

  /* removed */
  Vector assign(Vector other);

  /* removed */
  Vector assign(DoubleFunction function);

  /* removed */
  Vector assign(Vector other, DoubleDoubleFunction function);

  /* removed */
  Vector assign(DoubleDoubleFunction f, double y);

  /* removed */
  int size();

  /* removed */
  boolean isDense();

  /* removed */
  boolean isSequentialAccess();

  /* removed */
  Vector clone();

  /* removed */
  @Override
  Iterator<Element> iterator();

  /* removed */
  Iterator<Element> iterateNonZero();

  /* removed */
  Element getElement(int index);

  /* removed */
  interface Element {

    /* removed */
    double get();

    /* removed */
    int index();

    /* removed */
    void set(double value);
  }

  /* removed */
  Vector divide(double x);

  /* removed */
  double dot(Vector x);

  /* removed */
  double get(int index);

  /* removed */
  double getQuick(int index);

  /* removed */
  Vector like();

  /* removed */
  Vector minus(Vector x);

  /* removed */
  Vector normalize();

  /* removed */
  Vector normalize(double power);
  
  /* removed */
  Vector logNormalize();

  /* removed */
  Vector logNormalize(double power);

  /* removed */
  double norm(double power);

  /* removed */
  double minValue();

  /* removed */
  int minValueIndex();

  /* removed */
  double maxValue();

  /* removed */
  int maxValueIndex();

  /* removed */
  Vector plus(double x);

  /* removed */
  Vector plus(Vector x);

  /* removed */
  void set(int index, double value);

  /* removed */
  void setQuick(int index, double value);

  /* removed */
  int getNumNondefaultElements();

  /* removed */
  Vector times(double x);

  /* removed */
  Vector times(Vector x);

  /* removed */
  Vector viewPart(int offset, int length);

  /* removed */
  double zSum();

  /* removed */
  Matrix cross(Vector other);

  /*
   * Need stories for these but keeping them here for now.
   */
  // void getNonZeros(IntArrayList jx, DoubleArrayList values);
  // void foreachNonZero(IntDoubleFunction f);
  // DoubleDoubleFunction map);
  // NewVector assign(Vector y, DoubleDoubleFunction function, IntArrayList
  // nonZeroIndexes);

  /* removed */
  double aggregate(DoubleDoubleFunction aggregator, DoubleFunction map);

  /* removed */
  double aggregate(Vector other, DoubleDoubleFunction aggregator, DoubleDoubleFunction combiner);

  /* removed */
  double getLengthSquared();

  /* removed */
  double getDistanceSquared(Vector v);
}
