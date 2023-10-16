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

public class AttrObjectArray<T extends AcceptingVisitor> extends Attr<List<T>> {
  private Class<T> clazz;
  private Supplier<List<T>> getter;
  private Consumer<List<T>> setter;
  private Supplier<? extends T> newEntryInstance;

  AttrObjectArray(
      Class<T> clazz,
      List<T> defaultValue,
      String label,
      List<? extends Constraint<? super List<T>>> constraint,
      Supplier<? extends T> newEntryInstance,
      Supplier<List<T>> getter,
      Consumer<List<T>> setter) {
    super(null, label, constraint);
    this.clazz = clazz;

    this.setter = setter != null ? setter : super::set;
    this.getter = getter != null ? getter : super::get;
    this.newEntryInstance = newEntryInstance;

    set(defaultValue);
  }

  public void set(List<T> value) {
    super.set(value);
    setter.accept(value);
  }

  public List<T> get() {
    return getter.get();
  }

  public Class<T> getInterfaceClass() {
    return clazz;
  }

  public boolean isDefaultClass(Object value) {
    Objects.requireNonNull(value);
    T def = newDefaultEntryValue();
    return def != null && Objects.equals(def.getClass(), value.getClass());
  }

  public T newDefaultEntryValue() {
    return this.newEntryInstance.get();
  }

  public static class Builder<T extends AcceptingVisitor> extends BuilderScaffold<List<T>> {
    private final Supplier<? extends T> defaultEntryValue;
    private Class<T> clazz;
    private Supplier<List<T>> getter;
    private Consumer<List<T>> setter;

    public Builder(Class<T> clazz, Supplier<? extends T> defaultEntryValue) {
      this.clazz = clazz;
      this.defaultEntryValue = defaultEntryValue;
    }

    public Builder<T> getset(Supplier<List<T>> getter, Consumer<List<T>> setter) {
      this.setter = Objects.requireNonNull(setter);
      this.getter = Objects.requireNonNull(getter);
      return this;
    }

    @Override
    public Builder<T> label(String label) {
      super.label(label);
      return this;
    }

    public AttrObjectArray<T> defaultValue(List<T> defaultValue) {
      return new AttrObjectArray<T>(
          clazz, defaultValue, label, getConstraint(), defaultEntryValue, getter, setter);
    }
  }

  public static <T extends AcceptingVisitor> Builder<T> builder(
      Class<T> entryClazz, Supplier<? extends T> defaultEntryValue) {
    return new Builder<>(entryClazz, defaultEntryValue);
  }
}
