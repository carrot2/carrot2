/* Imported from Mahout. */package org.carrot2.mahout.math.function;



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
