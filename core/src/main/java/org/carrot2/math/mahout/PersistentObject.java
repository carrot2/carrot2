/* Imported from Mahout. */ package org.carrot2.math.mahout;

public abstract class PersistentObject implements Cloneable {

  protected PersistentObject() {}

  @Override
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException exc) {
      throw new InternalError(); // should never happen since we are cloneable
    }
  }
}
