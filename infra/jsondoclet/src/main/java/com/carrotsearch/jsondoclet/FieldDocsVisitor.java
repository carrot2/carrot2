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
package com.carrotsearch.jsondoclet;

import com.carrotsearch.jsondoclet.model.FieldDocs;
import java.util.Map;
import javax.lang.model.element.Element;
import javax.lang.model.element.UnknownElementException;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.SimpleElementVisitor9;

public class FieldDocsVisitor extends SimpleElementVisitor9<FieldDocsVisitor, Context> {
  private final Map<String, FieldDocs> fields;

  public FieldDocsVisitor(Map<String, FieldDocs> fields) {
    this.fields = fields;
  }

  @Override
  public FieldDocsVisitor visitVariable(VariableElement e, Context context) {
    if (context.env.isIncluded(e)) {
      FieldDocs fieldDocs = new FieldDocs();
      fieldDocs.type = e.asType().toString();
      fieldDocs.javadoc = JavaDocsVisitor.extractFrom(e, context);
      fields.put(e.getSimpleName().toString(), fieldDocs);
    }
    return this;
  }

  @Override
  protected FieldDocsVisitor defaultAction(Element e, Context context) {
    throw new UnknownElementException(e, "Unknown element visited: " + e.getKind());
  }
}
