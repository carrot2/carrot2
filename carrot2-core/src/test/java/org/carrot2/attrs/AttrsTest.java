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
    class Entry extends AttrComposite {
      AttrString match = attributes.register("match", AttrString.builder().defaultValue(null));
      AttrStringArray tokens = attributes.register("tokens", AttrStringArray.builder().defaultValue(null));
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
    entry.match.set("e");
    entry.tokens.set("foo", "bar");

    Clazz ob = new Clazz();
    ob.adHocDict.entries = Arrays.asList(entry, entry);
    System.out.println(Attrs.toPrettyString(ob, JvmNameMapper.INSTANCE));
  }
}

