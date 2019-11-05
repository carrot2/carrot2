/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package com.carrotsearch.jsondoclet;

import com.carrotsearch.jsondoclet.OptionImpl.SingleArgumentOption;
import com.sun.source.doctree.DocCommentTree;
import com.sun.source.util.DocTrees;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleElementVisitor9;
import javax.tools.Diagnostic.Kind;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;

public class JsonDoclet implements Doclet {
  private Reporter reporter;

  private SingleArgumentOption<Path> outputDirectoryOption =
      OptionImpl.pathOption("Output directory", List.of("-d"));

  private Set<Option> options = Set.of(outputDirectoryOption);

  @Override
  public void init(Locale locale, Reporter reporter) {
    this.reporter = reporter;
  }

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }

  @Override
  public Set<? extends Option> getSupportedOptions() {
    return options;
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latest();
  }

  @Override
  public boolean run(DocletEnvironment environment) {
    if (!outputDirectoryOption.hasValue()) {
      reporter.print(
          Kind.ERROR,
          "Output directory option is required (" + outputDirectoryOption.getNames() + ")");
      return false;
    }

    Elements elementUtils = environment.getElementUtils();
    DocTrees docTrees = environment.getDocTrees();

    for (Element element : environment.getIncludedElements()) {
      System.out.println("-- " + element.getSimpleName());
      ElementVisitor<Void, Void> visitor =
          new SimpleElementVisitor9<>() {
            int nesting = 0;

            @Override
            protected Void defaultAction(Element e, Void aVoid) {
              if (isIncluded(e)) {
                print(e);
                nesting++;
                e.getEnclosedElements().forEach(child -> child.accept(this, aVoid));
                nesting--;
              }
              return null;
            }

            private void print(Element e) {
              System.out.format(
                  Locale.ROOT,
                  "%" + (nesting + 1) + "s %s, %s\n",
                  "",
                  e.getSimpleName(),
                  e.getKind());

              if (e.getKind().isField()) {
                System.out.println("# " + e.asType().toString());
              }

              DocCommentTree docCommentTree = docTrees.getDocCommentTree(e);
              if (docCommentTree != null) {}
            }

            private boolean isIncluded(Element e) {
              return environment.isIncluded(e);
            }
          };
      element.accept(visitor, null);
    }

    return true;
  }
}
