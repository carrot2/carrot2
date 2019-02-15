package org.carrot2.clustering;

import java.util.function.BiConsumer;

public class TestDocument implements Document {
  public String title;

  public TestDocument(String title) {
    this.title = title;
  }

  @Override
  public void visitFields(BiConsumer<String, String> fieldConsumer) {
    fieldConsumer.accept("title", title);
  }
}
