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
