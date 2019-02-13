package org.carrot2.util.attrs;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.fest.assertions.Assertions;
import org.junit.Test;

import java.util.Map;

public class AttrsTest {
  public interface Interface extends AcceptingVisitor {
  }

  public static class InterfaceImpl1 implements Interface {
    private AttrGroup group = new AttrGroup();

    public AttrInteger attrInt = group.register(
        "attrInt", AttrInteger.builder()
            .build());

    @Override
    public void accept(AttrVisitor visitor) {
      group.visit(visitor);
    }
  }

  @Test
  public void testExtractAndRestore() {
    InterfaceImpl1 defaultValue = new InterfaceImpl1();

    class Component implements AcceptingVisitor {
      private AttrGroup group = new AttrGroup();

      public AttrBoolean attrBoolean = group.register(
          "attrBool", AttrBoolean.builder()
              .defaultValue(true)
              .build());

      public AttrInteger attrInt = group.register(
          "attrInt", AttrInteger.builder()
              .defaultValue(10)
              .build());

      public AttrObject<Interface> attrObject = group.register(
          "attrObject",
          AttrObject.builder(Interface.class)
              .defaultValue(defaultValue)
              .build());

      public InterfaceImpl1 attrDirectImpl = group.register(
          "attrDirectImpl", new InterfaceImpl1());

      @Override
      public void accept(AttrVisitor visitor) {
        group.visit(visitor);
      }
    }

    AliasMapper mapper = new AliasMapper();
    mapper.alias("component", Component.class, () -> new Component());
    mapper.alias("impl1", InterfaceImpl1.class, () -> new InterfaceImpl1());

    Component c1 = new Component();
    c1.attrInt.set(c1.attrInt.get() + 1);
    c1.attrObject.set(new InterfaceImpl1(),
        (impl) -> impl.attrInt.set(42));
    c1.attrDirectImpl.attrInt.set(84);

    Component c2 = restore(Component.class, extract(c1, mapper), mapper);
    Assertions.assertThat(c2.attrInt.get()).isEqualTo(c1.attrInt.get());

    System.out.println(extract(c1, mapper));
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

