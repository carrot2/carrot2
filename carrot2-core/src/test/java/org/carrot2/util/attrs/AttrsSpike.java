package org.carrot2.util.attrs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class AttrsSpike {

  public interface Visitable {
    public void visit(AttrVisitor visitor);
  }

  public static class IntegerAttr {
    private Integer value;

    public IntegerAttr(Integer value) {
      this.value = value;
    }

    public void set(int value) {
      this.value = value;
    }

    public Integer get() {
      return value;
    }

    public static class Builder {
      private Integer defaultValue;

      public Builder defaultValue(int value) {
        defaultValue = value;
        return this;
      }


      public IntegerAttr build() {
        return new IntegerAttr(defaultValue);
      }
    }

    public static Builder builder() {
      return new Builder();
    }
  }

  public static class ImplAttr<T extends Visitable> {
    private List<Alias<T>> aliases;
    private T value;
    private Class<T> clazz;

    public ImplAttr(Class<T> clazz, T value, List<Alias<T>> aliases) {
      this.value = value;
      this.clazz = clazz;
      this.aliases = aliases;
    }

    public <E extends T> E set(E value) {
      this.value = value;
      return value;
    }

    public T get() {
      return value;
    }

    public String toClassAlias() {
      Objects.requireNonNull(value);
      return aliases.stream()
          .filter(alias -> alias.isInstanceOf.test(value))
          .map(alias -> alias.name)
          .findFirst()
          .orElse(value.getClass().getName());
    }

    public void setFromClassAlias(String name) {
      set(aliases.stream()
          .filter(alias -> Objects.equals(alias.name, name))
          .map(alias -> alias.supplier)
          .findFirst()
          .orElse(() -> { throw new RuntimeException(); } )
          .get());
    }

    public static class Alias<T> {
      private final String name;
      private final Predicate<T> isInstanceOf;
      private final Supplier<T> supplier;

      public Alias(String name, Predicate<T> isInstanceOf, Supplier<T> supplier) {
        this.name = name;
        this.supplier = supplier;
        this.isInstanceOf = isInstanceOf;
      }
    }

    public static class Builder<T extends Visitable> {
      private Class<T> clazz;
      private T defaultValue;
      private List<Alias> aliases = new ArrayList<>();

      public Builder(Class<T> clazz) {
        this.clazz = clazz;
      }

      public Builder<T> defaultValue(T value) {
        defaultValue = value;
        return this;
      }

      public Builder<T> implAlias(String alias, Class<? extends T> clazz, Supplier<T> supplier) {
        aliases.add(new Alias<T>(alias, (ob) -> clazz.isInstance(ob), supplier));
        return this;
      }

      public ImplAttr<T> build() {
        return new ImplAttr(clazz, defaultValue, aliases);
      }
    }

    public static <T extends Visitable> Builder<T> builder(Class<T> clazz) {
      return new Builder(clazz);
    }
  }

  public static interface IFace extends Visitable {}
  public static class IFaceImplA implements IFace {
    public IntegerAttr attrInt = IntegerAttr.builder()
        .defaultValue(20)
        .build();

    @Override
    public void visit(AttrVisitor visitor) {
      visitor.visit("attr2", attrInt);
    }
  }

  public static class IFaceImplB implements IFace {
    public IntegerAttr foo = IntegerAttr.builder().build();

    @Override
    public void visit(AttrVisitor visitor) {
      visitor.visit("foo", foo);
    }
  }

  public static class Algorithm implements Visitable {
    public IntegerAttr attrInt = IntegerAttr.builder()
        .defaultValue(10)
        .build();

    public ImplAttr<IFace> implAttr = ImplAttr.builder(IFace.class)
        .implAlias("impl-a", IFaceImplA.class, () -> new IFaceImplA())
        .implAlias("impl-b", IFaceImplB.class, () -> new IFaceImplB())
        .defaultValue(new IFaceImplA())
        .build();

    public ImplAttr<IFace> implAttr2 = ImplAttr.builder(IFace.class)
        .implAlias("impl-a", IFaceImplA.class, () -> new IFaceImplA())
        .implAlias("impl-b", IFaceImplB.class, () -> new IFaceImplB())
        .build();

    public void visit(AttrVisitor visitor) {
      visitor.visit("attrInt", attrInt);
      visitor.visit("implAttr", implAttr);
      visitor.visit("implAttr2", implAttr2);
    }
  }

  public interface AttrVisitor {
    void visit(String key, IntegerAttr attrInt);
    void visit(String key, ImplAttr<?> attrImpl);
  }

  public static class ToMapVisitor implements AttrVisitor {
    private final Map<String, Object> map;

    public ToMapVisitor(Map<String, Object> map) {
      this.map = map;
    }

    @Override
    public void visit(String key, IntegerAttr attrInt) {
      map.put(key, attrInt.value);
    }

    @Override
    public void visit(String key, ImplAttr<?> attrImpl) {
      Visitable o = attrImpl.get();
      if (o != null) {
        Map<String, Object> submap = new LinkedHashMap<>();
        submap.put("@type", attrImpl.toClassAlias());
        o.visit(new ToMapVisitor(submap));
        map.put(key, submap);
      } else {
        map.put(key, null);
      }
    }
  }

  public static class FromMapVisitor implements AttrVisitor {
    private final Map<String, Object> map;

    public FromMapVisitor(Map<String, Object> map) {
      this.map = map;
    }

    @Override
    public void visit(String key, IntegerAttr attrInt) {
      if (map.containsKey(key)) {
        attrInt.set((Integer) map.get(key));
      }
    }

    @Override
    public void visit(String key, ImplAttr<?> attrImpl) {
      if (map.containsKey(key)) {
        Map<String, Object> submap = Map.class.cast(map.get(key));
        attrImpl.setFromClassAlias((String) submap.get("@type"));
        attrImpl.get().visit(new FromMapVisitor(submap));
      }
    }
  }

  @Test
  public void testMe() throws Exception {
    Algorithm algorithm = new Algorithm();
    algorithm.attrInt.set(100);

    algorithm.implAttr2.set(new IFaceImplB())
        .foo.set(10);

    Map<String, Object> map = asMap(algorithm);
    dump(map);

    FromMapVisitor v2 = new FromMapVisitor(map);
    Algorithm algorithm2 = new Algorithm();
    algorithm2.visit(v2);

    dump(asMap(algorithm2));
  }

  private Map<String, Object> asMap(Algorithm algorithm) {
    Map<String, Object> map = new LinkedHashMap<>();
    ToMapVisitor visitor = new ToMapVisitor(map);
    algorithm.visit(visitor);
    return map;
  }

  private void dump(Object object) throws JsonProcessingException {
    ObjectMapper om = new ObjectMapper();
    System.out.println(om.writer().withDefaultPrettyPrinter().writeValueAsString(object));
  }
}
