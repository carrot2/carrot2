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

public class AttrDouble extends Attr<Double> {
  private AttrDouble(Double value, Consumer<Double> constraint, String label) {
    super(value, label, constraint);
  }

  public static class Builder extends BuilderScaffold<Double> {
    public Builder min(double minInclusive) {
      addConstraint(
          (v) -> {
            if (v != null && v < minInclusive) {
              throw new IllegalArgumentException("Value must be >= " + minInclusive + ": " + v);
            }
          });
      return this;
    }

    public Builder max(double maxInclusive) {
      addConstraint(
          (v) -> {
            if (v != null && v > maxInclusive) {
              throw new IllegalArgumentException("Value must be <= " + maxInclusive + ": " + v);
            }
          });
      return this;
    }

    @Override
    public Builder label(String label) {
      super.label(label);
      return this;
    }

    public AttrDouble defaultValue(Double defaultValue) {
      return new AttrDouble(defaultValue, getConstraint(), label);
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
