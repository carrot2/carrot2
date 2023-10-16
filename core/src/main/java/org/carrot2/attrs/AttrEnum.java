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

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

public class AttrEnum<T extends Enum<T>> extends Attr<T> {
  private Class<T> clazz;

  AttrEnum(
      Class<T> clazz, T value, List<? extends Constraint<? super T>> constraint, String label) {
    super(value, label, constraint);

    if (!clazz.isEnum()) {
      throw new RuntimeException(
          String.format(Locale.ROOT, "Expected an enum class: %s", clazz.getSimpleName()));
    }
    this.clazz = clazz;
  }

  public Class<T> enumClass() {
    return clazz;
  }

  public static class Builder<T extends Enum<T>> extends BuilderScaffold<T> {
    private Class<T> clazz;

    public Builder(Class<T> clazz) {
      this.clazz = clazz;

      addConstraint(Constraint.named("value in " + EnumSet.allOf(clazz), (v) -> {}));
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
