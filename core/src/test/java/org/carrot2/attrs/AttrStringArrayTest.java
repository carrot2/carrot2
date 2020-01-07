/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2020, Dawid Weiss, Stanisław Osiński.
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

public class AttrStringArrayTest extends TestBase {
  @Test
  public void testToFromMap() {
    class Clazz extends AttrComposite {
      public AttrStringArray defValue =
          attributes.register("defValue", AttrStringArray.builder().defaultValue("foo", "bar"));
      public AttrStringArray nullValue =
          attributes.register("nullValue", AttrStringArray.builder().defaultValue(null));
      public AttrStringArray otherValue =
          attributes.register("otherValue", AttrStringArray.builder().defaultValue(null));
    }

    AliasMapper mapper = new AliasMapper();
    mapper.alias("clazz", Clazz.class, () -> new Clazz());

    Clazz ob = new Clazz();
    ob.otherValue.set("bar", "baz");

    Assertions.assertThat(Attrs.toMap(ob, mapper::toName))
        .containsEntry("defValue", new String[] {"foo", "bar"})
        .containsEntry("nullValue", null)
        .containsEntry("otherValue", new String[] {"bar", "baz"});

    Clazz clazz = Attrs.fromMap(Clazz.class, Attrs.toMap(ob, mapper::toName), mapper::fromName);
    Assertions.assertThat(clazz.defValue.get()).isEqualTo(new String[] {"foo", "bar"});
    Assertions.assertThat(clazz.nullValue.get()).isNull();
    Assertions.assertThat(clazz.otherValue.get()).isEqualTo(new String[] {"bar", "baz"});
  }
}
