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

public final class PlusMult implements DoubleDoubleFunction {

  private double multiplicator;

  public PlusMult(double multiplicator) {
    this.multiplicator = multiplicator;
  }

  @Override
  public double apply(double a, double b) {
    return a + b * multiplicator;
  }

  public static PlusMult minusMult(double constant) {
    return new PlusMult(-constant);
  }

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
