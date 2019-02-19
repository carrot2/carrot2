package org.carrot2.attrs;

import java.util.function.Consumer;

public class AttrInteger extends Attr<Integer> {
  private AttrInteger(Integer value, Consumer<Integer> constraint, String label) {
    super(value, label, constraint);
  }

  public static class Builder extends BuilderScaffold<Integer> {
    public Builder min(int minInclusive) {
      addConstraint((v) -> {
        if (v != null && v < minInclusive) {
          throw new IllegalArgumentException("Value must be >= " + minInclusive + ": " + v);
        }
      });
      return this;
    }

    public Builder max(int maxInclusive) {
      addConstraint((v) -> {
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

    public AttrInteger defaultValue(Integer defaultValue) {
      return new AttrInteger(defaultValue, getConstraint(), label);
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
