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
package org.carrot2.dcs.it;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.AssumptionViolatedException;
import org.junit.Test;

public class DistributionSanityTest extends AbstractDistributionTest {
  @Test
  public void checkRequiredFiles() {
    for (String file : Arrays.asList("carrot2.LICENSE", "dcs.cmd", "dcs"))
      Assertions.assertThat(getDistributionDir().resolve(file)).isRegularFile();
  }

  @Test
  public void checkShEols() throws IOException {
    try (Stream<Path> s = Files.walk(getDistributionDir())) {
      for (Path p : s.collect(Collectors.toList())) {
        if (p.getFileName().toString().endsWith(".sh")) {
          String content = Files.readString(p);
          if (content.indexOf('\r') >= 0) {
            throw new AssertionError(p + " contains carriage \\r?");
          }
        }
      }
    }
  }

  @Test
  public void checkFileMode() throws IOException {
    try (Stream<Path> s = Files.walk(getDistributionDir())) {
      for (Path p : s.collect(Collectors.toList())) {
        if (p.getFileName().toString().endsWith("dcs")) {
          try {
            Assertions.assertThat(Files.getPosixFilePermissions(p))
                .contains(PosixFilePermission.OWNER_EXECUTE);
          } catch (UnsupportedOperationException e) {
            throw new AssumptionViolatedException(
                "Expected a posix-compliant file system: " + p, e);
          }
        }
      }
    }
  }
}
