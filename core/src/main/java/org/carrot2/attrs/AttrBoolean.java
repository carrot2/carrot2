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

public class AttrBoolean extends Attr<Boolean> {
  private AttrBoolean(
      Boolean value, List<? extends Constraint<? super Boolean>> constraints, String label) {
    super(value, label, constraints);
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
