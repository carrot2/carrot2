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

import org.assertj.core.api.Assertions;
import org.carrot2.TestBase;
import org.junit.Test;

public class AttrDoubleTest extends TestBase {
  @Test
  public void testToFromMap() {
    class Clazz extends AttrComposite {
      public AttrDouble defValue =
          attributes.register("defValue", AttrDouble.builder().defaultValue(36.6));
      public AttrDouble nullValue =
          attributes.register("nullValue", AttrDouble.builder().defaultValue(null));
      public AttrDouble otherValue =
          attributes.register("otherValue", AttrDouble.builder().defaultValue(null));
      public AttrDouble nanValue =
          attributes.register("nanValue", AttrDouble.builder().defaultValue(Double.NaN));
      public AttrDouble infValue =
          attributes.register(
              "infValue", AttrDouble.builder().defaultValue(Double.POSITIVE_INFINITY));
    }

    AliasMapper mapper = new AliasMapper();
    mapper.alias("clazz", Clazz.class, () -> new Clazz());

    Clazz ob = new Clazz();
    ob.otherValue.set(36.6);

    Assertions.assertThat(Attrs.toMap(ob, mapper::toName))
        .containsEntry("defValue", 36.6d)
        .containsEntry("nullValue", null)
        .containsEntry("otherValue", 36.6d)
        .containsEntry("nanValue", Double.NaN)
        .containsEntry("infValue", Double.POSITIVE_INFINITY);

    Clazz clazz = Attrs.fromMap(Clazz.class, Attrs.toMap(ob, mapper::toName), mapper::fromName);
    Assertions.assertThat(clazz.defValue.get()).isEqualTo(36.6d);
    Assertions.assertThat(clazz.nullValue.get()).isNull();
    Assertions.assertThat(clazz.otherValue.get()).isEqualTo(36.6d);
    Assertions.assertThat(clazz.nanValue.get()).isNaN();
    Assertions.assertThat(clazz.infValue.get()).isEqualTo(Double.POSITIVE_INFINITY);
  }

  @Test
  public void testConstraintValidation() {
    AttrDouble attr = AttrDouble.builder().min(10).max(20).defaultValue(null);

    attr.set(10d);
    attr.set(20d);
    Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> attr.set(9d));
    Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> attr.set(21d));
  }
}
