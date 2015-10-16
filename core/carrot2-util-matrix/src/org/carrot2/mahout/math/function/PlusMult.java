/* Imported from Mahout. */package org.carrot2.mahout.math.function;



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
