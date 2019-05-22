/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.attrs;

import java.util.function.Consumer;

public abstract class Attr<T> implements AttrMetadata {
  private final String label;
  private final Consumer<? super T> valueCheck;

  T value;

  public Attr(T defaultValue, String label, Consumer<? super T> constraint) {
    this.label = label;
    this.valueCheck = constraint;
    this.value = defaultValue;
  }

  public final String getDescription() {
    return label;
  }

  public T get() {
    return value;
  }

  public void set(T value) {
    valueCheck.accept(value);
    this.value = value;
  }

  protected static class BuilderScaffold<T> {
    protected Consumer<T> constraint;
    protected String label;

    protected void addConstraint(Consumer<T> c) {
      if (this.constraint == null) {
        this.constraint = c;
      } else {
        this.constraint = this.constraint.andThen(c);
      }
    }

    protected Consumer<T> getConstraint() {
      return constraint == null ? (v) -> {} : constraint;
    }

    public BuilderScaffold<T> label(String label) {
      this.label = label;
      return this;
    }
  }
}
