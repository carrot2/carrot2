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

public class AttrBooleanTest extends TestBase {
  @Test
  public void testToFromMap() {
    class Clazz extends AttrComposite {
      public AttrBoolean defValue =
          attributes.register("defValue", AttrBoolean.builder().defaultValue(true));
      public AttrBoolean nullValue =
          attributes.register("nullValue", AttrBoolean.builder().defaultValue(null));
      public AttrBoolean otherValue =
          attributes.register("otherValue", AttrBoolean.builder().defaultValue(null));
    }

    AliasMapper mapper = new AliasMapper();
    mapper.alias("clazz", Clazz.class, () -> new Clazz());

    Clazz ob = new Clazz();
    ob.otherValue.set(false);

    Assertions.assertThat(Attrs.toMap(ob, mapper::toName))
        .containsEntry("defValue", true)
        .containsEntry("nullValue", null)
        .containsEntry("otherValue", false);

    Clazz clazz = Attrs.fromMap(Clazz.class, Attrs.toMap(ob, mapper::toName), mapper::fromName);
    Assertions.assertThat(clazz.defValue.get()).isEqualTo(true);
    Assertions.assertThat(clazz.nullValue.get()).isNull();
    Assertions.assertThat(clazz.otherValue.get()).isEqualTo(false);
  }
}
