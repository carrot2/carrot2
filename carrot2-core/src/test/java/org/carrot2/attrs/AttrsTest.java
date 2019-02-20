package org.carrot2.attrs;

import org.assertj.core.api.Assertions;
import org.carrot2.TestBase;
import org.junit.Test;

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
}

