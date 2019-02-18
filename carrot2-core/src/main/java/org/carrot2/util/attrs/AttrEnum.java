package org.carrot2.util.attrs;

import java.util.Locale;

public class AttrEnum<T extends Enum<T>> {
  private T value;
  private Class<T> clazz;

  AttrEnum(Class<T> clazz, T value) {
    if (!clazz.isEnum()) {
      throw new RuntimeException(String.format(Locale.ROOT,
          "Expected an enum class: %s",  clazz.getSimpleName()));
    }
    this.value = value;
    this.clazz = clazz;
  }

  public void set(T value) {
    this.value = value;
  }

  void set(String name) {
    set(name == null ? null : Enum.valueOf(clazz, name));
  }

  public T get() {
    return value;
  }

  public static class Builder<T extends Enum<T>> {
    private Class<T> clazz;
    private T defaultValue;

    public Builder(Class<T> clazz) {
      this.clazz = clazz;
    }

    public Builder<T> defaultValue(T value) {
      defaultValue = value;
      return this;
    }

    // TODO: add label storage?
    public Builder<T> label(String label) {
      return this;
    }

    public AttrEnum<T> build() {
      return new AttrEnum<>(clazz, defaultValue);
    }
  }

  public static <T extends Enum<T>> Builder<T> builder(Class<T> clazz) {
    return new Builder<>(clazz);
  }
}
