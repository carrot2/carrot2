package org.carrot2.attrs;

import java.util.function.Consumer;

public class AttrString extends Attr<String> {
  private AttrString(String value, Consumer<String> constraint, String label) {
    super(value, label, constraint);
  }

  public static class Builder extends BuilderScaffold<String> {
    @Override
    public Builder label(String label) {
      super.label(label);
      return this;
    }

    public AttrString defaultValue(String defaultValue) {
      return new AttrString(defaultValue, getConstraint(), label);
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
