package org.carrot2.attrs;

import java.util.Locale;
import java.util.function.Consumer;

public class AttrEnum<T extends Enum<T>> extends Attr<T> {
  private Class<T> clazz;

  AttrEnum(Class<T> clazz, T value, Consumer<T> constraint, String label) {
    super(value, label, constraint);

    if (!clazz.isEnum()) {
      throw new RuntimeException(String.format(Locale.ROOT,
          "Expected an enum class: %s",  clazz.getSimpleName()));
    }
    this.clazz = clazz;
  }

  public void set(String name) {
    set(name == null ? null : Enum.valueOf(clazz, name));
  }

  public static class Builder<T extends Enum<T>> extends BuilderScaffold<T> {
    private Class<T> clazz;

    public Builder(Class<T> clazz) {
      this.clazz = clazz;
    }

    public Builder<T> label(String label) {
      super.label(label);
      return this;
    }

    public AttrEnum<T> defaultValue(T defaultValue) {
      return new AttrEnum<>(clazz, defaultValue, getConstraint(), label);
    }
  }

  public static <T extends Enum<T>> Builder<T> builder(Class<T> clazz) {
    return new Builder<>(clazz);
  }
}
