package org.carrot2.clustering;

import java.util.function.BiConsumer;

public class TestDocument implements Document {
  private String title;
  private String snippet;

  public TestDocument(String title) {
    this.title = title;
  }

  public TestDocument(String title, String snippet) {
    this.title = title;
    this.snippet = snippet;
  }

  @Override
  public void visitFields(BiConsumer<String, String> fieldConsumer) {
    if (title != null) {
      fieldConsumer.accept("title", title);
    }
    if (snippet != null) {
      fieldConsumer.accept("snippet", snippet);
    }
  }
}
