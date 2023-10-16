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

import java.util.*;
import java.util.function.Function;
import org.assertj.core.api.Assertions;
import org.carrot2.TestBase;
import org.junit.Test;

public class AttrsTest extends TestBase {
  @Test
  public void testClassNameMapper() {
    class Clazz extends AttrComposite {}

    AliasMapper mapper = new AliasMapper();
    mapper.alias("clazz", Clazz.class, () -> new Clazz());

    Clazz ob = new Clazz();

    Assertions.assertThat(Attrs.toMap(ob, mapper::toName)).containsEntry(Attrs.KEY_TYPE, "clazz");

    Assertions.assertThat(
            Attrs.fromMap(Clazz.class, Attrs.toMap(ob, mapper::toName), mapper::fromName))
        .isNotNull();
  }

  interface AdHocDict extends AcceptingVisitor {}

  static class Entry implements AcceptingVisitor {
    String match;
    String[] tokens;

    @Override
    public void accept(AttrVisitor visitor) {
      AttrString a1 = AttrString.builder().defaultValue(match);
      visitor.visit("match", a1);
      this.match = a1.get();

      AttrStringArray a2 = AttrStringArray.builder().defaultValue(tokens);
      visitor.visit("tokens", a2);
      tokens = a2.get();
    }
  }

  static class AdHocDict1 extends AttrComposite implements AdHocDict {
    List<Entry> entries;

    public AdHocDict1() {}

    {
      attributes.register(
          "entries",
          AttrObjectArray.builder(Entry.class, Entry::new)
              .getset(() -> entries, (list) -> entries = list)
              .defaultValue(null));
    }
  }

  @Test
  public void testCustomDictionary() {

    class Clazz extends AttrComposite {
      AdHocDict adHocDict = new AdHocDict1();

      {
        attributes.register(
            "adhoc",
            AttrObject.builder(AdHocDict.class)
                .getset(() -> adHocDict, (v) -> adHocDict = v)
                .defaultValue(AdHocDict1::new));
      }
    }

    Entry entry = new Entry();
    entry.match = "e";
    entry.tokens = new String[] {"foo", "bar"};

    Clazz ob = new Clazz();
    var adHocDict = new AdHocDict1();
    adHocDict.entries = Arrays.asList(entry, entry);
    ob.adHocDict = adHocDict;
    String json = Attrs.toJson(ob, JvmNameMapper.INSTANCE);
    System.out.println(json);

    var asMap = Attrs.extract(ob, JvmNameMapper.INSTANCE::toName);
    Clazz ob2 = Attrs.populate(new Clazz(), asMap, JvmNameMapper.INSTANCE::fromName);
    System.out.println(Attrs.toJson(ob2, JvmNameMapper.INSTANCE));
  }

  private enum EnumClazz {
    FOO,
    BAR;
  }

  @Test
  public void testFromMapAttrWithInvalidValue() {
    class Clazz extends AttrComposite {
      AttrBoolean attrBoolean =
          attributes.register("attrBoolean", AttrBoolean.builder().defaultValue(null));
      AttrInteger attrInteger =
          attributes.register("attrInteger", AttrInteger.builder().defaultValue(null));
      AttrDouble attrDouble =
          attributes.register("attrDouble", AttrDouble.builder().defaultValue(null));
      AttrString attrString =
          attributes.register("attrString", AttrString.builder().defaultValue(null));
      AttrEnum<EnumClazz> attrEnum =
          attributes.register("attrEnum", AttrEnum.builder(EnumClazz.class).defaultValue(null));
      AttrObject<Clazz> attrObject =
          attributes.register(
              "attrObject", AttrObject.builder(Clazz.class).defaultValue(null, () -> new Clazz()));
      AttrObjectArray<Clazz> attrObjectArray =
          attributes.register(
              "attrObjectArray",
              AttrObjectArray.builder(Clazz.class, () -> new Clazz()).defaultValue(null));
    }

    AliasMapper mapper = new AliasMapper();
    mapper.alias("clazz", Clazz.class, () -> new Clazz());

    Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> {
              Map<String, Object> map = Collections.singletonMap("extraKey", "");
              Attrs.populate(new Clazz(), map, mapper::fromName);
            });

    Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> {
              Map<String, Object> map =
                  Collections.singletonMap(
                      "attrObject", Collections.singletonMap("extraKey", "invalid-value"));
              Attrs.populate(new Clazz(), map, mapper::fromName);
            });

    for (Object value : Arrays.asList(true, false, null)) {
      checkValueLegal(mapper, new Clazz(), "attrBoolean", value);
    }
    for (Object value : Arrays.asList(10, "true", 10d, new Object[0], new ArrayList<>())) {
      checkValueIllegal(mapper, new Clazz(), "attrBoolean", (c) -> c.attrBoolean.get(), value);
    }

    for (Object value : Arrays.asList(10, 10f, 10d, null)) {
      checkValueLegal(mapper, new Clazz(), "attrInteger", value);
    }
    for (Object value :
        Arrays.asList(
            true,
            "true",
            10.1f,
            10.1d,
            Long.MAX_VALUE,
            Double.NaN,
            new Object[0],
            new ArrayList<>())) {
      checkValueIllegal(mapper, new Clazz(), "attrInteger", (c) -> c.attrInteger.get(), value);
    }

    for (Object value : Arrays.asList(10, 10f, 10d, null, Double.NaN, Double.MAX_VALUE)) {
      checkValueLegal(mapper, new Clazz(), "attrDouble", value);
    }
    for (Object value : Arrays.asList(true, "true", new Object[0], new ArrayList<>())) {
      checkValueIllegal(mapper, new Clazz(), "attrDouble", (c) -> c.attrDouble.get(), value);
    }

    for (Object value : Arrays.asList(null, "abc", "")) {
      checkValueLegal(mapper, new Clazz(), "attrString", value);
    }
    for (Object value : Arrays.asList(true, 10, 10d, new Object[0], new ArrayList<>())) {
      checkValueIllegal(mapper, new Clazz(), "attrString", (c) -> c.attrString.get(), value);
    }

    for (Object value : Arrays.asList(null, EnumClazz.FOO, EnumClazz.FOO.name())) {
      checkValueLegal(mapper, new Clazz(), "attrEnum", value);
    }
    for (Object value :
        Arrays.asList(true, 10, 10d, "NONVALUE", new Object[0], new ArrayList<>())) {
      checkValueIllegal(mapper, new Clazz(), "attrEnum", (c) -> c.attrEnum.get(), value);
    }

    for (Object value : Arrays.asList(null, new HashMap<>())) {
      checkValueLegal(mapper, new Clazz(), "attrObject", value);
    }
    for (Object value :
        Arrays.asList(
            true,
            10,
            10d,
            "NONVALUE",
            Collections.singletonMap("attrInteger", "invalid-value"),
            new Object(),
            new Object[0],
            new ArrayList<>())) {
      checkValueIllegal(mapper, new Clazz(), "attrObject", (c) -> c.attrObject.get(), value);
    }

    for (Object value :
        Arrays.asList(
            null,
            new ArrayList<>(),
            new Object[] {Collections.emptyMap(), Collections.singletonMap("attrString", "foo")})) {
      checkValueLegal(mapper, new Clazz(), "attrObjectArray", value);
    }
    for (Object value :
        Arrays.asList(true, 10, 10d, new Object(), new Object[] {"invalid-value"})) {
      checkValueIllegal(
          mapper, new Clazz(), "attrObjectArray", (c) -> c.attrObjectArray.get(), value);
    }
  }

  private <T extends AcceptingVisitor, E> void checkValueIllegal(
      AliasMapper mapper, T instance, String key, Function<T, E> reader, E value) {
    E previously = reader.apply(instance);

    Throwable x;
    try {
      Attrs.populate(instance, Collections.singletonMap(key, value), mapper::fromName);
      throw new RuntimeException("Expected an invalid value for: " + value);
    } catch (Throwable t) {
      x = t;
    }

    System.out.println(x.toString());

    Assertions.assertThat(x)
        .as("illegal value check: '" + value + "'")
        .isExactlyInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Value at key ");

    Assertions.assertThat(reader.apply(instance)).isSameAs(previously);
  }

  private <T extends AcceptingVisitor, E> void checkValueLegal(
      AliasMapper mapper, T instance, String key, E value) {
    Assertions.assertThatCode(
            () -> {
              Attrs.populate(instance, Collections.singletonMap(key, value), mapper::fromName);
            })
        .doesNotThrowAnyException();
  }
}
