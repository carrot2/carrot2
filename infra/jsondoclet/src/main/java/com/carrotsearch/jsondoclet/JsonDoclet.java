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

import com.carrotsearch.jsondoclet.OptionImpl.SingleArgumentOption;
import com.carrotsearch.jsondoclet.model.ClassDocs;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.lang.model.SourceVersion;
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

    Path outputDirectory = outputDirectoryOption.getValue().toAbsolutePath().normalize();
    if (!Files.isDirectory(outputDirectory)) {
      reporter.print(Kind.ERROR, "Output directory does not exist: " + outputDirectory);
      return false;
    }

    Context context = new Context();
    context.reporter = reporter;
    context.docTrees = environment.getDocTrees();
    context.elements = environment.getElementUtils();
    context.env = environment;

    context.referenceConverter = new PlainReferenceConverter();

    environment.getIncludedElements().stream()
        .filter(element -> element.getKind().isClass())
        .forEach(element -> element.accept(new ClassDocsVisitor(), context));

    DefaultPrettyPrinter pp = new DefaultPrettyPrinter();
    pp.indentArraysWith(new DefaultIndenter("  ", DefaultIndenter.SYS_LF));

    ObjectMapper om =
        new ObjectMapper()
            .configure(SerializationFeature.INDENT_OUTPUT, true)
            .setDefaultPrettyPrinter(pp);

    for (ClassDocs classDocs : context.classDocs.values()) {
      Path output = outputDirectory.resolve(classDocs.type + ".json");
      try (Writer w = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {
        om.writeValue(w, classDocs);
      } catch (IOException e) {
        reporter.print(
            Kind.ERROR,
            "Could not extract or save documentation for type: "
                + classDocs.type
                + ", reason: "
                + e);
        return false;
      }
    }

    return true;
  }
}
