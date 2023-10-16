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

import com.carrotsearch.jsondoclet.model.ClassDocs;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.UnknownElementException;
import javax.lang.model.util.SimpleElementVisitor9;

public class ClassDocsVisitor extends SimpleElementVisitor9<ClassDocsVisitor, Context> {
  @Override
  public ClassDocsVisitor visitType(TypeElement e, Context context) {
    if (!context.classDocs.containsKey(e)) {
      ClassDocs classDocs = new ClassDocs();
      classDocs.type = e.getQualifiedName().toString();
      classDocs.javadoc = JavaDocsVisitor.extractFrom(e, context);
      context.classDocs.put(e, classDocs);

      e.getEnclosedElements().stream()
          .filter(element -> element.getKind().isField())
          .forEach(
              element -> {
                element.accept(new FieldDocsVisitor(classDocs.fields), context);
              });
    }
    return this;
  }

  @Override
  protected ClassDocsVisitor defaultAction(Element e, Context context) {
    throw new UnknownElementException(e, "Unknown element visited: " + e.getKind());
  }
}
