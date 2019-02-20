package org.carrot2.attrs;

import org.assertj.core.api.Assertions;
import org.carrot2.AbstractTest;
import org.junit.Test;

import java.util.Map;

public class AttrsTest extends AbstractTest {
  public interface Interface extends AcceptingVisitor {
  }

  public static class InterfaceImpl1 implements Interface {
    protected AttrGroup group = new AttrGroup();

    public AttrInteger attrInt = group.register(
        "attrInt", AttrInteger.builder()
            .defaultValue(null));

    @Override
    public void accept(AttrVisitor visitor) {
      group.visit(visitor);
    }
  }

  public static class InterfaceImpl2 extends AttrComposite implements Interface {
    public AttrString attrString = attributes.register(
        "attrString", AttrString.builder()
            .defaultValue("baz"));
  }

  public static class InterfaceImpl1Sub extends InterfaceImpl1 {
    public AttrString attrString = group.register(
        "attrString", AttrString.builder()
            .defaultValue("sub"));
  }

  public enum EnumClass {
    VALUE1,
    VALUE2,
    VALUE3;
  }

  @Test
  public void testExtractAndRestore() {

    class Component implements AcceptingVisitor {
      private AttrGroup attrs = new AttrGroup();

      public AttrBoolean attrBoolean = attrs.register(
          "attrBool", AttrBoolean.builder()
              .defaultValue(true));

      public AttrBoolean attrBooleanNoValue = attrs.register(
          "attrBoolNoValue", AttrBoolean.builder()
              .defaultValue(null));

      public AttrInteger attrInt = attrs.register(
          "attrInt", AttrInteger.builder()
              .defaultValue(10));

      public AttrInteger attrIntNoValue = attrs.register(
          "attrIntNoValue", AttrInteger.builder()
              .defaultValue(null));

      public AttrDouble attrDouble = attrs.register(
          "attrDouble", AttrDouble.builder()
              .defaultValue(36.6));

      public AttrDouble attrDoubleNoValue = attrs.register(
          "attrDoubleNoValue", AttrDouble.builder()
              .defaultValue(null));

      public AttrString attrString = attrs.register(
          "attrString", AttrString.builder()
              .defaultValue("foo"));

      public AttrString attrStringNoValue = attrs.register(
          "attrStringNoValue", AttrString.builder()
              .defaultValue(null));

      public AttrObject<Interface> attrObject = attrs.register(
          "attrObject",
          AttrObject.builder(Interface.class)
              .defaultValue(() -> new InterfaceImpl1()));

      public AttrEnum<EnumClass> attrEnum = attrs.register(
          "attrEnum",
          AttrEnum.builder(EnumClass.class)
              .defaultValue(EnumClass.VALUE1));

      public AttrEnum<EnumClass> attrEnumNoValue = attrs.register(
          "attrEnumNoValue",
          AttrEnum.builder(EnumClass.class)
              .defaultValue(null));

      public final AttrStringArray attrStringArray =
          attrs.register("attrStringArray", AttrStringArray.builder()
              .defaultValue("foo", "bar", "baz"));

      public final AttrStringArray attrStringArrayNoValue =
          attrs.register("attrStringArrayNoValue", AttrStringArray.builder()
              .defaultValue(null));

      public AttrObject<InterfaceImpl1> attrConstantImpl = attrs.register(
          "attrConstantImpl",
          AttrObject.builder(InterfaceImpl1.class)
              .defaultValue(() -> new InterfaceImpl1()));

      public AttrObject<InterfaceImpl1> attrConstantImplSubclass = attrs.register(
          "attrConstantImplSubclass",
          AttrObject.builder(InterfaceImpl1.class)
              .defaultValue(() -> new InterfaceImpl1Sub()));

      public AttrObject<Interface> attrConstantInterface = attrs.register(
          "attrConstantInterface",
          AttrObject.builder(Interface.class)
              .defaultValue(() -> new InterfaceImpl2()));

      public InterfaceImpl1 constantImpl = new InterfaceImpl1();

      {
        attrs.register("constantImpl", AttrObject.builder(InterfaceImpl1.class)
            .getset(() -> constantImpl, (v) -> constantImpl = v)
            .defaultValue(() -> new InterfaceImpl1()));
      }

      @Override
      public void accept(AttrVisitor visitor) {
        attrs.visit(visitor);
      }
    }

    AliasMapper mapper = new AliasMapper();
    mapper.alias("component", Component.class, () -> new Component());
    mapper.alias("impl1", InterfaceImpl1.class, () -> new InterfaceImpl1());
    mapper.alias("impl2", InterfaceImpl2.class, () -> new InterfaceImpl2());
    mapper.alias("impl1-sub", InterfaceImpl1Sub.class, () -> new InterfaceImpl1Sub());

    Component c1 = new Component();
    c1.attrInt.set(c1.attrInt.get() + 1);
    c1.attrDouble.set(Math.PI);
    c1.attrObject.set(new InterfaceImpl1(),
        (impl) -> impl.attrInt.set(42));
    c1.attrEnum.set(EnumClass.VALUE2);

    c1.constantImpl.attrInt.set(42);
    System.out.println(Attrs.toPrettyString(c1));

    Component c2 = restore(Component.class, extract(c1, mapper), mapper);
    Assertions.assertThat(c2.attrInt.get()).isEqualTo(c1.attrInt.get());
    Assertions.assertThat(c2.constantImpl.attrInt.get()).isEqualTo(42);
  }

  public static <E extends AcceptingVisitor> E restore(Class<? extends E> clazz,
                                                       Map<String, Object> attrs,
                                                       ClassNameMapper mapper) {
    return Attrs.fromMap(clazz, attrs, mapper::fromName);
  }

  private static Map<String, Object> extract(AcceptingVisitor ob, ClassNameMapper mapper) {
    return Attrs.toMap(ob, mapper::toName);
  }
}

