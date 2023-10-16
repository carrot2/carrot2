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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.carrot2.TestBase;
import org.junit.Test;

public class AttrObjectArrayTest extends TestBase {
  @Test
  public void testToFromMap() {
    class Entry extends AttrComposite {
      AttrString attr = attributes.register("attr", AttrString.builder().defaultValue(null));

      Entry() {}

      Entry(String value) {
        attr.set(value);
      }
    }

    class Clazz extends AttrComposite {
      public AttrObjectArray<Entry> defValue =
          attributes.register(
              "defValue",
              AttrObjectArray.builder(Entry.class, () -> new Entry())
                  .defaultValue(Arrays.asList(new Entry("foo"), new Entry("bar"))));

      public AttrObjectArray<Entry> nullValue =
          attributes.register(
              "nullValue",
              AttrObjectArray.builder(Entry.class, () -> new Entry()).defaultValue(null));

      public AttrObjectArray<Entry> otherValue =
          attributes.register(
              "otherValue",
              AttrObjectArray.builder(Entry.class, () -> new Entry()).defaultValue(null));
    }

    AliasMapper mapper = new AliasMapper();
    mapper.alias("clazz", Clazz.class, () -> new Clazz());
    mapper.alias("entry", Entry.class, () -> new Entry());

    Clazz ob = new Clazz();
    ob.otherValue.set(Collections.singletonList(new Entry("baz")));

    Map<String, Object> foo = new HashMap<>();
    foo.put("attr", "foo");
    Map<String, Object> bar = new HashMap<>();
    bar.put("attr", "bar");
    Map<String, Object> baz = new HashMap<>();
    baz.put("attr", "baz");

    Assertions.assertThat(Attrs.toMap(ob, mapper::toName))
        .containsEntry("defValue", new Object[] {foo, bar})
        .containsEntry("nullValue", null)
        .containsEntry("otherValue", new Object[] {baz});

    Clazz clazz = Attrs.fromMap(Clazz.class, Attrs.toMap(ob, mapper::toName), mapper::fromName);
    Assertions.assertThat(clazz.defValue.get().stream())
        .extracting(e -> e.attr.get())
        .containsExactly("foo", "bar");

    Assertions.assertThat(clazz.nullValue.get()).isNull();
    Assertions.assertThat(clazz.otherValue.get().stream())
        .extracting(e -> e.attr.get())
        .containsExactly("baz");
  }
}
