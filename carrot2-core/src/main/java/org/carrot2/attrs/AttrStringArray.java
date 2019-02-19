package org.carrot2.attrs;

public class AttrStringArray {
  private String [] value;

  private AttrStringArray(String [] value) {
    this.value = value;
  }

  public void set(String... value) {
    this.value = value;
  }

  public String [] get() {
    return value;
  }

  public static class Builder {
    private String [] defaultValue;

    public Builder defaultValue(String... value) {
      defaultValue = value;
      return this;
    }

    public AttrStringArray build() {
      return new AttrStringArray(defaultValue == null ? null : defaultValue.clone());
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
