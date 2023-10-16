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

public class AttrString extends Attr<String> {
  private AttrString(
      String value, List<? extends Constraint<? super String>> constraint, String label) {
    super(value, label, constraint);
  }

  public static class Builder extends BuilderScaffold<String> {
    @Override
    public Builder label(String label) {
      super.label(label);
      return this;
    }

    public AttrString defaultValue(String defaultValue) {
      return new AttrString(defaultValue, getConstraint(), label);
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
