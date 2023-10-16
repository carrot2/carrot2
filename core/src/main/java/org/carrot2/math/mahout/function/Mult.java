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
package org.carrot2.math.mahout.function;

public final class Mult implements DoubleFunction {

  private double multiplicator;

  Mult(double multiplicator) {
    this.multiplicator = multiplicator;
  }

  @Override
  public double apply(double a) {
    return a * multiplicator;
  }

  public static Mult div(double constant) {
    return mult(1 / constant);
  }

  public static Mult mult(double constant) {
    return new Mult(constant);
  }

  public double getMultiplicator() {
    return multiplicator;
  }

  public void setMultiplicator(double multiplicator) {
    this.multiplicator = multiplicator;
  }
}
