package org.carrot2.attrs;

public class AttrDouble {
  private Double value;

  private AttrDouble(Double value) {
    this.value = value;
  }

  public void set(Double value) {
    this.value = value;
  }

  public Double get() {
    return value;
  }

  public static class Builder {
    private Double defaultValue;

    public Builder defaultValue(double value) {
      defaultValue = value;
      return this;
    }

    // TODO: add validation/ reporting?
    public Builder min(double minInclusive) {
      return this;
    }

    public Builder max(double maxInclusive) {
      return this;
    }

    // TODO: add validation/ reporting?
    public Builder label(String label) {
      return this;
    }

    public AttrDouble build() {
      return new AttrDouble(defaultValue);
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
