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
