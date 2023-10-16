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

import com.carrotsearch.randomizedtesting.LifecycleScope;
import com.carrotsearch.randomizedtesting.RandomizedTest;
import com.carrotsearch.randomizedtesting.annotations.TestCaseOrdering;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.tools.DocumentationTool;
import javax.tools.DocumentationTool.DocumentationTask;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import org.assertj.core.api.Assertions;
import org.carrot2.TestBase;
import org.junit.Test;

@TestCaseOrdering(TestCaseOrdering.AlphabeticOrder.class)
public class TestJsonDoclet extends TestBase {
  @Test
  public void testSample01() throws IOException {
    Map<String, String> files = checkSample("Sample01");
    Assertions.assertThat(files).containsOnlyKeys("com.carrotsearch.jsondoclet.Sample01.json");
  }

  @Test
  public void testSample02() throws IOException {
    checkSample("Sample02");
  }

  @Test
  public void testSample03_classLink() throws IOException {
    checkSample("Sample03");
  }

  @Test
  public void testSample04_fieldLink() throws IOException {
    checkSample("Sample04");
  }

  @Test
  public void testSample05_interfaceLink() throws IOException {
    checkSample("Sample05");
  }

  @Test
  public void testSample06_method() throws IOException {
    checkSample("Sample06");
  }

  @Test
  public void testSample07_linkplain() throws IOException {
    checkSample("Sample07");
  }

  @Test
  public void testSample08_linklabel() throws IOException {
    checkSample("Sample08");
  }

  @Test
  public void testSample09_code() throws IOException {
    checkSample("Sample09");
  }

  private Map<String, String> checkSample(String name) throws IOException {
    Map<String, String> files = process(name + ".java");
    Assertions.assertThat(files.get("com.carrotsearch.jsondoclet." + name + ".json"))
        .isEqualToIgnoringWhitespace(resourceString(name + ".json"));
    return files;
  }

  private Map<String, String> process(String... resources) throws IOException {
    DocumentationTool javadoc = ToolProvider.getSystemDocumentationTool();

    Path output = RandomizedTest.newTempDir(LifecycleScope.TEST);
    Path classes = Files.createDirectory(output.resolve("classes"));
    Path docs = Files.createDirectory(output.resolve("docs"));

    try (StandardJavaFileManager fm =
        javadoc.getStandardFileManager(null, Locale.ROOT, StandardCharsets.UTF_8)) {
      fm.setLocation(StandardLocation.CLASS_OUTPUT, List.of(classes.toFile()));
      fm.setLocation(StandardLocation.CLASS_PATH, Collections.emptyList());

      List<SimpleJavaFileObject> compilationUnits = javaFiles(resources);

      try (StringWriter sw = new StringWriter();
          PrintWriter pw = new PrintWriter(sw)) {

        Iterable<String> options = Arrays.asList("-d", docs.toAbsolutePath().toString());

        DocumentationTask t =
            javadoc.getTask(pw, fm, null, JsonDoclet.class, options, compilationUnits);

        pw.flush();

        boolean ok = t.call();
        System.out.println(sw.toString());
        Assertions.assertThat(ok).isTrue();

        try (Stream<Path> s = Files.list(docs)) {
          return s.collect(
              Collectors.toMap(
                  e -> e.getFileName().toString(),
                  e -> {
                    try {
                      return Files.readString(e, StandardCharsets.UTF_8);
                    } catch (IOException ex) {
                      throw new UncheckedIOException(ex);
                    }
                  }));
        }
      }
    }
  }

  private List<SimpleJavaFileObject> javaFiles(String... resources) {
    return Arrays.stream(resources)
        .map(resource -> new JavaSourceAsResource(resource, resourceString(resource)))
        .collect(Collectors.toList());
  }

  private static class JavaSourceAsResource extends SimpleJavaFileObject {
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
