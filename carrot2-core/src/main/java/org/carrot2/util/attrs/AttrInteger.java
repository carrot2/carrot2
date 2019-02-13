package org.carrot2.util.attrs;

public class AttrInteger {
  private Integer value;

  private AttrInteger(Integer value) {
    this.value = value;
  }

  public void set(int value) {
    this.value = value;
  }

  public Integer get() {
    return value;
  }

  public static class Builder {
    private Integer defaultValue;

    public Builder defaultValue(int value) {
      defaultValue = value;
      return this;
    }

    public AttrInteger build() {
      return new AttrInteger(defaultValue);
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
