package org.carrot2.attrs;

import org.assertj.core.api.Assertions;
import org.carrot2.TestBase;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AttrsTest extends TestBase {
  @Test
  public void testClassNameMapper() {
    class Clazz extends AttrComposite {
    }

    AliasMapper mapper = new AliasMapper();
    mapper.alias("clazz", Clazz.class, () -> new Clazz());

    Clazz ob = new Clazz();

    Assertions.assertThat(Attrs.toMap(ob, mapper::toName))
        .containsEntry(Attrs.KEY_TYPE, "clazz");

    Assertions.assertThat(Attrs.fromMap(Clazz.class, Attrs.toMap(ob, mapper::toName), mapper::fromName))
        .isNotNull();
  }

  @Test
  public void testCustomDictionary() {
    class Entry implements AcceptingVisitor {
      String match;
      String [] tokens;

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

    class AdHocDict extends AttrComposite {
      List<Entry> entries;
      {
        attributes.register("entries", AttrObjectArray.builder(Entry.class, () -> new Entry())
            .getset(() -> entries, (list) -> entries = list)
            .defaultValue(null));
      }
    }

    class Clazz extends AttrComposite {
      AdHocDict adHocDict = new AdHocDict();
      {
        attributes.register("adhoc", AttrObject.builder(AdHocDict.class)
            .getset(() -> adHocDict, (v) -> adHocDict = v)
            .defaultValue(() -> new AdHocDict()));
      }
    }

    Entry entry = new Entry();
    entry.match = "e";
    entry.tokens = new String [] {"foo", "bar"};

    Clazz ob = new Clazz();
    ob.adHocDict.entries = Arrays.asList(entry, entry);
    System.out.println(Attrs.toPrettyString(ob, JvmNameMapper.INSTANCE));
  }
}

