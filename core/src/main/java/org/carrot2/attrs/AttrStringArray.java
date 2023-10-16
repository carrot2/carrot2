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
import java.util.stream.Stream;

public class AttrStringArray extends Attr<String[]> {
  private AttrStringArray(
      String[] value, List<? extends Constraint<? super String[]>> constraint, String label) {
    super(value, label, constraint);
  }

  @Override
  public void set(String... value) {
    super.set(value);
  }

  public void set(List<String> values) {
    super.set(values.toArray(String[]::new));
  }

  /**
   * @return Return true if the value of this attribute is {@code null} or an empty array.
   * @since 4.1.0
   */
  public boolean isEmpty() {
    return get() == null || get().length == 0;
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
