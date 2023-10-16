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
package org.carrot2.infra.docattrs;

import java.time.DayOfWeek;
import org.assertj.core.api.Assertions;
import org.carrot2.attrs.AttrBoolean;
import org.carrot2.attrs.AttrComposite;
import org.carrot2.attrs.AttrDouble;
import org.carrot2.attrs.AttrEnum;
import org.carrot2.attrs.AttrInteger;
import org.carrot2.attrs.AttrObject;
import org.carrot2.attrs.AttrString;
import org.carrot2.attrs.JvmNameMapper;
import org.junit.Test;

public class TestClassInfoCollector {
  @Test
  public void testBooleanAttr() {
    @SuppressWarnings("unused")
    class Clazz extends AttrComposite {
      AttrBoolean attrTrue =
          attributes.register("attrTrue", AttrBoolean.builder().defaultValue(true));
      AttrBoolean attrFalse =
          attributes.register("attrFalse", AttrBoolean.builder().defaultValue(false));
      AttrBoolean attrNull =
          attributes.register("attrNull", AttrBoolean.builder().defaultValue(null));
    }

    ClassInfoCollector collector = new ClassInfoCollector(JvmNameMapper.INSTANCE);
    ClassInfo classInfo = collector.collect(new Clazz());
    Assertions.assertThat(classInfo.attributes)
        .hasEntrySatisfying("attrTrue", info -> Assertions.assertThat(info.value).isEqualTo(true))
        .hasEntrySatisfying("attrFalse", info -> Assertions.assertThat(info.value).isEqualTo(false))
        .hasEntrySatisfying("attrNull", info -> Assertions.assertThat(info.value).isNull());
  }

  @Test
  public void testNumericAttr() {
    @SuppressWarnings("unused")
    class Clazz extends AttrComposite {
      AttrInteger attrInteger =
          attributes.register("attrInteger", AttrInteger.builder().defaultValue(42));
      AttrDouble attrDouble =
          attributes.register("attrDouble", AttrDouble.builder().defaultValue(42.5));
    }

    ClassInfoCollector collector = new ClassInfoCollector(JvmNameMapper.INSTANCE);
    ClassInfo classInfo = collector.collect(new Clazz());
    Assertions.assertThat(classInfo.attributes)
        .hasEntrySatisfying("attrInteger", info -> Assertions.assertThat(info.value).isEqualTo(42))
        .hasEntrySatisfying(
            "attrDouble", info -> Assertions.assertThat(info.value).isEqualTo(42.5));
  }

  @Test
  public void testEnumAttr() {
    @SuppressWarnings("unused")
    class Clazz extends AttrComposite {
      AttrEnum<DayOfWeek> attrEnum =
          attributes.register(
              "attrEnum", AttrEnum.builder(DayOfWeek.class).defaultValue(DayOfWeek.FRIDAY));
      AttrEnum<DayOfWeek> attrEnumNull =
          attributes.register("attrEnumNull", AttrEnum.builder(DayOfWeek.class).defaultValue(null));
    }

    ClassInfoCollector collector = new ClassInfoCollector(JvmNameMapper.INSTANCE);
    ClassInfo classInfo = collector.collect(new Clazz());
    Assertions.assertThat(classInfo.attributes)
        .hasEntrySatisfying(
            "attrEnum",
            info -> Assertions.assertThat(info.value).isEqualTo(DayOfWeek.FRIDAY.toString()))
        .hasEntrySatisfying("attrEnumNull", info -> Assertions.assertThat(info.value).isNull());
  }

  @Test
  public void testStringAttr() {
    @SuppressWarnings("unused")
    class Clazz extends AttrComposite {
      AttrString attr = attributes.register("attr", AttrString.builder().defaultValue("foo"));
      AttrString attrNull =
          attributes.register("attrNull", AttrString.builder().defaultValue(null));
    }

    ClassInfoCollector collector = new ClassInfoCollector(JvmNameMapper.INSTANCE);
    ClassInfo classInfo = collector.collect(new Clazz());
    Assertions.assertThat(classInfo.attributes)
        .hasEntrySatisfying("attr", info -> Assertions.assertThat(info.value).isEqualTo("foo"))
        .hasEntrySatisfying("attrNull", info -> Assertions.assertThat(info.value).isNull());
  }

  @Test
  public void testObjectAttr() {
    class Foo extends AttrComposite {}

    @SuppressWarnings("unused")
    class Clazz extends AttrComposite {
      AttrObject<Foo> attr =
          attributes.register("attr", AttrObject.builder(Foo.class).defaultValue(Foo::new));
      AttrObject<Foo> attrNull =
          attributes.register("attrNull", AttrObject.builder(Foo.class).defaultValue(() -> null));
    }

    ClassInfoCollector collector = new ClassInfoCollector(JvmNameMapper.INSTANCE);
    ClassInfo classInfo = collector.collect(new Clazz());
    Assertions.assertThat(classInfo.attributes)
        .hasEntrySatisfying(
            "attr",
            info ->
                Assertions.assertThat(info.value)
                    .isEqualTo(JvmNameMapper.INSTANCE.toName(new Foo())))
        .hasEntrySatisfying("attrNull", info -> Assertions.assertThat(info.value).isNull());
  }
}
