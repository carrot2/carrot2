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

import com.carrotsearch.randomizedtesting.LifecycleScope;
import com.carrotsearch.randomizedtesting.RandomizedTest;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import javax.tools.DocumentationTool;
import javax.tools.DocumentationTool.DocumentationTask;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import org.carrot2.TestBase;
import org.junit.Test;

public class TestDoclet extends TestBase {
  @Test
  public void testSample1() throws IOException {
    DocumentationTool javadoc = ToolProvider.getSystemDocumentationTool();

    Path output = RandomizedTest.newTempDir(LifecycleScope.TEST);
    Path classes = Files.createDirectory(output.resolve("classes"));
    Path docs = Files.createDirectory(output.resolve("docs"));

    try (StandardJavaFileManager fm =
        javadoc.getStandardFileManager(null, Locale.ROOT, StandardCharsets.UTF_8)) {
      fm.setLocation(StandardLocation.CLASS_OUTPUT, List.of(classes.toFile()));
      fm.setLocation(StandardLocation.CLASS_PATH, Collections.emptyList());

      List<SimpleJavaFileObject> compilationUnits = javaFiles("Sample1.java", "Sample2.java");

      try (StringWriter sw = new StringWriter();
          PrintWriter pw = new PrintWriter(sw)) {
        Iterable<String> options = Arrays.asList("-d", docs.toAbsolutePath().toString());
        Class<?> docletClass = JsonDoclet.class;
        DocumentationTask t = javadoc.getTask(pw, fm, null, docletClass, options, compilationUnits);

        boolean ok = t.call();

        pw.flush();
        String out = sw.toString().replaceAll("[\r\n]+", "\n");
        System.out.println(out);

        if (!ok) {
          throw new RuntimeException("There have been processing errors.");
        }
      }
    }
  }

  private List<SimpleJavaFileObject> javaFiles(String... resources) {
    return Arrays.stream(resources)
        .map(resource -> new JavaSourceAsResource(resource, resourceString(resource)))
        .collect(Collectors.toList());
  }

  static class JavaSourceAsResource extends SimpleJavaFileObject {
    private final String content;

    JavaSourceAsResource(String fileName, String content) {
      super(URI.create(fileName), JavaFileObject.Kind.SOURCE);
      this.content = content;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncoding) {
      return content;
    }
  }
}
