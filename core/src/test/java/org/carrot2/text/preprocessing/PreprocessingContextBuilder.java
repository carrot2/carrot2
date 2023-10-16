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
package org.carrot2.text.preprocessing;

import java.util.ArrayList;
import org.carrot2.clustering.CachedLangComponents;
import org.carrot2.clustering.Document;
import org.carrot2.clustering.FieldMapDocument;
import org.carrot2.language.LanguageComponents;

/** Preprocessing context builder for tests. */
class PreprocessingContextBuilder {
  private final LanguageComponents languageComponents;
  private ArrayList<Document> documents = new ArrayList<>();

  public PreprocessingContextBuilder(LanguageComponents languageComponents) {
    this.languageComponents = languageComponents;
  }

  PreprocessingContextBuilder() {
    this(CachedLangComponents.loadCached("English"));
  }

  public PreprocessingContext buildContext(ContextPreprocessor pipeline) {
    return pipeline.preprocess(documents.stream(), null, languageComponents);
  }

  public PreprocessingContextAssert buildContextAssert(ContextPreprocessor pipeline) {
    return PreprocessingContextAssert.assertThat(buildContext(pipeline));
  }

  public static final class FieldValue {
    String field;
    String value;

    public FieldValue(String field, String value) {
      this.field = field;
      this.value = value;
    }

    public static FieldValue fv(String fieldName, String value) {
      return new FieldValue(fieldName, value);
    }
  }

  public PreprocessingContextBuilder newDoc(String title) {
    return newDoc(title, null);
  }

  public PreprocessingContextBuilder newDoc(String title, String summary) {
    FieldMapDocument doc = new FieldMapDocument();
    doc.addField("title", title);
    doc.addField("summary", summary);
    documents.add(doc);
    return this;
  }

  public PreprocessingContextBuilder newDoc(FieldValue... fields) {
    FieldMapDocument doc = new FieldMapDocument();
    for (FieldValue fv : fields) {
      doc.addField(fv.field, fv.value);
    }
    documents.add(doc);
    return this;
  }
}
