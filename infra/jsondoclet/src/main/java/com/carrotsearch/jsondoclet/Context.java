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
import com.sun.source.util.DocTrees;
import java.util.HashMap;
import java.util.Map;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;

public class Context {
  public Reporter reporter;
  public DocTrees docTrees;
  public Elements elements;
  public DocletEnvironment env;

  public ReferenceConverter referenceConverter;

  public Map<Element, ClassDocs> classDocs = new HashMap<>();
}
