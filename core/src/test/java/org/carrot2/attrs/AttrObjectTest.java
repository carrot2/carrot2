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

import java.util.Map;
import org.assertj.core.api.Assertions;
import org.carrot2.TestBase;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class AttrObjectTest extends TestBase {
  interface Interface extends AcceptingVisitor {}

  private class InterfaceImpl1 extends AttrComposite implements Interface {}

  private class InterfaceImpl2 extends AttrComposite implements Interface {}

  private class InterfaceImpl3 extends InterfaceImpl2 {
    public AttrString attr = attributes.register("attr", AttrString.builder().defaultValue("foo"));
  }

  @Test
  public void testToFromMap() {
    class Clazz extends AttrComposite {
      public AttrObject<Interface> nullValue =
          attributes.register(
              "nullValue", AttrObject.builder(Interface.class).defaultValue(() -> null));
      public AttrObject<Interface> defValue =
          attributes.register(
              "defValue",
              AttrObject.builder(Interface.class).defaultValue(() -> new InterfaceImpl1()));
      public AttrObject<Interface> otherValue =
          attributes.register(
              "otherValue", AttrObject.builder(Interface.class).defaultValue(() -> null));
    }

    AliasMapper mapper = new AliasMapper();
    mapper.alias("impl1", InterfaceImpl1.class, () -> new InterfaceImpl1());
    mapper.alias("impl2", InterfaceImpl2.class, () -> new InterfaceImpl2());
    mapper.alias("impl3", InterfaceImpl3.class, () -> new InterfaceImpl3());
    mapper.alias("clazz", Clazz.class, () -> new Clazz());

    Clazz ob = new Clazz();
    InterfaceImpl3 otherValue = new InterfaceImpl3();
    otherValue.attr.set("bar");
    ob.otherValue.set(otherValue);

    Map<String, Object> map = Attrs.toMap(ob, mapper::toName);
    Assertions.assertThat(map)
        .containsOnlyKeys("defValue", "nullValue", "otherValue", Attrs.KEY_TYPE);

    Assertions.assertThat((Map<String, Object>) map.get("defValue"))
        .containsOnlyKeys(Attrs.KEY_TYPE)
        .containsEntry(Attrs.KEY_TYPE, "impl1");

    Assertions.assertThat(map.get("nullValue")).isNull();

    Assertions.assertThat((Map<String, Object>) map.get("otherValue"))
        .containsOnlyKeys(Attrs.KEY_TYPE, "attr")
        .containsEntry(Attrs.KEY_TYPE, "impl3");

    Clazz clazz = Attrs.fromMap(Clazz.class, Attrs.toMap(ob, mapper::toName), mapper::fromName);
    Assertions.assertThat(clazz.nullValue.get()).isNull();
    Assertions.assertThat(clazz.defValue.get())
        .isExactlyInstanceOf(InterfaceImpl1.class)
        .isNotSameAs(ob.defValue.get());
    Assertions.assertThat(clazz.otherValue.get())
        .isExactlyInstanceOf(InterfaceImpl3.class)
        .isNotSameAs(ob.otherValue.get());

    Assertions.assertThat(((InterfaceImpl3) clazz.otherValue.get()).attr.get()).isEqualTo("bar");
  }

  @Test
  public void testToFromMapExplicitFields() {
    class Clazz extends AttrComposite {
      public Interface nullValue;
      public Interface defValue;
      public Interface otherValue;

      {
        attributes.register(
            "nullValue",
            AttrObject.builder(Interface.class)
                .getset(() -> nullValue, (v) -> nullValue = v)
                .defaultValue(() -> null));
        attributes.register(
            "defValue",
            AttrObject.builder(Interface.class)
                .getset(() -> defValue, (v) -> defValue = v)
                .defaultValue(() -> new InterfaceImpl1()));
        attributes.register(
            "otherValue",
            AttrObject.builder(Interface.class)
                .getset(() -> otherValue, (v) -> otherValue = v)
                .defaultValue(() -> null));
      }
    }

    AliasMapper mapper = new AliasMapper();
    mapper.alias("impl1", InterfaceImpl1.class, () -> new InterfaceImpl1());
    mapper.alias("impl2", InterfaceImpl2.class, () -> new InterfaceImpl2());
    mapper.alias("impl3", InterfaceImpl3.class, () -> new InterfaceImpl3());
    mapper.alias("clazz", Clazz.class, () -> new Clazz());

    Clazz ob = new Clazz();
    InterfaceImpl3 otherValue = new InterfaceImpl3();
    otherValue.attr.set("bar");
    ob.otherValue = otherValue;

    Map<String, Object> map = Attrs.toMap(ob, mapper::toName);
    Assertions.assertThat(map)
        .containsOnlyKeys("defValue", "nullValue", "otherValue", Attrs.KEY_TYPE);

    Assertions.assertThat((Map<String, Object>) map.get("defValue"))
        .containsOnlyKeys(Attrs.KEY_TYPE)
        .containsEntry(Attrs.KEY_TYPE, "impl1");

    Assertions.assertThat(map.get("nullValue")).isNull();

    Assertions.assertThat((Map<String, Object>) map.get("otherValue"))
        .containsOnlyKeys(Attrs.KEY_TYPE, "attr")
        .containsEntry(Attrs.KEY_TYPE, "impl3");

    Clazz clazz = Attrs.fromMap(Clazz.class, Attrs.toMap(ob, mapper::toName), mapper::fromName);
    Assertions.assertThat(clazz.nullValue).isNull();
    Assertions.assertThat(clazz.defValue)
        .isExactlyInstanceOf(InterfaceImpl1.class)
        .isNotSameAs(ob.defValue);
    Assertions.assertThat(clazz.otherValue)
        .isExactlyInstanceOf(InterfaceImpl3.class)
        .isNotSameAs(ob.otherValue);

    Assertions.assertThat(((InterfaceImpl3) clazz.otherValue).attr.get()).isEqualTo("bar");
  }

  @Test
  public void testImplicitType() {
    class Clazz extends AttrComposite {
      public InterfaceImpl2 sameClass;
      public InterfaceImpl2 subClass;
      public InterfaceImpl2 nullDefault;

      {
        attributes.register(
            "sameClass",
            AttrObject.builder(InterfaceImpl2.class)
                .getset(() -> sameClass, (v) -> sameClass = v)
                .defaultValue(() -> new InterfaceImpl2()));
        attributes.register(
            "subClass",
            AttrObject.builder(InterfaceImpl2.class)
                .getset(() -> subClass, (v) -> subClass = v)
                .defaultValue(() -> new InterfaceImpl3()));
        attributes.register(
            "nullDefault",
            AttrObject.builder(InterfaceImpl2.class)
                .getset(() -> nullDefault, (v) -> nullDefault = v)
                .defaultValue(() -> null));
      }
    }

    AliasMapper mapper = new AliasMapper();
    mapper.alias("impl2", InterfaceImpl2.class, () -> new InterfaceImpl2());
    mapper.alias("impl3", InterfaceImpl3.class, () -> new InterfaceImpl3());
    mapper.alias("clazz", Clazz.class, () -> new Clazz());

    Clazz ob = new Clazz();
    ob.nullDefault = new InterfaceImpl2();
    ((InterfaceImpl3) ob.subClass).attr.set("baz");

    Map<String, Object> map = Attrs.toMap(ob, mapper::toName);
    Assertions.assertThat(map)
        .containsOnlyKeys("sameClass", "subClass", "nullDefault", Attrs.KEY_TYPE);

    Assertions.assertThat((Map<String, Object>) map.get("sameClass")).isEmpty();

    Assertions.assertThat((Map<String, Object>) map.get("subClass"))
        .containsOnlyKeys(Attrs.KEY_TYPE, "attr")
        .containsEntry(Attrs.KEY_TYPE, "impl3");

    Assertions.assertThat((Map<String, Object>) map.get("nullDefault"))
        .containsOnlyKeys(Attrs.KEY_TYPE)
        .containsEntry(Attrs.KEY_TYPE, "impl2");

    Clazz clazz = Attrs.fromMap(Clazz.class, Attrs.toMap(ob, mapper::toName), mapper::fromName);
    Assertions.assertThat(clazz.sameClass)
        .isExactlyInstanceOf(InterfaceImpl2.class)
        .isNotSameAs(ob.sameClass);
    Assertions.assertThat(clazz.subClass)
        .isExactlyInstanceOf(InterfaceImpl3.class)
        .isNotSameAs(ob.sameClass);
    Assertions.assertThat(clazz.nullDefault).isNotNull().isExactlyInstanceOf(InterfaceImpl2.class);

    Assertions.assertThat(((InterfaceImpl3) clazz.subClass).attr.get()).isEqualTo("baz");
  }
}
