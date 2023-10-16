/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2023, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.attrs;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AttrObject<T extends AcceptingVisitor> extends Attr<T> {
  private Class<T> clazz;
  private Supplier<T> getter;
  private Consumer<T> setter;
  private Supplier<? extends T> newInstance;

  AttrObject(
      Class<T> clazz,
      T defaultValue,
      String label,
      List<? extends Constraint<? super T>> constraint,
      Supplier<? extends T> newInstance,
      Supplier<T> getter,
      Consumer<T> setter) {
    super(null, label, constraint);
    this.clazz = clazz;

    this.setter = setter != null ? setter : super::set;
    this.getter = getter != null ? getter : super::get;
    this.newInstance = newInstance;

    set(defaultValue);
  }

  public void set(T value) {
    super.set(value);
    setter.accept(value);
  }

  public <E extends T> E set(E value, Consumer<E> closure) {
    set(value);
    closure.accept(value);
    return value;
  }

  public T get() {
    return getter.get();
  }

  public Class<T> getInterfaceClass() {
    return clazz;
  }

  public boolean isDefaultClass(Object value) {
    Objects.requireNonNull(value);
    T def = newDefaultValue();
    return def != null
        && Objects.equals(def.getClass(), value.getClass())
        && Objects.equals(clazz, value.getClass());
  }

  public T newDefaultValue() {
    return this.newInstance.get();
  }

  public static class Builder<T extends AcceptingVisitor> extends Attr.BuilderScaffold<T> {
    private Class<T> clazz;
    private Supplier<T> getter;
    private Consumer<T> setter;

    public Builder(Class<T> clazz) {
      this.clazz = clazz;
    }

    public Builder<T> getset(Supplier<T> getter, Consumer<T> setter) {
      this.setter = Objects.requireNonNull(setter);
      this.getter = Objects.requireNonNull(getter);
      return this;
    }

    @Override
    public Builder<T> label(String label) {
      super.label(label);
      return this;
    }

    public AttrObject<T> defaultValue(Supplier<? extends T> newInstance) {
      Objects.requireNonNull(newInstance, "New instance supplier must not be null.");
      return defaultValue(newInstance.get(), newInstance);
    }

    public AttrObject<T> defaultValue(T defaultValue, Supplier<? extends T> newInstance) {
      Objects.requireNonNull(newInstance, "New instance supplier must not be null.");
      return new AttrObject<T>(
          clazz, defaultValue, label, getConstraint(), newInstance, getter, setter);
    }
  }

  public static <T extends AcceptingVisitor> Builder<T> builder(Class<T> clazz) {
    return new Builder<>(clazz);
  }
}
