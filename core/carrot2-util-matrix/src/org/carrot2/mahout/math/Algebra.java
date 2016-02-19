
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

public final class Algebra {

  private Algebra() {
  }

  public static Vector mult(Matrix m, Vector v) {
    if (m.numRows() != v.size()) {
      throw new CardinalityException(m.numRows(), v.size());
    }
    // Use a Dense Vector for the moment,
    Vector result = new DenseVector(m.numRows());
    
    for (int i = 0; i < m.numRows(); i++) {
      result.set(i, m.viewRow(i).dot(v));
    }
    
    return result;
  }
  
  
  public static double hypot(double a, double b) {
    double r;
    if (Math.abs(a) > Math.abs(b)) {
      r = b / a;
      r = Math.abs(a) * Math.sqrt(1 + r * r);
    } else if (b != 0) {
      r = a / b;
      r = Math.abs(b) * Math.sqrt(1 + r * r);
    } else {
      r = 0.0;
    }
    return r;
  }
  
  
  public static double getNorm(Matrix m) {
    double max = 0.0;
    for (int i = 0; i < m.numRows(); i++) {
      int sum = 0;
      Vector cv = m.viewRow(i);
      for (int j = 0; j < cv.size(); j++) {
        sum += (int) Math.abs(cv.getQuick(j));
      }
      if (sum > max) {
        max = sum;
      }
    }
    return max;
  }

}
