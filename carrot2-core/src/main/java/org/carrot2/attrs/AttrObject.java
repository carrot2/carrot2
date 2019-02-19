package org.carrot2.attrs;

import java.util.function.Consumer;

public class AttrObject<T extends AcceptingVisitor> {
  private T value;
  private Class<T> clazz;

  AttrObject(Class<T> clazz, T value) {
    this.value = value;
    this.clazz = clazz;
  }

  public void set(T value) {
    set(value, (v) -> {});
  }

  public <E extends T> E set(E value, Consumer<E> closure) {
    this.value = value;
    closure.accept(value);
    return value;
  }

  T castSet(Object t) {
    set(clazz.cast(t));
    return get();
  }

  public T get() {
    return value;
  }

  public static class Builder<T extends AcceptingVisitor> {
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

    public AttrObject<T> build() {
      return new AttrObject<>(clazz, defaultValue);
    }
  }

  public static <T extends AcceptingVisitor> Builder<T> builder(Class<T> clazz) {
    return new Builder<>(clazz);
  }
}
