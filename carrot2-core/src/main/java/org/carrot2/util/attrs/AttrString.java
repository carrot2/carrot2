package org.carrot2.util.attrs;

public class AttrString {
  private String value;

  private AttrString(String value) {
    this.value = value;
  }

  public void set(String value) {
    this.value = value;
  }

  public String get() {
    return value;
  }

  public static class Builder {
    private String defaultValue;

    public Builder defaultValue(String value) {
      defaultValue = value;
      return this;
    }

    // TODO: add validation/ reporting?
    public Builder label(String label) {
      return this;
    }

    public AttrString build() {
      return new AttrString(defaultValue);
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
