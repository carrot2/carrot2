package org.carrot2.attrs;

import java.util.function.Consumer;

public class AttrBoolean extends Attr<Boolean> {
  private AttrBoolean(Boolean value, Consumer<Boolean> constraint, String label) {
    super(value, label, constraint);
  }

  public static class Builder extends Attr.BuilderScaffold<Boolean> {
    @Override
    public AttrBoolean.Builder label(String label) {
      super.label(label);
      return this;
    }

    public AttrBoolean defaultValue(Boolean defaultValue) {
      return new AttrBoolean(defaultValue, getConstraint(), label);
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
