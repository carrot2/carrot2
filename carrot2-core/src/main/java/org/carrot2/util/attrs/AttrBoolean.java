package org.carrot2.util.attrs;

public class AttrBoolean {
  private Boolean value;

  private AttrBoolean(Boolean value) {
    this.value = value;
  }

  public void set(boolean value) {
    this.value = value;
  }

  public Boolean get() {
    return value;
  }

  public static class Builder {
    private Boolean defaultValue;

    public Builder defaultValue(boolean value) {
      defaultValue = value;
      return this;
    }

    // TODO: add validation/ reporting?
    public Builder label(String cluster_count) {
      return this;
    }

    public AttrBoolean build() {
      return new AttrBoolean(defaultValue);
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
