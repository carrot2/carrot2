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

public class AttrIntegerTest extends TestBase {
  @Test
  public void testToFromMap() {
    class Clazz extends AttrComposite {
      public AttrInteger defValue =
          attributes.register("defValue", AttrInteger.builder().defaultValue(42));
      public AttrInteger nullValue =
          attributes.register("nullValue", AttrInteger.builder().defaultValue(null));
      public AttrInteger otherValue =
          attributes.register("otherValue", AttrInteger.builder().defaultValue(null));
    }

    AliasMapper mapper = new AliasMapper();
    mapper.alias("clazz", Clazz.class, () -> new Clazz());

    Clazz ob = new Clazz();
    ob.otherValue.set(24);

    Assertions.assertThat(Attrs.toMap(ob, mapper::toName))
        .containsEntry("defValue", 42)
        .containsEntry("nullValue", null)
        .containsEntry("otherValue", 24);

    Clazz clazz = Attrs.fromMap(Clazz.class, Attrs.toMap(ob, mapper::toName), mapper::fromName);
    Assertions.assertThat(clazz.defValue.get()).isEqualTo(42);
    Assertions.assertThat(clazz.nullValue.get()).isNull();
    Assertions.assertThat(clazz.otherValue.get()).isEqualTo(24);
  }

  @Test
  public void testConstraintValidation() {
    AttrInteger attr = AttrInteger.builder().min(10).max(20).defaultValue(null);

    attr.set(10);
    attr.set(20);
    Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> attr.set(9));
    Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> attr.set(21));
  }
}
