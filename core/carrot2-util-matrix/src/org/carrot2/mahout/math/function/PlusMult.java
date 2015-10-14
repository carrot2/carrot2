/* removed */

/*
Copyright 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose
is hereby granted without fee, provided that the above copyright notice appear in all copies and
that both that copyright notice and this permission notice appear in supporting documentation.
CERN makes no representations about the suitability of this software for any purpose.
It is provided "as is" without expressed or implied warranty.
*/

package org.carrot2.mahout.math.function;

/* removed */

public final class PlusMult implements DoubleDoubleFunction {

  private double multiplicator;

  public PlusMult(double multiplicator) {
    this.multiplicator = multiplicator;
  }

  /* removed */
  @Override
  public double apply(double a, double b) {
    return a + b * multiplicator;
  }

  /* removed */
  public static PlusMult minusMult(double constant) {
    return new PlusMult(-constant);
  }

  /* removed */
  public static PlusMult plusMult(double constant) {
    return new PlusMult(constant);
  }

  public double getMultiplicator() {
    return multiplicator;
  }

  public void setMultiplicator(double multiplicator) {
    this.multiplicator = multiplicator;
  }
}
