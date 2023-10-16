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
package com.carrotsearch.lingo3g.it;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class BasicIT extends DistributionTestBase {
  @Test
  public void checkScriptsExecutableInZip() throws Exception {
    try (ZipFile zf = new ZipFile(getDistributionZip().toFile())) {
      final Enumeration<ZipArchiveEntry> entries = zf.getEntries();
      final Map<String, String> zipInfos = new TreeMap<>();

      while (entries.hasMoreElements()) {
        final ZipArchiveEntry ze = entries.nextElement();
        String path = ze.getName().replaceFirst("[^/]*/", "");
        if (zipInfos.containsKey(path)) {
          throw new AssertionError("Duplicate ZIP entry for: " + ze.getName());
        }
        zipInfos.put(path, String.format(Locale.ROOT, "%o", ze.getUnixMode() & 0777));
      }

      assertThat(zipInfos)
          .hasEntrySatisfying("dcs/dcs.cmd", value -> assertThat(value).isEqualTo("644"))
          .hasEntrySatisfying("dcs/dcs", value -> assertThat(value).isEqualTo("755"))
          .hasEntrySatisfying("examples/gradlew.bat", value -> assertThat(value).isEqualTo("644"))
          .hasEntrySatisfying("examples/gradlew", value -> assertThat(value).isEqualTo("755"))
          .hasEntrySatisfying("README.txt", value -> assertThat(value).isEqualTo("644"))
          .hasEntrySatisfying("carrot2.LICENSE", value -> assertThat(value).isEqualTo("644"));
    }
  }

  @Test
  public void checkNoDuplicateJars() throws Exception {
    Path base = getDistributionBasePath();

    var libJars = collectJars(base.resolve("dcs"));
    libJars.values().forEach((locations) -> assertThat(locations).hasSize(1));

    Assertions.assertThat(collectJars(base.resolve("examples")))
        .containsOnlyKeys("gradle-wrapper.jar");
  }

  @Test
  public void noKnownTestDependencies() throws Exception {
    Path base = getDistributionBasePath();

    var libJars = collectJars(base);
    for (String key : libJars.keySet()) {
      assertThat(key)
          .as(libJars.get(key).toString())
          .doesNotContain("randomizedtesting")
          .doesNotContain("junit")
          .doesNotContain("assertj");
    }
  }

  @Test
  public void checkLicenseFilesAttachedToEachJar() throws Exception {
    Path base = getDistributionBasePath();

    Set<String> PERMITTED_LICENSES =
        new HashSet<>(Arrays.asList("PUBLIC-DOMAIN", "ASL", "CDDL", "BSD", "MIT", "MPL"));

    // Collect *-LICENSE*.txt files.
    Pattern licenseFileName = Pattern.compile("(?<prefix>.+?)-LICENSE-(?<type>.+?)\\.txt");
    Map<String, List<Path>> prefixToLicense = new HashMap<>();
    try (Stream<Path> walk = Files.walk(base)) {
      walk.forEach(
          (path) -> {
            String name = path.getFileName().toString();
            Matcher matcher = licenseFileName.matcher(name);
            if (matcher.matches()) {
              String prefix = matcher.group("prefix");
              assertThat(prefix).isNotNull();
              assertThat(PERMITTED_LICENSES).contains(matcher.group("type"));
              prefixToLicense.computeIfAbsent(prefix, (k) -> new ArrayList<>()).add(path);
            }
          });
    }

    // Now scan all JARs and check that they have an exactly one
    // corresponding license.
    try (Stream<Path> walk = Files.walk(base)) {
      Set<Path> appliedLicenses = new HashSet<>();
      walk.filter(
              (p) -> {
                String fileName = p.getFileName().toString().toLowerCase(Locale.ROOT);
                return fileName.endsWith(".jar")
                    && !fileName.equals("gradle-wrapper.jar")
                    && !fileName.startsWith("dcs-launcher-")
                    && !fileName.startsWith("carrot2-");
              })
          .forEach(
              (p) -> {
                String fileName = p.getFileName().toString();
                List<Path> licenseCandidates =
                    prefixToLicense.entrySet().stream()
                        .filter((e) -> fileName.startsWith(e.getKey()))
                        .flatMap((e) -> e.getValue().stream().filter(path -> areSiblings(path, p)))
                        .collect(Collectors.toList());

                if (licenseCandidates.size() == 0) {
                  throw new AssertionError("No licenses covering JAR file: " + p);
                }
                if (licenseCandidates.size() > 1) {
                  throw new AssertionError(
                      "More than one license covers JAR file: " + p + ", " + licenseCandidates);
                }

                appliedLicenses.addAll(licenseCandidates);
              });

      // Make sure all licenses have been used.
      Set<Path> unusedLicenses =
          prefixToLicense.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
      unusedLicenses.removeAll(appliedLicenses);
      assertThat(unusedLicenses).as("Unused license files").isEmpty();
    }
  }

  private boolean areSiblings(Path p1, Path p2) {
    return p1.getParent().equals(p2.getParent());
  }

  private Map<String, List<Path>> collectJars(Path base) throws IOException {
    final Map<String, List<Path>> nameToPath = new HashMap<>();
    try (Stream<Path> walk = Files.walk(base)) {
      walk.forEach(
          (file) -> {
            String name = file.getFileName().toString();
            if (name.toLowerCase(Locale.ROOT).endsWith(".jar")) {
              nameToPath.computeIfAbsent(name, (k) -> new ArrayList<>()).add(file);
            }
          });
    }
    return nameToPath;
  }
}
