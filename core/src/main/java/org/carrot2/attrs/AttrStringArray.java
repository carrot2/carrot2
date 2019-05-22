package org.carrot2.attrs;

import java.util.function.Consumer;
import java.util.stream.Stream;

public class AttrStringArray extends Attr<String[]> {
  private AttrStringArray(String[] value, Consumer<String[]> constraint, String label) {
    super(value, label, constraint);
  }

  public void set(String value, String... values) {
    super.set(Stream.concat(Stream.of(value), Stream.of(values)).toArray(String[]::new));
  }

  public static class Builder extends BuilderScaffold<String[]> {
    public AttrStringArray defaultValue(String value, String... values) {
      return defaultValue(
          Stream.concat(Stream.of(value), Stream.of(values)).toArray(String[]::new));
    }

    public AttrStringArray defaultValue(String[] values) {
      return new AttrStringArray(values, getConstraint(), label);
    }

    @Override
    public Builder label(String label) {
      super.label(label);
      return this;
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
