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
