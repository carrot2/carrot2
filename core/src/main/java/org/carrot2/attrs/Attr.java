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
package org.carrot2.attrs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Attr<T> implements AttrMetadata {
  private final String label;
  private final List<? extends Constraint<? super T>> constraints;

  T value;

  public Attr(T defaultValue, String label, List<? extends Constraint<? super T>> constraints) {
    this.label = label;
    this.constraints = constraints;
    this.value = defaultValue;
  }

  public final String getDescription() {
    return label;
  }

  public T get() {
    return value;
  }

  public void set(T value) {
    constraints.forEach(c -> c.accept(value));
    this.value = value;
  }

  public List<Constraint<? super T>> getConstraints() {
    return Collections.unmodifiableList(constraints);
  }

  protected static class BuilderScaffold<T> {
    protected List<Constraint<? super T>> constraints = new ArrayList<>();
    protected String label;

    protected void addConstraint(Constraint<? super T> c) {
      constraints.add(c);
    }

    protected List<? extends Constraint<? super T>> getConstraint() {
      return constraints;
    }

    public BuilderScaffold<T> label(String label) {
      this.label = label;
      return this;
    }
  }
}
