package org.carrot2.clustering;

import org.carrot2.attrs.AttrString;

public class CommonAttributes {
  public static AttrString queryHint() {
    return AttrString.builder()
        .label("Query hint")
        .defaultValue(null);
  }
}
